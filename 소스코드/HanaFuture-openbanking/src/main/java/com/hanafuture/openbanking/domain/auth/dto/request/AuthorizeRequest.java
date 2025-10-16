package com.hanafuture.openbanking.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizeRequest {
    
    @Builder.Default
    private String responseType = "code"; // 디폴트값 "code"
    
    private String clientId; // 오픈뱅킹에서 발급한 이용기관 앱의 Client ID (예: openbank_client_001)
    
    private String redirectUri; // 사용자인증이 성공하면 이용기관으로 연결되는 URL
    
    private String scope; // Access Token 권한 범위 (예: "account_info card_info")
    
    private String state; // CSRF보안위협에 대응하기 위해 이용기관이 세팅하는 난수값
    
    private String authType; // 사용자인증타입 구분 (예: "login inquiry transfer fintechinfo insuinfo")
    
    @Builder.Default
    private String cellphoneCertYn = "Y"; // 휴대전화 인증 사용여부 (미지정시 "Y")
    
    private String clientInfo; // 이용기관이 세팅하는 임의의 정보 (예: "이철우,111222-1111111,010-3614-7328,chulwoo@naver.com")
} 