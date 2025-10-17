package com.hanaTI.HanaFuture.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerificationRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다")
    private String phoneNumber;
    
    @NotBlank(message = "주민등록번호는 필수입니다")
    @Pattern(regexp = "^\\d{13}$", message = "주민등록번호는 13자리 숫자여야 합니다")
    private String residentNumber; // 마스킹 제거된 실제 번호
    
    @NotBlank(message = "통신사는 필수입니다")
    private String carrier; // 통신사 (SKT, KT, LGU, etc.)
}
