package com.hanafuture.openbanking.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth 2.0 토큰 발급 요청")
public class TokenRequest {
    
    @Schema(description = "클라이언트 ID", example = "F999999999", required = true)
    private String clientId; // 오픈뱅킹에서 발급한 이용기관 앱의 Client ID
    
    @Schema(description = "클라이언트 시크릿 (평문)", example = "A111111111", required = true)
    private String clientSecret; // 오픈뱅킹에서 발급한 이용기관 앱의 Client Secret
    
    @Schema(description = "권한 범위 (고정값: sa)", example = "sa", required = true)
    private String scope; // Access Token 권한 범위 (고정값: "sa")
    
    @Schema(description = "인증 방식 (고정값: client_credentials)", example = "client_credentials", required = true)
    private String grantType; // 고정값: "client_credentials"
} 