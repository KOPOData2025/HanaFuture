package com.hanaTI.HanaFuture.global.security;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    
    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }
    
    @Override
    public String getName() {
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return (String) properties.get("nickname");
    }
    
    @Override
    public String getEmail() {
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            String email = (String) kakaoAccount.get("email");
            if (email != null && !email.isEmpty()) {
                return email;
            }
        }
        
        // 이메일이 없으면 카카오 ID + 도메인으로 생성
        String kakaoId = getId();
        return kakaoId + "@kakao.local";
    }
    
    @Override
    public String getImageUrl() {
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return (String) properties.get("profile_image");
    }
}
