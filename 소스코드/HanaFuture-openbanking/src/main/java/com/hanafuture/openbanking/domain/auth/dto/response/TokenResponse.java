package com.hanafuture.openbanking.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "OAuth 2.0 토큰 발급 응답")
public class TokenResponse {
    
    @Schema(description = "JWT Access Token (90일 유효)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String authAccessToken; // 오픈뱅킹에서 발행된 Access Token
    
    @Schema(description = "토큰 타입 (고정값: Bearer)", example = "Bearer")
    private String tokenType; // Access Token 유형 고정값 "Bearer"
    
    @Schema(description = "토큰 만료 시간 (초 단위, 90일 = 7,776,000초)", example = "7776000")
    private long expiresIn; // Access Token 만료시간(초) - 90일
    
    @Schema(description = "권한 범위", example = "sa")
    private String scope; // Access Token 권한 범위
    
    @Schema(description = "클라이언트 이용 코드", example = "B222222222")
    private String clientUseCode; // 이용기관코드
} 