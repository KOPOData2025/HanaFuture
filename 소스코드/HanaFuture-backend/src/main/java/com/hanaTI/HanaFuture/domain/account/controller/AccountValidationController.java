package com.hanaTI.HanaFuture.domain.account.controller;

import com.hanaTI.HanaFuture.domain.banking.service.IntegratedBankingService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Account Validation", description = "계좌 연결 상태 확인 API")
@RestController
@RequestMapping("/api/account-validation")
@RequiredArgsConstructor
@Slf4j
public class AccountValidationController {

    private final IntegratedBankingService integratedBankingService;

    @Operation(
        summary = "사용자 계좌 연결 상태 확인", 
        description = "사용자가 연결된 계좌가 있는지 확인합니다. 모임통장 생성 전 필수 체크."
    )
    @GetMapping("/user/{userId}/connection-status")
    public ResponseEntity<ApiResponse<Object>> checkAccountConnectionStatus(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("사용자 {}의 계좌 연결 상태 확인", userId);
        
        try {
            // 통합 뱅킹 서비스로 사용자 계좌 조회
            Map<String, Object> accountsResult = integratedBankingService.getAllUserAccounts(userId);
            
            Map<String, Object> response = new HashMap<>();
            
            if ((Boolean) accountsResult.get("success")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) accountsResult.get("data");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> accounts = (List<Map<String, Object>>) data.get("accounts");
                
                boolean hasConnectedAccounts = accounts != null && !accounts.isEmpty();
                
                // 출금 가능한 계좌 필터링
                long withdrawableCount = accounts != null ? 
                    accounts.stream()
                        .filter(account -> {
                            Object isWithdrawable = account.get("isWithdrawable");
                            String accountType = (String) account.get("accountType");
                            return (Boolean.TRUE.equals(isWithdrawable)) || 
                                   "입출금".equals(accountType) || 
                                   "1".equals(accountType);
                        })
                        .count() : 0;
                
                response.put("hasConnectedAccounts", hasConnectedAccounts);
                response.put("totalAccounts", accounts != null ? accounts.size() : 0);
                response.put("withdrawableAccounts", withdrawableCount);
                response.put("needsAccountConnection", !hasConnectedAccounts || withdrawableCount == 0);
                
                if (hasConnectedAccounts) {
                    response.put("message", "연결된 계좌가 있습니다.");
                    response.put("recommendedAction", "PROCEED_TO_GROUP_ACCOUNT");
                } else {
                    response.put("message", "연결된 계좌가 없습니다. 오픈뱅킹 연결이 필요합니다.");
                    response.put("recommendedAction", "CONNECT_OPENBANKING");
                }
                
                // 계좌 요약 정보 추가
                if (accounts != null && !accounts.isEmpty()) {
                    response.put("accountSummary", accounts.stream()
                        .map(account -> Map.of(
                            "bankName", account.get("bankName"),
                            "accountType", account.get("accountType"),
                            "source", account.get("source")
                        ))
                        .toList());
                }
                
            } else {
                response.put("hasConnectedAccounts", false);
                response.put("totalAccounts", 0);
                response.put("withdrawableAccounts", 0);
                response.put("needsAccountConnection", true);
                response.put("message", "계좌 조회에 실패했습니다. 오픈뱅킹 연결이 필요합니다.");
                response.put("recommendedAction", "CONNECT_OPENBANKING");
            }
            
            log.info("계좌 연결 상태 확인 완료 - userId: {}, hasAccounts: {}", 
                userId, response.get("hasConnectedAccounts"));
            
            return ResponseEntity.ok(ApiResponse.success("계좌 연결 상태 확인 완료", response));
            
        } catch (Exception e) {
            log.error("계좌 연결 상태 확인 실패 - userId: {}", userId, e);
            
            Map<String, Object> errorResponse = Map.of(
                "hasConnectedAccounts", false,
                "totalAccounts", 0,
                "withdrawableAccounts", 0,
                "needsAccountConnection", true,
                "message", "계좌 연결 상태 확인 중 오류가 발생했습니다.",
                "recommendedAction", "CONNECT_OPENBANKING"
            );
            
            return ResponseEntity.ok(ApiResponse.success("계좌 연결 상태 확인 완료", errorResponse));
        }
    }

    @Operation(
        summary = "모임통장 생성 가능 여부 확인", 
        description = "사용자가 모임통장을 생성할 수 있는 상태인지 확인합니다."
    )
    @GetMapping("/user/{userId}/group-account-eligibility")
    public ResponseEntity<ApiResponse<Object>> checkGroupAccountEligibility(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("사용자 {}의 모임통장 생성 가능 여부 확인", userId);
        
        try {
            // 계좌 연결 상태 확인
            ResponseEntity<ApiResponse<Object>> connectionStatus = 
                checkAccountConnectionStatus(userId);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> connectionData = 
                (Map<String, Object>) connectionStatus.getBody().getData();
            
            Map<String, Object> response = new HashMap<>();
            boolean hasConnectedAccounts = (Boolean) connectionData.get("hasConnectedAccounts");
            long withdrawableAccounts = (Long) connectionData.get("withdrawableAccounts");
            
            if (hasConnectedAccounts && withdrawableAccounts > 0) {
                response.put("eligible", true);
                response.put("message", "모임통장 생성이 가능합니다.");
                response.put("nextStep", "GROUP_ACCOUNT_CREATION");
            } else {
                response.put("eligible", false);
                response.put("message", "모임통장 생성을 위해 출금 가능한 계좌 연결이 필요합니다.");
                response.put("nextStep", "OPENBANKING_CONNECTION");
                response.put("requiredActions", List.of(
                    "오픈뱅킹 동의",
                    "본인 인증",
                    "계좌 선택 및 연결"
                ));
            }
            
            response.put("accountStatus", connectionData);
            
            return ResponseEntity.ok(ApiResponse.success("모임통장 생성 가능 여부 확인 완료", response));
            
        } catch (Exception e) {
            log.error("모임통장 생성 가능 여부 확인 실패 - userId: {}", userId, e);
            return ResponseEntity.ok(ApiResponse.error("모임통장 생성 가능 여부 확인에 실패했습니다."));
        }
    }
}



