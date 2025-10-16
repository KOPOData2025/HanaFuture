package com.hanafuture.openbanking.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalAuthResponse {
    
    private boolean success; // 성공 여부
    
    private String message; // 메시지
    
    private String userCi; // 사용자 CI (고객식별정보)
    
    private String verifyCode; // 문자인증번호
} 