package com.hanaTI.HanaFuture.domain.user.controller;

import com.hanaTI.HanaFuture.domain.user.dto.LinkBankAccountRequest;
import com.hanaTI.HanaFuture.domain.user.dto.UserBankAccountResponse;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.user.service.UserBankAccountService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import com.hanaTI.HanaFuture.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Bank Accounts", description = "사용자 계좌 연동 관리 API")
public class UserBankAccountController {

    private final UserBankAccountService userBankAccountService;
    private final UserRepository userRepository;

    @Operation(summary = "연동된 계좌 목록 조회", description = "사용자가 연동한 모든 은행 계좌를 조회합니다.")
    @GetMapping
    public ApiResponse<List<UserBankAccountResponse>> getUserBankAccounts(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        List<UserBankAccountResponse> accounts = userBankAccountService.getUserBankAccounts(currentUser);
        return ApiResponse.success(accounts);
    }

    @Operation(summary = "사용자별 연동 계좌 조회", description = "특정 사용자 ID로 연동된 계좌를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserBankAccountResponse>> getUserBankAccountsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            HttpServletRequest request) {
        
        log.info(" 사용자 ID {}의 연동 계좌 조회 요청", userId);
        
        // JWT 토큰 로그 확인
        String authHeader = request.getHeader("Authorization");
        log.info(" Authorization 헤더: {}", authHeader != null ? "Bearer ***" + authHeader.substring(Math.max(0, authHeader.length() - 10)) : "없음");
        
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            
            log.info(" 사용자 정보: ID={}, 이름={}, 이메일={}", user.getId(), user.getName(), user.getEmail());
            
            List<UserBankAccountResponse> accounts = userBankAccountService.getUserBankAccounts(user);
            log.info("사용자 {}의 연동 계좌 {}개 조회 완료", userId, accounts.size());
            
            // 계좌 목록 상세 로그
            for (UserBankAccountResponse account : accounts) {
                log.info(" 계좌: {} - {} ({:,}원)", account.getAccountName(), account.getAccountNumber(), account.getBalance().longValue());
            }
            
            return ApiResponse.success(accounts);
        } catch (Exception e) {
            log.error("사용자 {}의 연동 계좌 조회 실패: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "출금 가능 계좌 목록 조회", description = "출금이 가능한 계좌만 조회합니다.")
    @GetMapping("/withdrawable")
    public ApiResponse<List<UserBankAccountResponse>> getWithdrawableAccounts(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        List<UserBankAccountResponse> accounts = userBankAccountService.getWithdrawableAccounts(currentUser);
        return ApiResponse.success(accounts);
    }

    @Operation(summary = "은행 계좌 연동", description = "하나은행 계좌를 사용자 계정에 연동합니다.")
    @PostMapping("/link")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserBankAccountResponse> linkBankAccount(
            @Valid @RequestBody LinkBankAccountRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        UserBankAccountResponse response = userBankAccountService.linkBankAccount(currentUser, request);
        return ApiResponse.success("계좌 연동이 완료되었습니다.", response);
    }

    @Operation(summary = "계좌 연동 해제", description = "연동된 계좌를 해제합니다.")
    @DeleteMapping("/{accountId}")
    public ApiResponse<Void> unlinkBankAccount(
            @PathVariable Long accountId,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        userBankAccountService.unlinkBankAccount(currentUser, accountId);
        return ApiResponse.success("계좌 연동이 해제되었습니다.");
    }

    @Operation(summary = "주계좌 설정", description = "특정 계좌를 주계좌로 설정합니다.")
    @PutMapping("/{accountId}/primary")
    public ApiResponse<Void> setPrimaryAccount(
            @PathVariable Long accountId,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        userBankAccountService.setPrimaryAccount(currentUser, accountId);
        return ApiResponse.success("주계좌가 변경되었습니다.");
    }

    @Operation(summary = "계좌 잔액 동기화", description = "특정 계좌의 잔액을 하나은행에서 최신 정보로 동기화합니다.")
    @PostMapping("/{accountId}/sync")
    public ApiResponse<UserBankAccountResponse> syncAccountBalance(
            @PathVariable Long accountId,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        UserBankAccountResponse response = userBankAccountService.syncAccountBalance(currentUser, accountId);
        return ApiResponse.success("계좌 잔액이 동기화되었습니다.", response);
    }

    @Operation(summary = "모든 계좌 동기화", description = "연동된 모든 계좌의 잔액을 동기화합니다.")
    @PostMapping("/sync-all")
    public ApiResponse<List<UserBankAccountResponse>> syncAllAccounts(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        List<UserBankAccountResponse> accounts = userBankAccountService.syncAllAccounts(currentUser);
        return ApiResponse.success("모든 계좌가 동기화되었습니다.", accounts);
    }

    @Operation(summary = "계좌 연동 가이드", description = "계좌 연동 방법과 주의사항을 안내합니다.")
    @GetMapping("/guide")
    public ApiResponse<Object> getLinkGuide() {
        Object guide = java.util.Map.of(
            "title", "하나은행 계좌 연동 가이드",
            "steps", java.util.List.of(
                "1. 하나은행 계좌번호를 준비하세요",
                "2. 계좌 소유자 본인 확인이 필요합니다",
                "3. SMS 인증 또는 공인인증서로 인증하세요",
                "4. 연동 완료 후 자동 동기화를 설정할 수 있습니다"
            ),
            "requirements", java.util.List.of(
                "하나은행 계좌 (입출금통장, 적금 등)",
                "본인 명의 계좌",
                "휴대폰 SMS 수신 가능",
                "계좌 비밀번호"
            ),
            "benefits", java.util.List.of(
                "모임통장 자동 이체 설정",
                "적금 상품 가입 시 출금계좌 자동 설정",
                "실시간 잔액 조회",
                "거래내역 자동 동기화"
            ),
            "security", java.util.List.of(
                "계좌번호는 암호화되어 저장됩니다",
                "하나은행 공식 API를 통해서만 접근합니다",
                "언제든지 연동을 해제할 수 있습니다",
                "개인정보는 금융보안원 기준에 따라 보호됩니다"
            )
        );
        
        return ApiResponse.success(guide);
    }
}
