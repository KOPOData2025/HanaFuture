package com.hanaTI.HanaFuture.domain.banking.controller;

import com.hanaTI.HanaFuture.domain.banking.service.IntegratedBankingService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 통합 뱅킹 API 컨트롤러
 * 오픈뱅킹, 하나은행 Mock 서버 기능을 모두 통합하여 처리
 */
@RestController
@RequestMapping("/api/integrated-banking")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Integrated Banking", description = "통합 뱅킹 API")
public class IntegratedBankingController {
    
    private final IntegratedBankingService integratedBankingService;
    
    @Operation(
        summary = "사용자 모든 계좌 조회", 
        description = "하나Future, 하나은행 Mock, 오픈뱅킹 연동 계좌를 모두 조회합니다."
    )
    @GetMapping("/accounts/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getAllUserAccounts(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info(" [마이페이지] 통합 계좌 조회 요청 - userId: {}", userId);
        
        Map<String, Object> result = integratedBankingService.getAllUserAccounts(userId);
        
        if ((Boolean) result.get("success")) {
        return ResponseEntity.ok(ApiResponse.success(
                (String) result.get("message"), 
                result.get("data")
            ));
        } else {
            return ResponseEntity.ok(ApiResponse.error((String) result.get("message")));
        }
    }
    
    @Operation(
        summary = "출금 가능 계좌 조회", 
        description = "사용자의 출금 가능한 계좌만 조회합니다."
    )
    @GetMapping("/accounts/withdrawable/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getWithdrawableAccounts(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("출금 가능 계좌 조회 요청 - userId: {}", userId);
        
        Map<String, Object> result = integratedBankingService.getWithdrawableAccounts(userId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success(
                (String) result.get("message"), 
                result.get("data")
            ));
        } else {
            return ResponseEntity.ok(ApiResponse.error((String) result.get("message")));
        }
    }
    
    @Operation(
        summary = "계좌 상세 조회", 
        description = "특정 계좌의 상세 정보를 조회합니다."
    )
    @GetMapping("/accounts/{accountId}/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getAccountDetail(
            @Parameter(description = "계좌 ID") @PathVariable String accountId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("계좌 상세 조회 요청 - accountId: {}, userId: {}", accountId, userId);
        
        Map<String, Object> result = integratedBankingService.getAccountDetail(accountId, userId);
        
        if ((Boolean) result.get("success")) {
        return ResponseEntity.ok(ApiResponse.success(
                (String) result.get("message"), 
                result.get("data")
            ));
        } else {
            return ResponseEntity.ok(ApiResponse.error((String) result.get("message")));
        }
    }

    @Operation(
        summary = "계좌 연동 시뮬레이션", 
        description = "새로운 계좌 연동을 시뮬레이션합니다. (실제로는 오픈뱅킹 인증 과정)"
    )
    @PostMapping("/accounts/link")
    public ResponseEntity<ApiResponse<Object>> linkAccount(@RequestBody Map<String, Object> request) {
        
        log.info("계좌 연동 시뮬레이션 요청: {}", request);
        
        // 실제로는 오픈뱅킹 인증 과정이 필요하지만, 
        // 통합 서버에서는 시뮬레이션으로 처리
        return ResponseEntity.ok(ApiResponse.success(
            "계좌 연동이 완료되었습니다. (시뮬레이션)", 
            Map.of(
                "linked", true,
                "accountId", "NEW_ACCOUNT_" + System.currentTimeMillis(),
                "message", "실제 환경에서는 오픈뱅킹 인증이 필요합니다."
            )
        ));
    }

    @Operation(
        summary = "계좌 잔액 새로고침", 
        description = "모든 연동 계좌의 잔액을 새로고침합니다."
    )
    @PostMapping("/accounts/refresh/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> refreshAccountBalances(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("계좌 잔액 새로고침 요청 - userId: {}", userId);

        // 통합 서버에서는 현재 데이터를 그대로 반환
        Map<String, Object> result = integratedBankingService.getAllUserAccounts(userId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success(
                "계좌 잔액 새로고침 완료", 
                result.get("data")
            ));
        } else {
            return ResponseEntity.ok(ApiResponse.error((String) result.get("message")));
        }
    }
}