package com.hanafuture.openbanking.domain.user.controller;

import com.hanafuture.openbanking.domain.user.dto.request.UserRegisterRequest;
import com.hanafuture.openbanking.domain.user.dto.response.UserRegisterResponse;
import com.hanafuture.openbanking.domain.user.service.UserService;
import com.hanafuture.openbanking.global.dto.ApiResponse;
import com.hanafuture.openbanking.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2.0/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자 관리", description = "사용자 계좌 등록 및 관리 API")
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * 사용자 계좌 등록 API
     */
    @Operation(
        summary = "사용자 계좌 등록",
        description = "사용자의 계좌 정보를 등록하고 고유한 핀테크 이용번호를 발급합니다. " +
                     "기존 사용자는 기존 정보를 재사용하고, 신규 사용자는 새로 생성됩니다.",
        tags = {"사용자 관리"},
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "계좌 등록 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 (필수 파라미터 누락, 잘못된 은행코드 등)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "인증 실패 (유효하지 않은 토큰)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> registerUser(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "사용자 계좌 등록 요청 정보", 
                required = true,
                content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            )
            @RequestBody UserRegisterRequest request) {
        
        try {
            log.info("User registration request received");
            
            // 1. Authorization 헤더에서 토큰 추출
            String authAccessToken = jwtUtil.extractTokenFromHeader(authorization);
            
            // 2. JWT 토큰 유효성 검증
            if (!jwtUtil.validateToken(authAccessToken)) {
                log.warn("Invalid token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("유효하지 않은 토큰입니다."));
            }
            
            // 3. JWT 토큰에서 Client ID 추출 (로깅용)
            String clientId = jwtUtil.extractClientId(authAccessToken);
            log.info("Token validation successful for client: {}", clientId);
            
            // 4. 사용자 등록 처리
            UserRegisterResponse response = userService.registerUser(request);
            
            log.info("User registration successful - userSeqNo: {}", 
                    response.getUserSeqNo());
            
            return ResponseEntity.ok(
                    ApiResponse.success("사용자 계좌가 성공적으로 등록되었습니다.", response)
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("User registration failed - Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("User registration failed - System error: {}", e.getMessage(), e);
            String errorMessage = "사용자 등록 중 시스템 오류가 발생했습니다: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(errorMessage));
        }
    }
    
} 