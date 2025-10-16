package com.hanafuture.openbanking.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizeResponse {
    
    private String verifyCode; // 문자인증번호
    
    private String clientInfo; // 이용기관이 세팅한 임의의 정보
} 