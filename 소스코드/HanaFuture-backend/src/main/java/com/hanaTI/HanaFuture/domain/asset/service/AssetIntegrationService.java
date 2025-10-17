package com.hanaTI.HanaFuture.domain.asset.service;

import com.hanaTI.HanaFuture.domain.asset.dto.*;
import com.hanaTI.HanaFuture.domain.asset.entity.*;
import com.hanaTI.HanaFuture.domain.asset.repository.*;
import com.hanaTI.HanaFuture.domain.banking.service.HanaBankApiClient;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.BusinessException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssetIntegrationService {

    private final ConnectedAssetRepository assetRepository;
    private final AssetTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final HanaBankApiClient hanaBankApiClient;

    /**
     * 사용자의 연결된 자산 목록 조회
     */
    public List<ConnectedAssetResponse> getUserAssets(Long userId) {
        User user = getUserById(userId);
        List<ConnectedAsset> assets = assetRepository.findByUserAndConnectionStatusOrderByCreatedAtDesc(
                user, AssetConnectionStatus.CONNECTED);
        
        return assets.stream()
                .map(this::convertToResponseWithTransactions)
                .collect(Collectors.toList());
    }

    /**
     * 자산 요약 정보 조회
     */
    public AssetSummaryResponse getAssetSummary(Long userId) {
        User user = getUserById(userId);
        
        // 기본 자산 정보
        BigDecimal totalAssets = assetRepository.getTotalAssets(user);
        BigDecimal totalInvestments = assetRepository.getTotalInvestments(user);
        BigDecimal totalLoans = assetRepository.getTotalLoans(user);
        BigDecimal totalCardUsage = assetRepository.getTotalCardUsage(user);
        BigDecimal netWorth = totalAssets.add(totalInvestments).subtract(totalLoans).subtract(totalCardUsage);

        // 연결 상태 통계
        long connectedCount = assetRepository.countByUserAndConnectionStatus(user, AssetConnectionStatus.CONNECTED);
        long disconnectedCount = assetRepository.countByUserAndConnectionStatus(user, AssetConnectionStatus.DISCONNECTED);
        long errorCount = assetRepository.countByUserAndConnectionStatus(user, AssetConnectionStatus.ERROR);

        // 동기화 필요한 자산 수
        LocalDateTime syncThreshold = LocalDateTime.now().minusHours(1);
        List<ConnectedAsset> assetsNeedingSync = assetRepository.findUserAssetsNeedingSync(user, syncThreshold);

        // 계좌 유형별 통계
        List<Object[]> accountTypeStatsData = assetRepository.getAssetStatsByType(user);
        List<AssetSummaryResponse.AccountTypeStat> accountTypeStats = accountTypeStatsData.stream()
                .map(data -> AssetSummaryResponse.AccountTypeStat.builder()
                        .accountType(((AssetAccountType) data[0]).name())
                        .accountTypeDescription(((AssetAccountType) data[0]).getDescription())
                        .count((Long) data[1])
                        .totalBalance((BigDecimal) data[2])
                        .build())
                .collect(Collectors.toList());

        // 은행별 통계
        List<Object[]> bankStatsData = assetRepository.getAssetStatsByBank(user);
        List<AssetSummaryResponse.BankStat> bankStats = bankStatsData.stream()
                .map(data -> AssetSummaryResponse.BankStat.builder()
                        .bankName((String) data[0])
                        .count((Long) data[1])
                        .totalBalance((BigDecimal) data[2])
                        .build())
                .collect(Collectors.toList());

        // 최근 거래내역
        List<AssetTransaction> recentTransactions = transactionRepository.findTop20ByUserIdOrderByTransactionDateDesc(userId);
        List<ConnectedAssetResponse.AssetTransactionSummary> transactionSummaries = recentTransactions.stream()
                .map(this::convertToTransactionSummary)
                .collect(Collectors.toList());

        // 투자 수익률 계산
        BigDecimal totalProfitLoss = BigDecimal.ZERO;
        BigDecimal totalProfitLossRate = BigDecimal.ZERO;
        List<ConnectedAsset> investmentAssets = assetRepository.findByUserAndAccountTypeAndConnectionStatus(
                user, AssetAccountType.INVESTMENT, AssetConnectionStatus.CONNECTED);
        
        if (!investmentAssets.isEmpty()) {
            totalProfitLoss = investmentAssets.stream()
                    .filter(asset -> asset.getProfitLoss() != null)
                    .map(ConnectedAsset::getProfitLoss)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 평균 수익률 계산 (간단화)
            totalProfitLossRate = investmentAssets.stream()
                    .filter(asset -> asset.getProfitLossRate() != null)
                    .map(ConnectedAsset::getProfitLossRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(investmentAssets.size()), 2, java.math.RoundingMode.HALF_UP);
        }

        // 최근 동기화 시간
        LocalDateTime lastSyncedAt = assetRepository.findByUserAndConnectionStatusOrderByCreatedAtDesc(
                user, AssetConnectionStatus.CONNECTED).stream()
                .map(ConnectedAsset::getLastSyncedAt)
                .filter(date -> date != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return AssetSummaryResponse.builder()
                .totalAssets(totalAssets)
                .totalInvestments(totalInvestments)
                .totalLoans(totalLoans)
                .totalCardUsage(totalCardUsage)
                .netWorth(netWorth)
                .totalInvestmentProfitLoss(totalProfitLoss)
                .totalInvestmentProfitLossRate(totalProfitLossRate)
                .connectedAccountsCount(connectedCount)
                .disconnectedAccountsCount(disconnectedCount)
                .errorAccountsCount(errorCount)
                .lastSyncedAt(lastSyncedAt)
                .accountsNeedingSync((long) assetsNeedingSync.size())
                .accountTypeStats(accountTypeStats)
                .bankStats(bankStats)
                .recentTransactions(transactionSummaries)
                .build();
    }

    /**
     * 새 계좌 연결
     */
    @Transactional
    public ConnectedAssetResponse connectAsset(ConnectAssetRequest request, Long userId) {
        User user = getUserById(userId);
        
        // 중복 계좌 확인
        if (assetRepository.existsByUserAndAccountNumberAndBankCode(
                user, request.getAccountNumber(), request.getBankCode())) {
            throw new BusinessException(ErrorCode.ASSET_ALREADY_CONNECTED);
        }

        // 하나은행 API를 통한 계좌 정보 조회
        Map<String, Object> accountResponse = hanaBankApiClient.getAccountBalance(request.getAccountNumber()).block();
        
        if (accountResponse == null || !(Boolean) accountResponse.get("success")) {
            throw new BusinessException(ErrorCode.ASSET_CONNECTION_FAILED);
        }
        
        // 하나은행 API 응답에서 계좌 정보 추출
        @SuppressWarnings("unchecked")
        Map<String, Object> accountData = (Map<String, Object>) accountResponse.get("data");
        BigDecimal balance = new BigDecimal(accountData.get("balanceAmt").toString());

        // 주 계좌 설정 처리
        if (request.getIsPrimary()) {
            // 기존 주 계좌 해제
            assetRepository.findByUserAndIsPrimaryTrue(user)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        assetRepository.save(existingPrimary);
                    });
        }

        // 연결된 자산 생성
        ConnectedAsset asset = ConnectedAsset.builder()
                .user(user)
                .accountNumber(request.getAccountNumber())
                .maskedAccountNumber(maskAccountNumber(request.getAccountNumber()))
                .bankCode(request.getBankCode())
                .bankName(request.getBankName())
                .accountName(request.getAccountName())
                .accountType(request.getAccountType())
                .balance(balance)
                .currency(request.getCurrency())
                .alias(request.getAlias())
                .isPrimary(request.getIsPrimary())
                .syncEnabled(request.getSyncEnabled())
                .notificationEnabled(request.getNotificationEnabled())
                .consentId("HANA_" + System.currentTimeMillis())
                .consentExpiresAt(LocalDateTime.now().plusMonths(6))
                .lastSyncedAt(LocalDateTime.now())
                .build();

        asset = assetRepository.save(asset);
        
        // 비동기로 거래내역 동기화
        syncTransactionsAsync(asset);
        
        log.info("자산 연결 완료: userId={}, assetId={}, bankCode={}, accountNumber={}", 
                userId, asset.getId(), request.getBankCode(), request.getAccountNumber());
        
        return convertToResponseWithTransactions(asset);
    }

    /**
     * 자산 동기화
     */
    @Transactional
    public void syncAsset(Long assetId, Long userId) {
        ConnectedAsset asset = getAssetById(assetId);
        validateAssetOwnership(asset, userId);
        
        if (asset.getConnectionStatus() != AssetConnectionStatus.CONNECTED) {
            throw new BusinessException(ErrorCode.ASSET_NOT_CONNECTED);
        }

        try {
            // 하나은행 API를 통한 계좌 정보 업데이트
            Map<String, Object> accountResponse = hanaBankApiClient.getAccountBalance(asset.getAccountNumber()).block();
            
            if (accountResponse != null && (Boolean) accountResponse.get("success")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> accountData = (Map<String, Object>) accountResponse.get("data");
                BigDecimal newBalance = new BigDecimal(accountData.get("balanceAmt").toString());
                
                asset.updateBalance(newBalance);
                
                // 거래내역 동기화
                syncTransactions(asset);
                
                assetRepository.save(asset);
                
                log.info("자산 동기화 완료: assetId={}, userId={}", assetId, userId);
            }
        } catch (Exception e) {
            asset.updateConnectionStatus(AssetConnectionStatus.ERROR);
            assetRepository.save(asset);
            log.error("자산 동기화 실패: assetId={}, userId={}", assetId, userId, e);
            throw new BusinessException(ErrorCode.ASSET_SYNC_FAILED);
        }
    }

    /**
     * 모든 자산 동기화
     */
    @Transactional
    public void syncAllAssets(Long userId) {
        User user = getUserById(userId);
        List<ConnectedAsset> assets = assetRepository.findByUserAndConnectionStatusOrderByCreatedAtDesc(
                user, AssetConnectionStatus.CONNECTED);
        
        for (ConnectedAsset asset : assets) {
            if (asset.getSyncEnabled()) {
                try {
                    syncAsset(asset.getId(), userId);
                } catch (Exception e) {
                    log.error("자산 동기화 실패 (전체 동기화 중): assetId={}, userId={}", asset.getId(), userId, e);
                    // 개별 자산 동기화 실패는 전체 동기화를 중단시키지 않음
                }
            }
        }
        
        log.info("전체 자산 동기화 완료: userId={}, 처리된 자산 수={}", userId, assets.size());
    }

    /**
     * 자산 설정 업데이트
     */
    @Transactional
    public ConnectedAssetResponse updateAssetSettings(Long assetId, UpdateAssetSettingsRequest request, Long userId) {
        ConnectedAsset asset = getAssetById(assetId);
        validateAssetOwnership(asset, userId);
        
        // 주 계좌 설정 처리
        if (request.getIsPrimary() != null && request.getIsPrimary()) {
            // 기존 주 계좌 해제
            assetRepository.findByUserAndIsPrimaryTrue(asset.getUser())
                    .ifPresent(existingPrimary -> {
                        if (!existingPrimary.getId().equals(assetId)) {
                            existingPrimary.setPrimary(false);
                            assetRepository.save(existingPrimary);
                        }
                    });
        }

        asset.updateSettings(
                request.getSyncEnabled(),
                request.getNotificationEnabled(),
                request.getAlias()
        );
        
        if (request.getIsPrimary() != null) {
            asset.setPrimary(request.getIsPrimary());
        }
        
        asset = assetRepository.save(asset);
        
        log.info("자산 설정 업데이트 완료: assetId={}, userId={}", assetId, userId);
        
        return convertToResponseWithTransactions(asset);
    }

    /**
     * 자산 연결 해제
     */
    @Transactional
    public void disconnectAsset(Long assetId, Long userId) {
        ConnectedAsset asset = getAssetById(assetId);
        validateAssetOwnership(asset, userId);
        
        // 하나은행 API 연결 해제 (필요시 구현)
        log.info("하나은행 계좌 연결 해제: accountNumber={}", asset.getAccountNumber());
        
        asset.updateConnectionStatus(AssetConnectionStatus.DISCONNECTED);
        assetRepository.save(asset);
        
        log.info("자산 연결 해제 완료: assetId={}, userId={}", assetId, userId);
    }

    /**
     * 거래내역 조회
     */
    public Page<ConnectedAssetResponse.AssetTransactionSummary> getAssetTransactions(
            Long assetId, Long userId, Pageable pageable) {
        ConnectedAsset asset = getAssetById(assetId);
        validateAssetOwnership(asset, userId);
        
        Page<AssetTransaction> transactions = transactionRepository.findByConnectedAssetOrderByTransactionDateDesc(
                asset, pageable);
        
        return transactions.map(this::convertToTransactionSummary);
    }

    // Private helper methods
    
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
    
    private ConnectedAsset getAssetById(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSET_NOT_FOUND));
    }
    
    private void validateAssetOwnership(ConnectedAsset asset, Long userId) {
        if (!asset.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
    
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        
        return accountNumber.substring(0, 4) + "****" + 
               accountNumber.substring(accountNumber.length() - 4);
    }
    
    @Async
    private void syncTransactionsAsync(ConnectedAsset asset) {
        try {
            syncTransactions(asset);
        } catch (Exception e) {
            log.error("비동기 거래내역 동기화 실패: assetId={}", asset.getId(), e);
        }
    }
    
    private void syncTransactions(ConnectedAsset asset) {
        log.info("거래내역 동기화 시작: assetId={}, accountNumber={}", 
                asset.getId(), asset.getAccountNumber());
        
        try {
            // 최근 30일간의 거래내역 조회
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = asset.getLastSyncedAt() != null ? 
                    asset.getLastSyncedAt().minusDays(1) : endDate.minusDays(30);
            
            // 하나은행 API 호출
            Map<String, Object> transactionResponse = hanaBankApiClient.getTransactionHistory(
                    asset.getAccountNumber(), 
                    startDate.toLocalDate().toString(), 
                    endDate.toLocalDate().toString()
            ).block();
            
            if (transactionResponse != null && (Boolean) transactionResponse.get("success")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> transactions = (List<Map<String, Object>>) transactionResponse.get("data");
                
                int syncedCount = 0;
                for (Map<String, Object> txnData : transactions) {
                    String transactionId = (String) txnData.get("transactionId");
                    
                    // 중복 거래 확인
                    if (!transactionRepository.existsByConnectedAssetAndTransactionId(asset, transactionId)) {
                        AssetTransaction transaction = AssetTransaction.builder()
                                .connectedAsset(asset)
                                .transactionId(transactionId)
                                .transactionType(mapTransactionType((String) txnData.get("type")))
                                .amount(new BigDecimal(txnData.get("amount").toString()))
                                .balanceAfter(new BigDecimal(txnData.get("balanceAfter").toString()))
                                .description((String) txnData.get("description"))
                                .counterparty((String) txnData.get("counterparty"))
                                .transactionDate(LocalDateTime.parse((String) txnData.get("transactionDate")))
                                .build();
                        
                        transactionRepository.save(transaction);
                        syncedCount++;
                    }
                }
                
                log.info("거래내역 동기화 완료: assetId={}, 새로운 거래 수={}", asset.getId(), syncedCount);
            }
        } catch (Exception e) {
            log.error("거래내역 동기화 실패: assetId={}", asset.getId(), e);
        }
    }
    
    private AssetTransactionType mapTransactionType(String bankTransactionType) {
        // 하나은행 거래 유형을 내부 enum으로 매핑
        return switch (bankTransactionType) {
            case "DEPOSIT", "입금" -> AssetTransactionType.DEPOSIT;
            case "WITHDRAWAL", "출금" -> AssetTransactionType.WITHDRAWAL;
            case "TRANSFER_IN", "이체입금" -> AssetTransactionType.TRANSFER_IN;
            case "TRANSFER_OUT", "이체출금" -> AssetTransactionType.TRANSFER_OUT;
            case "PAYMENT", "결제" -> AssetTransactionType.PAYMENT;
            case "INTEREST", "이자" -> AssetTransactionType.INTEREST;
            case "FEE", "수수료" -> AssetTransactionType.FEE;
            default -> AssetTransactionType.OTHER;
        };
    }
    
    private ConnectedAssetResponse convertToResponseWithTransactions(ConnectedAsset asset) {
        ConnectedAssetResponse response = ConnectedAssetResponse.from(asset);
        
        // 최근 거래내역 추가
        List<AssetTransaction> recentTransactions = 
                transactionRepository.findTop10ByConnectedAssetOrderByTransactionDateDesc(asset);
        List<ConnectedAssetResponse.AssetTransactionSummary> transactionSummaries = 
                recentTransactions.stream()
                        .map(this::convertToTransactionSummary)
                        .collect(Collectors.toList());
        response.setRecentTransactions(transactionSummaries);
        
        return response;
    }
    
    private ConnectedAssetResponse.AssetTransactionSummary convertToTransactionSummary(AssetTransaction transaction) {
        return ConnectedAssetResponse.AssetTransactionSummary.builder()
                .transactionId(transaction.getId())
                .externalTransactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .transactionTypeDescription(transaction.getTransactionType().getDescription())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .counterparty(transaction.getCounterparty())
                .status(transaction.getStatus())
                .statusDescription(transaction.getStatus().getDescription())
                .transactionDate(transaction.getTransactionDate())
                .category(transaction.getCategory())
                .memo(transaction.getMemo())
                .build();
    }
}
