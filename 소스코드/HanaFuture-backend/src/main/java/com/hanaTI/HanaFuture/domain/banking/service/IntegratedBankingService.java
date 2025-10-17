package com.hanaTI.HanaFuture.domain.banking.service;

import com.hanaTI.HanaFuture.domain.account.entity.Account;
import com.hanaTI.HanaFuture.domain.account.repository.AccountRepository;
import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccount;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccountStatus;
import com.hanaTI.HanaFuture.domain.user.repository.UserBankAccountRepository;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 통합 뱅킹 서비스
 * 오픈뱅킹, 하나은행 Mock 서버 기능을 모두 통합하여 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegratedBankingService {
    
    private final AccountRepository accountRepository;
    private final HanaBankMockAccountRepository hanaBankMockAccountRepository;
    private final UserRepository userRepository;
    private final UserBankAccountRepository userBankAccountRepository;
    
    /**
     * 사용자의 모든 연동 계좌 조회 (통합)
     * - 하나Future 계좌 (accounts 테이블)
     * - 하나은행 Mock 계좌 (hanabank_mock_accounts 테이블)
     * - 오픈뱅킹 연동 계좌 (user_bank_accounts 테이블)
     */
    public Map<String, Object> getAllUserAccounts(Long userId) {
        try {
            log.info("통합 계좌 조회 시작 - userId: {}", userId);
            
            // 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
            
            log.info("사용자 정보 확인 - ID: {}, 이름: {}, 이메일: {}", user.getId(), user.getName(), user.getEmail());
            
            List<Map<String, Object>> allAccounts = new ArrayList<>();
            
            // 1. 하나Future 계좌 조회
            List<Account> hanaFutureAccounts = accountRepository.findByUserIdAndIsActiveTrue(userId);
            log.info("HanaFuture 계좌 조회 결과 - {}개", hanaFutureAccounts.size());
            for (Account account : hanaFutureAccounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("id", account.getId());
                accountData.put("accountNum", account.getAccountNumber());
                accountData.put("bankName", account.getBankName());
                accountData.put("productName", account.getAccountName());
                accountData.put("balanceAmt", account.getBalance());
                accountData.put("accountType", mapAccountType(account.getAccountType().toString()));
                accountData.put("source", "HANA_FUTURE");
                accountData.put("isWithdrawable", "CHECKING".equals(account.getAccountType()));
                accountData.put("fintechUseNum", "HF_" + account.getId());
                allAccounts.add(accountData);
            }
            
            // 2. 사용자 연동 계좌 조회 (user_bank_accounts)
            List<UserBankAccount> userBankAccounts = userBankAccountRepository
                    .findByUserAndStatusOrderByIsPrimaryDescLinkedAtAsc(user, UserBankAccountStatus.ACTIVE);
            log.info("사용자 연동 계좌 조회 결과 - {}개", userBankAccounts.size());
            
            for (UserBankAccount account : userBankAccounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("id", "USER_BANK_" + account.getId());
                accountData.put("accountNum", account.getAccountNumber());
                accountData.put("bankName", account.getBankName());
                accountData.put("productName", account.getAccountName());
                accountData.put("balanceAmt", account.getBalance());
                accountData.put("accountType", mapMockAccountType(account.getAccountType()));
                accountData.put("source", "USER_BANK_ACCOUNT");
                accountData.put("isWithdrawable", "1".equals(account.getAccountType())); // 1: 입출금
                accountData.put("fintechUseNum", "UBA_" + account.getId());
                allAccounts.add(accountData);
            }

            String userName = user.getName();
            log.info("사용자 이름으로 계좌 조회: {}", userName);
            
            // is_active가 null이거나 true인 계좌들을 조회
            List<HanaBankMockAccount> allUserAccounts = hanaBankMockAccountRepository.findByUserName(userName);
            List<HanaBankMockAccount> hanaBankAccounts = allUserAccounts.stream()
                    .filter(account -> account.getIsActive() == null || account.getIsActive())
                    .collect(Collectors.toList());
            
            log.info("HanaBank Mock 계좌 조회 결과 - 사용자명: {}, 전체: {}개, 활성: {}개", 
                    userName, allUserAccounts.size(), hanaBankAccounts.size());

            for (HanaBankMockAccount acc : allUserAccounts) {
                log.info("HanaBank Mock 계좌: {} - 활성상태: {}", acc.getAccountNum(), acc.getIsActive());
            }
            for (HanaBankMockAccount account : hanaBankAccounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("id", "HANA_MOCK_" + account.getId());
                accountData.put("accountNum", account.getAccountNum());
                accountData.put("bankName", account.getBankName());
                accountData.put("productName", account.getProductName());
                accountData.put("balanceAmt", account.getBalanceAmt());
                accountData.put("accountType", mapMockAccountType(account.getAccountType()));
                accountData.put("source", "HANA_BANK_MOCK");
                accountData.put("isWithdrawable", "1".equals(account.getAccountType())); // 1: 입출금
                accountData.put("fintechUseNum", account.getFintechUseNum());
                allAccounts.add(accountData);
            }
            
            // 3. 총 자산 계산
            BigDecimal totalBalance = allAccounts.stream()
                    .map(account -> {
                        Object balanceObj = account.get("balanceAmt");
                        if (balanceObj instanceof BigDecimal) {
                            return (BigDecimal) balanceObj;
                        } else if (balanceObj instanceof Number) {
                            return BigDecimal.valueOf(((Number) balanceObj).doubleValue());
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 4. 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "통합 계좌 조회 성공");
            response.put("data", Map.of(
                "accounts", allAccounts,
                "totalBalance", totalBalance,
                "accountCount", allAccounts.size(),
                "summary", Map.of(
                    "hanaFutureAccounts", hanaFutureAccounts.size(),
                    "hanaBankAccounts", hanaBankAccounts.size(),
                    "totalAccounts", allAccounts.size()
                )
            ));
            
            log.info("통합 계좌 조회 완료 - userId: {}, 총 {}개 계좌, 총 자산: {}원", 
                    userId, allAccounts.size(), totalBalance);
            
            return response;
            
        } catch (Exception e) {
            log.error("통합 계좌 조회 실패 - userId: {}", userId, e);
            return Map.of(
                "success", false,
                "message", "계좌 조회 실패: " + e.getMessage(),
                "data", Collections.emptyList()
            );
        }
    }
    
    /**
     * 출금 가능한 계좌만 조회
     */
    public Map<String, Object> getWithdrawableAccounts(Long userId) {
        Map<String, Object> allAccountsResponse = getAllUserAccounts(userId);
        
        if (!(Boolean) allAccountsResponse.get("success")) {
            return allAccountsResponse;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) allAccountsResponse.get("data");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allAccounts = (List<Map<String, Object>>) data.get("accounts");
        
        // 출금 가능한 계좌만 필터링
        List<Map<String, Object>> withdrawableAccounts = allAccounts.stream()
                .filter(account -> (Boolean) account.get("isWithdrawable"))
                .collect(Collectors.toList());
        
        return Map.of(
            "success", true,
            "message", "출금 가능 계좌 조회 성공",
            "data", withdrawableAccounts
        );
    }
    
    /**
     * 특정 계좌 상세 조회
     */
    public Map<String, Object> getAccountDetail(String accountId, Long userId) {
        try {
            // HanaFuture 계좌인 경우
            if (!accountId.startsWith("HANA_MOCK_")) {
                Long id = Long.parseLong(accountId);
                Optional<Account> accountOpt = accountRepository.findById(id);
                
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();
                    
                    // 사용자 권한 확인
                    if (!account.getUser().getId().equals(userId)) {
                        return Map.of("success", false, "message", "계좌 접근 권한이 없습니다");
                    }
                    
                    return Map.of(
                        "success", true,
                        "message", "계좌 상세 조회 성공",
                        "data", Map.of(
                            "id", account.getId(),
                            "accountNum", account.getAccountNumber(),
                            "bankName", account.getBankName(),
                            "productName", account.getAccountName(),
                            "balanceAmt", account.getBalance(),
                            "accountType", mapAccountType(account.getAccountType().toString()),
                            "source", "HANA_FUTURE"
                        )
                    );
                }
            }
            // 하나은행 Mock 계좌인 경우
            else {
                Long mockId = Long.parseLong(accountId.replace("HANA_MOCK_", ""));
                Optional<HanaBankMockAccount> accountOpt = hanaBankMockAccountRepository
                        .findById(mockId);
                
                if (accountOpt.isPresent()) {
                    HanaBankMockAccount account = accountOpt.get();
                    return Map.of(
                        "success", true,
                        "message", "계좌 상세 조회 성공",
                        "data", Map.of(
                            "id", "HANA_MOCK_" + account.getId(),
                            "accountNum", account.getAccountNum(),
                            "bankName", account.getBankName(),
                            "productName", account.getProductName(),
                            "balanceAmt", account.getBalanceAmt(),
                            "accountType", mapMockAccountType(account.getAccountType()),
                            "source", "HANA_BANK_MOCK"
                        )
                    );
                }
            }
            
            return Map.of(
                "success", false,
                "message", "계좌를 찾을 수 없습니다",
                "data", null
            );
            
        } catch (Exception e) {
            log.error("계좌 상세 조회 실패 - accountId: {}, userId: {}", accountId, userId, e);
            return Map.of(
                "success", false,
                "message", "계좌 상세 조회 실패: " + e.getMessage(),
                "data", null
            );
        }
    }

    /**
     * 사용자 CI 생성 (Mock용)
     */
    private String generateUserCi(User user) {
        if ("hana@hanafuture.com".equals(user.getEmail())) {
            return "CI1001HANAFUTURE2024TEST";
        } else if ("leehana@naver.com".equals(user.getEmail())) {
            return "CI2002HANAFUTURE2024TEST";
        } else if ("chulwoo@naver.com".equals(user.getEmail())) {
            return "CI3003HANAFUTURE2024CHULWOO";
        }
        return "CI_" + user.getId() + "_" + user.getEmail().hashCode();
    }
    
    /**
     * 계좌 타입 매핑 (HanaFuture)
     */
    private String mapAccountType(String accountType) {
        switch (accountType) {
            case "CHECKING": return "입출금";
            case "SAVINGS": return "적금";
            case "DEPOSIT": return "예금";
            case "INVESTMENT": return "투자";
            default: return "기타";
        }
    }
    
    /**
     * 계좌 타입 매핑 (HanaBank Mock)
     */
    private String mapMockAccountType(String accountType) {
        switch (accountType) {
            case "1": return "입출금";
            case "2": return "적금";
            case "3": return "예금";
            case "4": return "투자";
            default: return "기타";
        }
    }
}