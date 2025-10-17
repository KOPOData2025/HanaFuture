package com.hanaTI.HanaFuture.domain.banking.controller;

import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 하나은행 API 테스트용 Mock 컨트롤러
@RestController
 */
@RequestMapping("/api/hanabank-mock")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HanaBank Mock API", description = "하나은행 API 테스트용 Mock 서버")
public class BankingTestController {
    
    @Operation(summary = "Mock 사용자 계좌 목록 조회", description = "테스트용 Mock 계좌 데이터를 반환합니다.")
    @GetMapping("/accounts/user/{userId}")
    public ApiResponse<List<Map<String, Object>>> getMockUserAccounts(@PathVariable String userId) {
        log.info("Mock 사용자 계좌 조회 요청: userId={}", userId);

        List<Map<String, Object>> accounts = List.of(
            Map.of(
                "accountNum", "81700123456789",
                "productName", "하나 주거래통장",
                "balanceAmt", 5500000,
                "accountType", "1"
            ),
            Map.of(
                "accountNum", "81700987654321", 
                "productName", "하나 자녀교육적금",
                "balanceAmt", 8500000,
                "accountType", "2"
            ),
            Map.of(
                "accountNum", "81700555666777", 
                "productName", "하나 비상자금예금",
                "balanceAmt", 12000000,
                "accountType", "3"
            ),
            Map.of(
                "accountNum", "81700111222333", 
                "productName", "하나 투자신탁",
                "balanceAmt", 15000000,
                "accountType", "4"
            )
        );
        
        return ApiResponse.success("계좌 조회 성공", accounts);
    }
    
    @Operation(summary = "Mock 계좌 잔액 조회", description = "테스트용 Mock 잔액 데이터를 반환합니다.")
    @GetMapping("/accounts/{accountNum}/balance")
    public ApiResponse<Map<String, Object>> getMockAccountBalance(@PathVariable String accountNum) {
        log.info("Mock 계좌 잔액 조회 요청: accountNum={}", accountNum);
        
        Map<String, Object> balance = Map.of(
            "accountNum", accountNum,
            "balanceAmt", 45231000,
            "availableAmt", 45231000,
            "currency", "KRW",
            "lastUpdated", "2024-09-08T14:30:00"
        );
        
        return ApiResponse.success("잔액 조회 성공", balance);
    }
    
    @Operation(summary = "Mock 거래내역 조회", description = "테스트용 Mock 거래내역 데이터를 반환합니다.")
    @PostMapping("/accounts/{accountNum}/transactions")
    public ApiResponse<List<Map<String, Object>>> getMockTransactionHistory(
            @PathVariable String accountNum,
            @RequestBody Map<String, Object> request) {
        log.info("Mock 거래내역 조회 요청: accountNum={}, request={}", accountNum, request);
        
        List<Map<String, Object>> transactions = List.of(
            Map.of(
                "transactionDate", "2024-09-08",
                "transactionTime", "14:30:00",
                "description", "모임통장 입금",
                "amount", 200000,
                "transactionType", "DEPOSIT",
                "balance", 45231000
            ),
            Map.of(
                "transactionDate", "2024-09-07",
                "transactionTime", "10:15:00", 
                "description", "ATM 출금",
                "amount", -50000,
                "transactionType", "WITHDRAW",
                "balance", 45031000
            )
        );
        
        return ApiResponse.success("거래내역 조회 성공", transactions);
    }
    
    @Operation(summary = "Mock 적금 상품 추천", description = "테스트용 Mock 적금 상품 추천 데이터를 반환합니다.")
    @PostMapping("/savings/recommendations")
    public ApiResponse<List<Map<String, Object>>> getMockSavingsRecommendations(@RequestBody Map<String, Object> request) {
        log.info("Mock 적금 상품 추천 조회 요청: request={}", request);
        
        List<Map<String, Object>> products = List.of(
            Map.of(
                "productCode", "HANA_SAVINGS_001",
                "productName", "하나 자유적금",
                "interestRate", 3.5,
                "minAmount", 10000,
                "maxAmount", 50000000,
                "description", "자유롭게 입출금이 가능한 적금상품"
            ),
            Map.of(
                "productCode", "HANA_CHILDCARE_001",
                "productName", "하나 육아적금",
                "interestRate", 4.2,
                "minAmount", 50000,
                "maxAmount", 10000000,
                "description", "육아비용 마련을 위한 특별 적금상품"
            )
        );
        
        return ApiResponse.success("적금 상품 추천 조회 성공", products);
    }
}