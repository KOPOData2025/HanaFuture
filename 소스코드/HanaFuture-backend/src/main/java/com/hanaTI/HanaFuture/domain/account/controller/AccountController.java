package com.hanaTI.HanaFuture.domain.account.controller;

import com.hanaTI.HanaFuture.domain.account.dto.*;
import com.hanaTI.HanaFuture.domain.account.service.AccountService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account", description = "계좌 관리 API")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
    
    private final AccountService accountService;
    
    @Operation(
            summary = "계좌 요약 정보 조회",
            description = "사용자의 모든 계좌 요약 정보와 최근 거래내역을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AccountSummaryResponse>> getAccountSummary(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        AccountSummaryResponse response = accountService.getAccountSummary(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("계좌 요약 정보를 조회했습니다.", response));
    }
    
    @Operation(
            summary = "계좌 목록 조회",
            description = "사용자의 모든 활성 계좌 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getUserAccounts(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        List<AccountResponse> response = accountService.getUserAccounts(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("계좌 목록을 조회했습니다.", response));
    }
    
    @Operation(
            summary = "계좌 상세 조회",
            description = "특정 계좌의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없음"
            )
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @Parameter(description = "계좌 ID", example = "1") @PathVariable Long accountId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        AccountResponse response = accountService.getAccount(userDetails.getUsername(), accountId);
        return ResponseEntity.ok(ApiResponse.success("계좌 정보를 조회했습니다.", response));
    }
    
    @Operation(
            summary = "계좌 생성",
            description = "새로운 계좌를 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "계좌 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        AccountResponse response = accountService.createAccount(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("계좌가 생성되었습니다.", response));
    }
    
    @Operation(
            summary = "계좌 정보 수정",
            description = "계좌의 이름과 설명을 수정합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없음"
            )
    })
    @PutMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @Parameter(description = "계좌 ID", example = "1") @PathVariable Long accountId,
            @Valid @RequestBody CreateAccountRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        AccountResponse response = accountService.updateAccount(userDetails.getUsername(), accountId, request);
        return ResponseEntity.ok(ApiResponse.success("계좌 정보가 수정되었습니다.", response));
    }
    
    @Operation(
            summary = "계좌 비활성화",
            description = "계좌를 비활성화합니다. 잔액이 있는 계좌는 비활성화할 수 없습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비활성화 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잔액이 있는 계좌는 비활성화할 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없음"
            )
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @Parameter(description = "계좌 ID", example = "1") @PathVariable Long accountId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        accountService.deactivateAccount(userDetails.getUsername(), accountId);
        return ResponseEntity.ok(ApiResponse.success("계좌가 비활성화되었습니다.", null));
    }
    
    // Note: Transaction-related endpoints removed as they use deleted TransactionResponse
    // GroupAccountTransaction endpoints should be used instead
}

