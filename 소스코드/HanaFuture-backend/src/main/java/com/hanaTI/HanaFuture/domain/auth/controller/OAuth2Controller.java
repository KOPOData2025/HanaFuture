package com.hanaTI.HanaFuture.domain.auth.controller;

import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@Tag(name = "OAuth2 인증", description = "소셜 로그인 관련 API")
public class OAuth2Controller {
    
    @Operation(
            summary = "OAuth2 로그인 URL 조회",
            description = "카카오, 구글 소셜 로그인 URL을 조회합니다."
    )
    @GetMapping("/login-urls")
    public ResponseEntity<ApiResponse<Map<String, String>>> getLoginUrls() {
        Map<String, String> loginUrls = Map.of(
            "google", "/oauth2/authorization/google",
            "kakao", "/oauth2/authorization/kakao"
        );
        
        return ResponseEntity.ok(
            ApiResponse.success("소셜 로그인 URL 조회 성공", loginUrls)
        );
    }
    
    @Operation(
            summary = "OAuth2 로그인 상태 확인",
            description = "현재 OAuth2 로그인 상태를 확인합니다."
    )
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOAuth2Status() {
        Map<String, Object> status = Map.of(
            "available", true,
            "providers", new String[]{"google", "kakao"}
        );
        
        return ResponseEntity.ok(
            ApiResponse.success("OAuth2 상태 조회 성공", status)
        );
    }
}
