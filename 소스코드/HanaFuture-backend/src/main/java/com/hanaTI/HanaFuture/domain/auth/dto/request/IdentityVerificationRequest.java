package com.hanaTI.HanaFuture.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationRequest {
    
    @NotBlank(message = "주민등록번호는 필수입니다")
    @Pattern(regexp = "^\\d{6}-\\d{7}$", message = "주민등록번호 형식이 올바르지 않습니다")
    private String residentNumber; // 주민등록번호 (123456-1234567)
    
    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다")
    private String phoneNumber; // 휴대폰 번호 (010-1234-5678)
    
    @NotBlank(message = "이름은 필수입니다")
    private String name; // 이름
}

