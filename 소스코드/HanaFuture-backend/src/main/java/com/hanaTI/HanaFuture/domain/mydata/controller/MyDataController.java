package com.hanaTI.HanaFuture.domain.mydata.controller;

import com.hanaTI.HanaFuture.domain.mydata.dto.MyDataConnectRequest;
import com.hanaTI.HanaFuture.domain.mydata.dto.MyDataAccountResponse;
import com.hanaTI.HanaFuture.domain.mydata.service.MyDataIntegrationService;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import com.hanaTI.HanaFuture.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mydata")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MyData Integration", description = "마이데이터 통합 자산 연동 API")
public class MyDataController {

    private final MyDataIntegrationService myDataIntegrationService;

    @Operation(summary = "마이데이터 계좌 연동", description = "마이데이터를 통해 여러 은행의 계좌를 연동합니다.")
    @PostMapping("/connect")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Object>> connectMyDataAccounts(
            @Valid @RequestBody MyDataConnectRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        log.info("마이데이터 계좌 연동 요청 - 사용자: {}, 은행 수: {}", 
                currentUser.getEmail(), request.getBankCodes().size());
        
        Map<String, Object> result = myDataIntegrationService.connectMyDataAccounts(currentUser, request);
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ApiResponse.success("마이데이터 연동이 완료되었습니다.", result);
        } else {
            return ApiResponse.error(result.get("message").toString());
        }
    }

    @Operation(summary = "마이데이터 연결된 계좌 목록 조회", description = "마이데이터로 연동된 모든 계좌를 조회합니다.")
    @GetMapping("/accounts")
    public ApiResponse<List<MyDataAccountResponse>> getMyDataAccounts(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        log.info("마이데이터 계좌 목록 조회 - 사용자: {}", currentUser.getEmail());
        
        List<MyDataAccountResponse> accounts = myDataIntegrationService.getMyDataAccounts(currentUser);
        return ApiResponse.success("마이데이터 계좌 목록을 조회했습니다.", accounts);
    }

    @Operation(summary = "마이데이터 계좌 연동 해제", description = "특정 계좌의 마이데이터 연동을 해제합니다.")
    @PostMapping("/disconnect/{accountId}")
    public ApiResponse<Map<String, Object>> disconnectMyDataAccount(
            @PathVariable String accountId,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        log.info("마이데이터 계좌 연동 해제 - 사용자: {}, 계좌ID: {}", 
                currentUser.getEmail(), accountId);
        
        Map<String, Object> result = myDataIntegrationService.disconnectMyDataAccount(currentUser, accountId);
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ApiResponse.success("마이데이터 연동이 해제되었습니다.", result);
        } else {
            return ApiResponse.error(result.get("message").toString());
        }
    }

    @Operation(summary = "지원 은행 목록 조회", description = "마이데이터 연동이 가능한 은행 목록을 조회합니다.")
    @GetMapping("/supported-banks")
    public ApiResponse<List<Map<String, Object>>> getSupportedBanks() {
        
        List<Map<String, Object>> supportedBanks = List.of(
            Map.of(
                "bankCode", "hana",
                "bankName", "하나은행",
                "description", "주거래 은행으로 우선 연동",
                "isRecommended", true,
                "supportedProducts", List.of("입출금", "적금", "예금", "대출")
            ),
            Map.of(
                "bankCode", "kb",
                "bankName", "KB국민은행",
                "description", "국내 최대 은행",
                "isRecommended", false,
                "supportedProducts", List.of("입출금", "적금", "예금")
            ),
            Map.of(
                "bankCode", "shinhan",
                "bankName", "신한은행",
                "description", "디지털 금융 선도",
                "isRecommended", false,
                "supportedProducts", List.of("입출금", "적금", "예금", "대출")
            ),
            Map.of(
                "bankCode", "woori",
                "bankName", "우리은행",
                "description", "공공금융 전문",
                "isRecommended", false,
                "supportedProducts", List.of("입출금", "적금", "예금")
            ),
            Map.of(
                "bankCode", "nh",
                "bankName", "NH농협은행",
                "description", "농축산업 특화",
                "isRecommended", false,
                "supportedProducts", List.of("입출금", "적금", "예금")
            ),
            Map.of(
                "bankCode", "ibk",
                "bankName", "IBK기업은행",
                "description", "중소기업 금융",
                "isRecommended", false,
                "supportedProducts", List.of("입출금", "적금", "대출")
            )
        );
        
        return ApiResponse.success("지원 은행 목록을 조회했습니다.", supportedBanks);
    }
}
