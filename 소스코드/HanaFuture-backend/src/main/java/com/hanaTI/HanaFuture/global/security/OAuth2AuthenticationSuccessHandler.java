package com.hanaTI.HanaFuture.global.security;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private static final String FRONTEND_URL = "https://hanafuture.com";
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        try {
            log.info("========== OAuth2 인증 성공 처리 시작 ==========");
            log.info("Frontend URL 설정값: {}", FRONTEND_URL);
            
            // Principal 타입 확인
            Object principal = authentication.getPrincipal();
            log.info("Principal type: {}", principal.getClass().getSimpleName());
            
            OAuth2UserPrincipal userPrincipal = (OAuth2UserPrincipal) principal;
            User user = userPrincipal.getUser();
            
            log.info("사용자 정보: email={}, name={}", user.getEmail(), user.getName());
            
            // 임시 사용자인지 확인 (ID가 null이면 DB에 저장되지 않은 임시 사용자)
            if (user.getId() == null) {
                // 신규 OAuth2 사용자 - 추가 정보 입력 필요
                log.info("신규 OAuth2 사용자 - 추가 정보 입력 페이지로 리다이렉트: {}", user.getEmail());
                
                // 임시 토큰 생성 (사용자 정보를 포함한 특별한 토큰)
                String tempToken = jwtUtil.generateTempOAuth2Token(user.getEmail(), user.getName(), 
                                                                  user.getProvider().name(), user.getProviderId());
                
                String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/auth/additional-info")
                        .queryParam("tempToken", tempToken)  // URLEncoder 제거 - UriComponentsBuilder가 자동으로 인코딩
                        .queryParam("isNewUser", true)
                        .build()
                        .toUriString();
                        
                log.info("신규 OAuth2 사용자 리다이렉트: {} -> {}", user.getEmail(), targetUrl);
                
                // 명시적으로 302 리다이렉트 전송
                response.setStatus(HttpServletResponse.SC_FOUND);  // 302
                response.setHeader("Location", targetUrl);
                response.flushBuffer();
                return;
            }
            
            // 기존 사용자 - 일반적인 JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());
            
            log.info("JWT 토큰 생성 완료");
            
            // 리프레시 토큰을 DB에 저장
            user.updateRefreshToken(refreshToken);
            userRepository.save(user);
            
            log.info("리프레시 토큰 DB 저장 완료");
            
            // 추가 정보 입력 필요 여부 확인
            boolean needsAdditionalInfo = user.getBirthDate() == null || 
                                        user.getGender() == null || 
                                        user.getResidenceSido() == null;
            
            // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
            String callbackPath = needsAdditionalInfo ? "/auth/additional-info" : "/auth/oauth2/callback";
            String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + callbackPath)
                    .queryParam("token", accessToken)  // URLEncoder 제거
                    .queryParam("refresh", refreshToken)  // URLEncoder 제거
                    .queryParam("needsAdditionalInfo", needsAdditionalInfo)
                    .build()
                    .toUriString();
            
            log.info("OAuth2 로그인 성공: {} -> {}", user.getEmail(), targetUrl);
            
            // 명시적으로 302 리다이렉트 전송
            response.setStatus(HttpServletResponse.SC_FOUND);  // 302
            response.setHeader("Location", targetUrl);
            response.flushBuffer();
            
        } catch (Exception ex) {
            log.error("========== OAuth2 인증 성공 처리 중 오류 발생 ==========", ex);
            log.error("Exception type: {}", ex.getClass().getSimpleName());
            log.error("Exception message: {}", ex.getMessage());
            log.error("Frontend URL 설정값: {}", FRONTEND_URL);
            
            // 에러 발생 시 프론트엔드 에러 페이지로 리다이렉트
            String errorUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/auth/error")
                    .queryParam("error", "authentication_failed")
                    .build()
                    .toUriString();
            
            log.error("에러 리다이렉트 URL: {}", errorUrl);
            
            // 명시적으로 302 리다이렉트 전송
            response.setStatus(HttpServletResponse.SC_FOUND);  // 302
            response.setHeader("Location", errorUrl);
            response.flushBuffer();
        }
    }
}