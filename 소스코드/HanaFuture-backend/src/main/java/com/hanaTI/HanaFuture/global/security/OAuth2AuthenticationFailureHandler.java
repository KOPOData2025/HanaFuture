package com.hanaTI.HanaFuture.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String FRONTEND_URL = "https://hanafuture.com";
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        
        log.error("OAuth2 인증 실패: {}", exception.getMessage(), exception);
        
        String errorMessage = exception.getMessage();
        if (errorMessage == null) {
            errorMessage = "OAuth2 인증 중 알 수 없는 오류가 발생했습니다.";
        }
        
        String errorCode = "authentication_failed";
        
        // 에러 타입에 따른 분류
        if (errorMessage.contains("OAuth2AuthenticationException")) {
            errorCode = "oauth2_failed";
        } else if (errorMessage.contains("email")) {
            errorCode = "email_required";
        } else if (errorMessage.contains("password")) {
            errorCode = "database_error";
        }
        
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/auth/error")
                .queryParam("error", errorCode)
                .build()
                .toUriString();
        
        log.info("OAuth2 인증 실패 리다이렉트: {}", targetUrl);
        
        // 명시적으로 302 리다이렉트 전송
        response.setStatus(HttpServletResponse.SC_FOUND);  // 302
        response.setHeader("Location", targetUrl);
        response.flushBuffer();
    }
}
