package com.hanaTI.HanaFuture.global.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OAuth2 인증 요청에 추가 파라미터를 설정하는 커스텀 리졸버
 * 카카오 로그인 시 항상 로그인 페이지를 표시하도록 prompt=login 파라미터 추가
 */
@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultAuthorizationRequestResolver = 
            new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultAuthorizationRequestResolver.resolve(request);
        return authorizationRequest != null ? customizeAuthorizationRequest(authorizationRequest) : null;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = 
            this.defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId);
        return authorizationRequest != null ? customizeAuthorizationRequest(authorizationRequest) : null;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        // 기본적으로는 추가 파라미터 없이 OAuth2의 정상적인 동작을 허용
        // 기존 사용자는 카카오 세션이 있으면 바로 로그인, 신규 사용자는 인증 페이지 표시
        return authorizationRequest;
    }
}
