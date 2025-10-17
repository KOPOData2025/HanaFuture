package com.hanaTI.HanaFuture.domain.user.service;

import com.hanaTI.HanaFuture.domain.banking.service.HanaBankApiClient;
import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import com.hanaTI.HanaFuture.domain.user.dto.LinkBankAccountRequest;
import com.hanaTI.HanaFuture.domain.user.dto.UserBankAccountResponse;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccount;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccountStatus;
import com.hanaTI.HanaFuture.domain.user.repository.UserBankAccountRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserBankAccountService {

    private final UserBankAccountRepository userBankAccountRepository;
    private final HanaBankApiClient hanaBankApiClient;
    private final HanaBankMockAccountRepository hanaBankMockAccountRepository;

    /**
     * 사용자의 연동된 계좌 목록 조회
     */
    public List<UserBankAccountResponse> getUserBankAccounts(User user) {
        List<UserBankAccount> accounts = userBankAccountRepository.findByUserOrderByIsPrimaryDescLinkedAtAsc(user);
        
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 출금 가능 계좌 목록 조회
     */
    public List<UserBankAccountResponse> getWithdrawableAccounts(User user) {
        List<UserBankAccount> accounts = userBankAccountRepository.findWithdrawableAccountsByUser(user);
        
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 하나은행 계좌 연동
     */
    @Transactional
    public UserBankAccountResponse linkBankAccount(User user, LinkBankAccountRequest request) {
        // 1. 계좌번호 중복 확인
        if (userBankAccountRepository.existsByUserAndAccountNumber(user, request.getAccountNumber())) {
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_LINKED);
        }

        // 2. 하나은행 API로 계좌 유효성 검증 및 정보 조회
        Map<String, Object> accountInfo = validateAndGetAccountInfo(request.getAccountNumber());

        // 3. 중복 계좌 확인
        if (userBankAccountRepository.existsByUserAndAccountNumber(user, request.getAccountNumber())) {
            log.warn("이미 등록된 계좌입니다: userId={}, accountNumber={}", user.getId(), request.getAccountNumber());
            throw new IllegalArgumentException("이미 등록된 계좌입니다.");
        }

        // 4. 계좌 연동 정보 저장
        UserBankAccount bankAccount = UserBankAccount.builder()
                .user(user)
                .bankCode("081") // 하나은행 코드
                .bankName("하나은행")
                .accountNumber(request.getAccountNumber())
                .accountName((String) accountInfo.get("productName"))
                .accountAlias(request.getAccountAlias())
                .accountType((String) accountInfo.get("accountType"))
                .balance((BigDecimal) accountInfo.get("balance"))
                .isPrimary(isFirstAccount(user) || request.getSetAsPrimary())
                .status(UserBankAccountStatus.ACTIVE)
                .autoSyncEnabled(request.getAutoSyncEnabled())
                .lastSyncedAt(LocalDateTime.now())
                .build();

        // 5. 주계좌 설정 시 기존 주계좌 해제
        if (bankAccount.getIsPrimary()) {
            unsetExistingPrimaryAccount(user);
        }

        UserBankAccount savedAccount = userBankAccountRepository.save(bankAccount);
        
        log.info("계좌 연동 완료: userId={}, accountNumber={}", user.getId(), request.getAccountNumber());
        
        return convertToResponse(savedAccount);
    }

    /**
     * 계좌 연동 해제
     */
    @Transactional
    public void unlinkBankAccount(User user, Long accountId) {
        UserBankAccount account = userBankAccountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 소유자 확인
        if (!account.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        // 주계좌인 경우 다른 계좌를 주계좌로 설정
        if (account.getIsPrimary()) {
            setNextPrimaryAccount(user, accountId);
        }

        account.updateStatus(UserBankAccountStatus.UNLINKED);
        userBankAccountRepository.save(account);
        
        log.info("계좌 연동 해제: userId={}, accountId={}", user.getId(), accountId);
    }

    /**
     * 주계좌 변경
     */
    @Transactional
    public void setPrimaryAccount(User user, Long accountId) {
        UserBankAccount account = userBankAccountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 소유자 확인
        if (!account.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        // 기존 주계좌 해제
        unsetExistingPrimaryAccount(user);

        // 새 주계좌 설정
        account.setPrimary();
        userBankAccountRepository.save(account);
        
        log.info("주계좌 변경: userId={}, accountId={}", user.getId(), accountId);
    }

    /**
     * 계좌 잔액 동기화
     */
    @Transactional
    public UserBankAccountResponse syncAccountBalance(User user, Long accountId) {
        UserBankAccount account = userBankAccountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 소유자 확인
        if (!account.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        try {
            // 하나은행 API로 최신 잔액 조회
            Map<String, Object> balanceInfo = hanaBankApiClient.getAccountBalance(account.getAccountNumber()).block();
            
            if (balanceInfo != null && (Boolean) balanceInfo.get("success")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) balanceInfo.get("data");
                BigDecimal newBalance = new BigDecimal(data.get("balanceAmt").toString());
                
                account.updateBalance(newBalance);
                userBankAccountRepository.save(account);
                
                log.info("계좌 잔액 동기화 완료: accountId={}, balance={}", accountId, newBalance);
            }
        } catch (Exception e) {
            log.error("계좌 잔액 동기화 실패: accountId={}", accountId, e);
            account.updateStatus(UserBankAccountStatus.SYNC_ERROR);
            userBankAccountRepository.save(account);
            throw new CustomException(ErrorCode.ACCOUNT_SYNC_FAILED);
        }

        return convertToResponse(account);
    }

    /**
     * 모든 계좌 잔액 동기화
     */
    @Transactional
    public List<UserBankAccountResponse> syncAllAccounts(User user) {
        List<UserBankAccount> activeAccounts = userBankAccountRepository.findByUserAndStatusOrderByIsPrimaryDescLinkedAtAsc(
                user, UserBankAccountStatus.ACTIVE);

        for (UserBankAccount account : activeAccounts) {
            try {
                syncAccountBalance(user, account.getId());
            } catch (Exception e) {
                log.warn("계좌 동기화 실패 (계속 진행): accountId={}", account.getId(), e);
            }
        }

        return getUserBankAccounts(user);
    }

    private Map<String, Object> validateAndGetAccountInfo(String accountNumber) {
        try {
            Optional<HanaBankMockAccount> mockAccount = 
                hanaBankMockAccountRepository.findByAccountNum(accountNumber);
            
            if (mockAccount.isPresent()) {
                HanaBankMockAccount account = mockAccount.get();
                log.info("Mock 계좌 검증 성공: {} - {} (활성상태: {})", 
                        accountNumber, account.getProductName(), account.getIsActive());
                
                return Map.of(
                    "productName", account.getProductName(),
                    "accountType", account.getAccountType(),
                    "balance", new BigDecimal(account.getBalanceAmt().toString())
                );
            } else {
                log.warn("Mock 계좌 테이블에서 계좌를 찾을 수 없음: {}", accountNumber);
                throw new CustomException(ErrorCode.INVALID_ACCOUNT);
            }
            
        } catch (CustomException e) {
            throw e; // CustomException은 그대로 전파
        } catch (Exception e) {
            log.error("계좌 정보 조회 실패: accountNumber={}", accountNumber, e);
            throw new CustomException(ErrorCode.ACCOUNT_VALIDATION_FAILED);
        }
    }

    private boolean isFirstAccount(User user) {
        return userBankAccountRepository.countByUserAndBankCode(user, "081") == 0;
    }

    private void unsetExistingPrimaryAccount(User user) {
        userBankAccountRepository.findByUserAndIsPrimaryTrue(user)
                .ifPresent(existingPrimary -> {
                    existingPrimary.unsetPrimary();
                    userBankAccountRepository.save(existingPrimary);
                });
    }

    private void setNextPrimaryAccount(User user, Long excludeAccountId) {
        List<UserBankAccount> activeAccounts = userBankAccountRepository.findByUserAndStatusOrderByIsPrimaryDescLinkedAtAsc(
                user, UserBankAccountStatus.ACTIVE);
        
        activeAccounts.stream()
                .filter(account -> !account.getId().equals(excludeAccountId))
                .findFirst()
                .ifPresent(nextPrimary -> {
                    nextPrimary.setPrimary();
                    userBankAccountRepository.save(nextPrimary);
                });
    }

    private UserBankAccountResponse convertToResponse(UserBankAccount account) {
        return UserBankAccountResponse.builder()
                .id(account.getId())
                .bankCode(account.getBankCode())
                .bankName(account.getBankName())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .accountAlias(account.getAccountAlias())
                .displayName(account.getDisplayName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .isPrimary(account.getIsPrimary())
                .status(account.getStatus())
                .autoSyncEnabled(account.getAutoSyncEnabled())
                .isWithdrawable(account.isWithdrawable())
                .lastSyncedAt(account.getLastSyncedAt())
                .linkedAt(account.getLinkedAt())
                .build();
    }
}
