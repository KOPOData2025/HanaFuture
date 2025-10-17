package com.hanaTI.HanaFuture.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsVerificationResponse {
    
    private String verificationId; // 인증 ID
    private boolean smsSent; // SMS 발송 성공 여부
    private String message; // 결과 메시지
    private int expiryMinutes; // 인증 유효 시간 (분)
}

