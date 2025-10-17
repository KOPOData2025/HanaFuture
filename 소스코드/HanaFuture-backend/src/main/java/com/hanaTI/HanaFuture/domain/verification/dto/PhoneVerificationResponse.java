package com.hanaTI.HanaFuture.domain.verification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PhoneVerificationResponse {
    
    private String phoneNumber;
    private boolean sent;
    private String message;
    private LocalDateTime expiryTime;
    private int expiryMinutes;
    
    public static PhoneVerificationResponse success(String phoneNumber, LocalDateTime expiryTime, int expiryMinutes) {
        return PhoneVerificationResponse.builder()
                .phoneNumber(phoneNumber)
                .sent(true)
                .message("인증번호가 발송되었습니다.")
                .expiryTime(expiryTime)
                .expiryMinutes(expiryMinutes)
                .build();
    }
    
    public static PhoneVerificationResponse failure(String phoneNumber, String message) {
        return PhoneVerificationResponse.builder()
                .phoneNumber(phoneNumber)
                .sent(false)
                .message(message)
                .build();
    }
}
