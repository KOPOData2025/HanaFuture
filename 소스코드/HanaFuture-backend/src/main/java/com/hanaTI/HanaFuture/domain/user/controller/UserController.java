package com.hanaTI.HanaFuture.domain.user.controller;

import com.hanaTI.HanaFuture.domain.user.dto.UserInfoResponse;
import com.hanaTI.HanaFuture.domain.user.dto.UserInfoUpdateRequest;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.service.UserService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import com.hanaTI.HanaFuture.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 정보 관리 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "사용자 정보 조회",
            description = "현재 로그인된 사용자의 상세 정보를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        UserInfoResponse userInfo = userService.getUserInfo(currentUser);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", userInfo));
    }

    @Operation(
            summary = "사용자 정보 업데이트",
            description = "사용자의 개인정보를 업데이트합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateUserInfo(
            @Parameter(hidden = true) @CurrentUser User currentUser,
            @Valid @RequestBody UserInfoUpdateRequest request) {
        
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 업데이트 성공", updatedUserInfo));
    }

    @Operation(
            summary = "사용자 프로필 조회",
            description = "사용자의 기본 프로필 정보를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserProfile(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        UserInfoResponse userInfo = userService.getUserInfo(currentUser);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", userInfo));
    }

    @Operation(
            summary = "사용자 프로필 업데이트",
            description = "사용자의 프로필 정보를 업데이트합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateUserProfile(
            @Parameter(hidden = true) @CurrentUser User currentUser,
            @Valid @RequestBody UserInfoUpdateRequest request) {
        
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("프로필 업데이트 성공", updatedUserInfo));
    }
}