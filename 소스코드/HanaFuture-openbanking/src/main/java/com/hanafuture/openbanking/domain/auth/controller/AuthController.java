package com.hanafuture.openbanking.domain.auth.controller;

import com.hanafuture.openbanking.domain.auth.dto.request.TokenRequest;
import com.hanafuture.openbanking.domain.auth.dto.response.TokenResponse;
import com.hanafuture.openbanking.domain.auth.service.AuthService;
import com.hanafuture.openbanking.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth/2.0")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth 2.0 인증", description = "OAuth 2.0 Client Credentials Grant 방식의 토큰 발급 API")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * OAuth 2.0 Client Credentials Grant 토큰 발급 API
     */
    @Operation(
        summary = "OAuth 2.0 토큰 발급",
        description = "Client Credentials Grant 방식으로 Access Token을 발급합니다. " +
                     "클라이언트 ID와 시크릿을 검증하여 90일간 유효한 JWT 토큰을 반환합니다.",
        tags = {"OAuth 2.0 인증"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "토큰 발급 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 (필수 파라미터 누락, 잘못된 scope 등)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "인증 실패 (잘못된 client_id 또는 client_secret)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> issueToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "토큰 발급 요청 정보", 
                required = true,
                content = @Content(schema = @Schema(implementation = TokenRequest.class))
            )
            @RequestBody TokenRequest request) {
        
        try {
            log.info("OAuth token request received for client: {}, grantType: {}", 
                    request.getClientId(), request.getGrantType());
            
            // 토큰 발급 처리
            TokenResponse response = authService.issueToken(request);
            
            log.info("OAuth token issued successfully for client: {}, clientUseCode: {}", 
                    request.getClientId(), response.getClientUseCode());
            
            return ResponseEntity.ok(
                    ApiResponse.success("토큰이 성공적으로 발급되었습니다.", response)
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("OAuth token failed - Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (RuntimeException e) {
            log.error("OAuth token failed - System error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("토큰 발급 중 시스템 오류가 발생했습니다."));
        }
    }
} 