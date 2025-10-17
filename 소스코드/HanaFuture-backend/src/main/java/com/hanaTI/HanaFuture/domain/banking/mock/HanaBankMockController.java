package com.hanaTI.HanaFuture.domain.banking.mock;

import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 하나은행 API Mock 서버
 */
@RestController
@RequestMapping("/mock/hanabank/api/v1")
@ConditionalOnProperty(name = "hanabank.mock.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class HanaBankMockController {

    private final HanaBankMockAccountRepository mockAccountRepository;

    /**
     * 사용자 계좌 목록 조회 (SQL 기반)
     */
    @GetMapping("/accounts/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAccounts(@PathVariable String userId) {
        log.info("Mock API 호출: 사용자 계좌 목록 조회 - userId: {}", userId);
        
        try {
            String testUserCi = "CI1001HANAFUTURE2024TEST";
            List<HanaBankMockAccount> mockAccounts = mockAccountRepository.findByUserCiAndIsActiveTrue(testUserCi);
            
            // 계좌 데이터를 API 응답 형식으로 변환
            List<Map<String, Object>> accounts = mockAccounts.stream()
                    .map(this::convertToAccountData)
                    .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "계좌 조회 성공");
            response.put("data", accounts);
            
            log.info("Mock 계좌 조회 완료: {}개 계좌", accounts.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Mock 계좌 조회 실패", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "계좌 조회 실패: " + e.getMessage());
            errorResponse.put("data", Collections.emptyList());
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 계좌 잔액 조회 (SQL 기반)
     */
    @GetMapping("/accounts/{accountNum}/balance")
    public ResponseEntity<Map<String, Object>> getAccountBalance(@PathVariable String accountNum) {
        log.info("Mock API 호출: 계좌 잔액 조회 - accountNum: {}", accountNum);
        
        try {
            Optional<HanaBankMockAccount> accountOpt = mockAccountRepository.findByAccountNumAndIsActiveTrue(accountNum);

        Map<String, Object> response = new HashMap<>();
            
            if (accountOpt.isPresent()) {
                HanaBankMockAccount account = accountOpt.get();
                
                Map<String, Object> balanceData = new HashMap<>();
                balanceData.put("accountNum", account.getAccountNum());
                balanceData.put("productName", account.getProductName());
                balanceData.put("balanceAmt", account.getBalanceAmt());
                balanceData.put("accountType", account.getAccountType());
                balanceData.put("bankName", account.getBankName());
                balanceData.put("lastUpdateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
        response.put("success", true);
                response.put("message", "잔액 조회 성공");
                response.put("data", balanceData);
                
                log.info("Mock 잔액 조회 완료: {} - {}", accountNum, account.getBalanceAmt());
            } else {
                response.put("success", false);
                response.put("message", "계좌를 찾을 수 없습니다");
                response.put("data", null);
                
                log.warn("Mock 계좌 없음: {}", accountNum);
            }
        
        return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Mock 잔액 조회 실패", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "잔액 조회 실패: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 계좌 데이터를 API 응답 형식으로 변환
     */
    private Map<String, Object> convertToAccountData(HanaBankMockAccount account) {
        Map<String, Object> data = new HashMap<>();
        data.put("accountNum", account.getAccountNum());
        data.put("productName", account.getProductName());
        data.put("balanceAmt", account.getBalanceAmt());
        data.put("accountType", account.getAccountType());
        data.put("bankCode", account.getBankCode());
        data.put("bankName", account.getBankName());
        data.put("fintechUseNum", account.getFintechUseNum());
        return data;
    }
}