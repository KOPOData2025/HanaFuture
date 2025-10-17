package com.hanaTI.HanaFuture.domain.openbanking.service;

import com.hanaTI.HanaFuture.domain.account.entity.Account;
import com.hanaTI.HanaFuture.domain.account.repository.AccountRepository;
import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenBankingService {
    
    private final AccountRepository accountRepository;
    private final HanaBankMockAccountRepository hanaBankMockAccountRepository;
    private final UserRepository userRepository;
    
    /**
     * 주민등록번호를 CI로 변환
     */
    public String generateCiFromResidentNumber(String residentNumber) {
        try {
            String cleanResidentNumber = residentNumber.replaceAll("-", "");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cleanResidentNumber.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().substring(0, 64); // 64자리로 제한
        } catch (NoSuchAlgorithmException e) {
            log.error("CI 생성 중 오류 발생", e);
            throw new RuntimeException("CI 생성 실패", e);
        }
    }

    public Map<String, Object> getUserAccounts(String userCi) {
        try {
            log.info("내부 DB에서 사용자 계좌 조회 시작 - userCi: {}", userCi);

            User user = findUserByCi(userCi);
            if (user == null) {
                return createErrorResponse("사용자를 찾을 수 없습니다");
            }
            
            List<Map<String, Object>> accountList = new ArrayList<>();

            List<Account> hanaFutureAccounts = accountRepository.findByUserIdAndIsActiveTrue(user.getId());
            for (Account account : hanaFutureAccounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("accountNum", account.getAccountNumber());
                accountData.put("bankName", account.getBankName());
                accountData.put("productName", account.getAccountName());
                accountData.put("balanceAmt", account.getBalance());
                accountData.put("accountType", mapAccountType(account.getAccountType().toString()));
                accountData.put("fintechUseNum", "HF_" + account.getId());
                accountData.put("source", "HANA_FUTURE");
                accountList.add(accountData);
            }

            List<HanaBankMockAccount> hanaBankAccounts = hanaBankMockAccountRepository
                    .findByUserCiAndIsActiveTrue(userCi);
            for (HanaBankMockAccount account : hanaBankAccounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("accountNum", account.getAccountNum());
                accountData.put("bankName", account.getBankName());
                accountData.put("productName", account.getProductName());
                accountData.put("balanceAmt", account.getBalanceAmt());
                accountData.put("accountType", mapMockAccountType(account.getAccountType()));
                accountData.put("fintechUseNum", account.getFintechUseNum());
                accountData.put("source", "HANA_BANK");
                accountList.add(accountData);
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("resList", accountList);
            responseData.put("totalCnt", accountList.size());
            responseData.put("inquiryRecordCnt", accountList.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "계좌 조회 성공");
            response.put("data", responseData);
            
            log.info("내부 DB 계좌 조회 완료 - userCi: {}, 계좌 수: {}", userCi, accountList.size());
            return response;
            
        } catch (Exception e) {
            log.error("내부 DB 계좌 조회 중 오류 발생 - userCi: {}", userCi, e);
            return createErrorResponse("계좌 조회 실패: " + e.getMessage());
        }
    }

    private User findUserByCi(String userCi) {
        if ("CI1001HANAFUTURE2024TEST".equals(userCi)) {
            return userRepository.findByEmail("hana@hanafuture.com").orElse(null);
        } else if ("CI2002HANAFUTURE2024TEST".equals(userCi)) {
            return userRepository.findByEmail("leehana@naver.com").orElse(null);
        } else if ("CI3003HANAFUTURE2024CHULWOO".equals(userCi)) {
            return userRepository.findByEmail("chulwoo@naver.com").orElse(null);
        }

        return userRepository.findAll().stream()
                .filter(user -> generateUserCi(user).equals(userCi))
                .findFirst()
                .orElse(null);
    }

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
    
    /**
     * 오류 응답 생성
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
    
}
