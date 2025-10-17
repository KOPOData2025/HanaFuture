package com.hanaTI.HanaFuture.domain.auth.controller;

import com.hanaTI.HanaFuture.domain.auth.dto.*;
import com.hanaTI.HanaFuture.domain.auth.dto.ExtendedSignUpRequest;
import com.hanaTI.HanaFuture.domain.auth.dto.request.SmsVerificationRequest;
import com.hanaTI.HanaFuture.domain.auth.dto.response.SmsVerificationResponse;
import com.hanaTI.HanaFuture.domain.auth.service.AuthService;
import com.hanaTI.HanaFuture.domain.verification.service.PhoneVerificationService;
import com.hanaTI.HanaFuture.domain.verification.dto.PhoneVerificationRequest;
import com.hanaTI.HanaFuture.domain.verification.dto.VerificationCodeRequest;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;
    
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자 계정을 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 이메일"
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        TokenResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", response));
    }
    
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", response));
    }
    
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 Refresh Token"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", response));
    }
    
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자를 로그아웃시킵니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다.", null));
    }
    
    @Operation(
            summary = "사용자 정보 조회",
            description = "현재 로그인된 사용자의 정보를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다. 유효한 액세스 토큰을 제공해주세요."));
        }
        
        UserResponse response = authService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("사용자 정보를 조회했습니다.", response));
    }
    
    @Operation(
            summary = "확장 회원가입",
            description = "복지 혜택 맞춤 추천을 위한 상세 정보를 포함한 회원가입입니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "확장 회원가입 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 이메일"
            )
    })
    @PostMapping("/signup/extended")
    public ResponseEntity<ApiResponse<TokenResponse>> extendedSignUp(@Valid @RequestBody ExtendedSignUpRequest request) {
        TokenResponse response = authService.extendedSignUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("맞춤 혜택 회원가입이 완료되었습니다.", response));
    }
    
    @Operation(
            summary = "사용자 정보 업데이트",
            description = "OAuth2 사용자의 추가 정보를 업데이트합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/update-info")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserInfo(
            @Valid @RequestBody OAuth2UserUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        UserResponse response = authService.updateOAuth2UserInfo(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보가 업데이트되었습니다.", response));
    }
    
    @Operation(
            summary = "OAuth2 신규 사용자 등록",
            description = "임시 토큰으로 OAuth2 신규 사용자의 추가 정보를 입력하여 실제 사용자로 등록합니다."
    )
    @PostMapping("/oauth2/register")
    public ResponseEntity<ApiResponse<TokenResponse>> registerOAuth2User(
            @Valid @RequestBody OAuth2UserUpdateRequest request,
            @RequestParam("tempToken") String tempToken) {
        
        TokenResponse response = authService.registerOAuth2UserWithAdditionalInfo(tempToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("OAuth2 사용자 등록이 완료되었습니다.", response));
    }
    
    @Operation(
            summary = "SMS 인증번호 발송",
            description = "본인 인증을 위한 SMS 인증번호를 발송합니다."
    )
    @PostMapping("/send-sms")
    public ResponseEntity<ApiResponse<SmsVerificationResponse>> sendSmsVerification(
            @Valid @RequestBody SmsVerificationRequest request) {
        
        try {
            log.info("SMS 인증번호 발송 요청: 이름 {}, 휴대폰 {}, 통신사 {}", 
                    request.getName(), request.getPhoneNumber(), request.getCarrier());
            
            // 기존 PhoneVerificationService 사용
            PhoneVerificationRequest phoneRequest = new PhoneVerificationRequest();
            phoneRequest.setPhoneNumber(request.getPhoneNumber());
            phoneRequest.setPurpose("본인인증");
            
            var phoneResponse = phoneVerificationService.sendVerificationCode(phoneRequest);
            
            if (phoneResponse.isSent()) {
                SmsVerificationResponse response = SmsVerificationResponse.builder()
                        .verificationId("VERIFY_" + System.currentTimeMillis())
                        .smsSent(true)
                        .message("인증번호가 발송되었습니다.")
                        .expiryMinutes(5)
                        .build();
                
                return ResponseEntity.ok(
                        ApiResponse.success("SMS 인증번호가 발송되었습니다.", response)
                );
            } else {
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.error(phoneResponse.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("SMS 발송 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("SMS 발송에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "SMS 인증번호 확인",
            description = "발송된 SMS 인증번호를 확인합니다."
    )
    @PostMapping("/verify-sms")
    public ResponseEntity<ApiResponse<Boolean>> verifySmsCode(
            @RequestParam String phoneNumber,
            @RequestParam String code) {
        
        try {
            log.info("SMS 인증번호 확인 요청: 휴대폰 {}, 코드 {}", phoneNumber, code);
            
            // 기존 PhoneVerificationService 사용
            VerificationCodeRequest verifyRequest = new VerificationCodeRequest();
            verifyRequest.setPhoneNumber(phoneNumber);
            verifyRequest.setVerificationCode(code);
            
            boolean isValid = phoneVerificationService.verifyCode(verifyRequest);
            
            if (isValid) {
                return ResponseEntity.ok(
                        ApiResponse.success("인증이 완료되었습니다.", true)
                );
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("인증번호가 올바르지 않습니다."));
            }
            
        } catch (Exception e) {
            log.error("SMS 인증 확인 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("인증 확인 중 오류가 발생했습니다."));
        }
    }
}
