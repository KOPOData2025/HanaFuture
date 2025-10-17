package com.hanaTI.HanaFuture.domain.verification.controller;

import com.hanaTI.HanaFuture.domain.verification.dto.PhoneVerificationRequest;
import com.hanaTI.HanaFuture.domain.verification.dto.PhoneVerificationResponse;
import com.hanaTI.HanaFuture.domain.verification.dto.VerificationCodeRequest;
import com.hanaTI.HanaFuture.domain.verification.service.PhoneVerificationService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@Tag(name = "휴대폰 인증", description = "휴대폰 본인인증 관련 API")
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/phone/send")
    @Operation(summary = "휴대폰 인증번호 발송", description = "휴대폰으로 인증번호를 발송합니다.")
    public ResponseEntity<ApiResponse<PhoneVerificationResponse>> sendVerificationCode(
            @Valid @RequestBody PhoneVerificationRequest request) {
        
        PhoneVerificationResponse response = phoneVerificationService.sendVerificationCode(request);
        
        if (response.isSent()) {
            return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다.", response));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, response.getMessage(), response));
        }
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "휴대폰 인증번호 확인", description = "발송된 인증번호를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(
            @Valid @RequestBody VerificationCodeRequest request) {
        
        boolean verified = phoneVerificationService.verifyCode(request);
        
        return ResponseEntity.ok(ApiResponse.success("인증이 완료되었습니다.", verified));
    }
}
