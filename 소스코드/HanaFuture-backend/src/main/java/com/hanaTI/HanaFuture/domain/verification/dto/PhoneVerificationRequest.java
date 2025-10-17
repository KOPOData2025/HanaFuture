package com.hanaTI.HanaFuture.domain.verification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneVerificationRequest {
    
    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String phoneNumber;
    
    private String purpose; // MYDATA_AUTH, SIGNUP, PASSWORD_RESET 등
}
