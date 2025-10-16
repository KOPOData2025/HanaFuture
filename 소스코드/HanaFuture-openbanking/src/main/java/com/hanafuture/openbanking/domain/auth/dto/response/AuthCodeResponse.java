package com.hanafuture.openbanking.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCodeResponse {
    
    private String code; // 생성된 authorization code
    
    private String scope; // 권한 범위
    
    private String state; // CSRF 방지용 문자열
} 