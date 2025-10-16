package com.hana.hanabank.domain.account.controller;

import com.hana.hanabank.domain.account.entity.Account;
import com.hana.hanabank.domain.account.service.AccountService;
import com.hana.hanabank.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Account", description = "계좌 관리 API")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    @Operation(summary = "사용자 계좌 목록 조회", description = "사용자 ID로 계좌 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Account>>> getUserAccounts(@PathVariable String userId) {
        try {
            List<Account> accounts = accountService.getUserAccounts(userId);
            return ResponseEntity.ok(ApiResponse.success(accounts));
        } catch (Exception e) {
            log.error("계좌 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("계좌 목록 조회에 실패했습니다."));
        }
    }
    
    @Operation(summary = "계좌 잔액 조회", description = "계좌번호로 잔액을 조회합니다.")
    @GetMapping("/{accountNum}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getAccountBalance(@PathVariable String accountNum) {
        try {
            BigDecimal balance = accountService.getAccountBalance(accountNum);
            return ResponseEntity.ok(ApiResponse.success(balance));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 잔액 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("계좌 잔액 조회에 실패했습니다."));
        }
    }
    
    @Operation(summary = "계좌 상세 정보 조회", description = "계좌번호로 상세 정보를 조회합니다.")
    @GetMapping("/{accountNum}")
    public ResponseEntity<ApiResponse<Account>> getAccountDetail(@PathVariable String accountNum) {
        try {
            Account account = accountService.getAccountDetail(accountNum);
            return ResponseEntity.ok(ApiResponse.success(account));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("계좌 상세 조회에 실패했습니다."));
        }
    }
    
    @Operation(summary = "육아 적금 상품 생성", description = "육아 특화 적금 상품을 생성합니다.")
    @PostMapping("/childcare-savings")
    public ResponseEntity<ApiResponse<Account>> createChildcareSavings(
            @RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String productType = (String) request.get("productType");
            BigDecimal monthlyAmount = new BigDecimal(request.get("monthlyAmount").toString());
            Integer periodMonths = (Integer) request.get("periodMonths");
            
            Account account = accountService.createChildcareSavings(userId, productType, monthlyAmount, periodMonths);
            return ResponseEntity.ok(ApiResponse.success("육아 적금 상품이 생성되었습니다.", account));
        } catch (Exception e) {
            log.error("육아 적금 생성 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("육아 적금 생성에 실패했습니다."));
        }
    }
    
    @Operation(summary = "육아 상품 추천", description = "사용자에게 맞는 육아 관련 금융 상품을 추천합니다.")
    @GetMapping("/childcare-recommendations/{userId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getChildcareProductRecommendations(
            @PathVariable String userId) {
        try {
            List<Map<String, Object>> recommendations = accountService.getChildcareProductRecommendations(userId);
            return ResponseEntity.ok(ApiResponse.success(recommendations));
        } catch (Exception e) {
            log.error("육아 상품 추천 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("육아 상품 추천에 실패했습니다."));
        }
    }
    
    @Operation(summary = "테스트용 계좌 생성", description = "테스트를 위한 계좌 데이터를 생성합니다.")
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Account>> createTestAccount(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String accountNum = (String) request.get("accountNum");
            String productName = (String) request.getOrDefault("productName", "하나 통장");
            BigDecimal balanceAmt = new BigDecimal(request.getOrDefault("balanceAmt", "1000000").toString());
            
            Account account = accountService.createTestAccount(userId, accountNum, productName, balanceAmt);
            
            log.info("테스트 계좌 생성 성공: 사용자={}, 계좌번호={}", userId, accountNum);
            return ResponseEntity.ok(ApiResponse.success("테스트 계좌가 생성되었습니다.", account));
            
        } catch (Exception e) {
            log.error("테스트 계좌 생성 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("테스트 계좌 생성에 실패했습니다."));
        }
    }
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserAccountsByCi(@RequestBody Map<String, Object> request) {
        try {
            String userCi = (String) request.get("userCi");
            String userName = (String) request.get("userName");
            String userNum = (String) request.get("userNum");

            log.info("CI 기반 계좌 조회 요청: 이름={}, CI={}", userName, userCi != null ? userCi.substring(0, 8) + "..." : "null");

            List<Account> accounts = accountService.getUserAccountsByCi(userCi);
            
            if (accounts.isEmpty()) {
                log.info("계좌가 없음: 사용자={}", userName);
                Map<String, Object> data = Map.of("accounts", List.of(), "totalCount", 0);
                return ResponseEntity.ok(ApiResponse.success("계좌가 없습니다.", data));
            }

            // 계좌 데이터 변환
            List<Map<String, Object>> accountList = accounts.stream()
                .map(this::convertAccountToMap)
                .collect(java.util.stream.Collectors.toList());

            log.info("CI 기반 계좌 조회 성공: 사용자={}, 계좌수={}", userName, accountList.size());

            Map<String, Object> data = Map.of(
                "accounts", accountList,
                "totalCount", accountList.size()
            );

            return ResponseEntity.ok(ApiResponse.success("계좌 조회가 완료되었습니다.", data));

        } catch (Exception e) {
            log.error("CI 기반 계좌 조회 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("계좌 조회 중 오류가 발생했습니다."));
        }
    }
    
    private Map<String, Object> convertAccountToMap(Account account) {
        Map<String, Object> accountMap = new java.util.HashMap<>();
        accountMap.put("accountNum", account.getAccountNum());
        accountMap.put("accountAlias", account.getProductName() + " (" + account.getProductSubName() + ")");
        accountMap.put("balanceAmt", String.valueOf(account.getBalanceAmt().longValue()));
        accountMap.put("productName", account.getProductName());
        accountMap.put("productSubName", account.getProductSubName());
        accountMap.put("bankCodeStd", account.getBankCodeStd());
        accountMap.put("bankName", "하나은행");
        accountMap.put("fintechUseNum", "FT" + account.getBankCodeStd() + account.getAccountSeq());
        accountMap.put("accountType", account.getAccountType());
        accountMap.put("accountIssueDate", account.getAccountIssueDate());
        accountMap.put("maturityDate", account.getMaturityDate());
        accountMap.put("lastTranDate", account.getLastTranDate());
        accountMap.put("dormancyYn", account.getDormancyYn());
        return accountMap;
    }
}
