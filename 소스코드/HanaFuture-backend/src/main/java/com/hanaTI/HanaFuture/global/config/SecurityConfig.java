package com.hanaTI.HanaFuture.global.config;

import com.hanaTI.HanaFuture.global.security.CustomUserDetailsService;
import com.hanaTI.HanaFuture.global.security.JwtAuthenticationEntryPoint;
import com.hanaTI.HanaFuture.global.security.JwtAuthenticationFilter;
import com.hanaTI.HanaFuture.global.security.CustomOAuth2UserService;
import com.hanaTI.HanaFuture.global.security.OAuth2AuthenticationSuccessHandler;
import com.hanaTI.HanaFuture.global.security.OAuth2AuthenticationFailureHandler;
import com.hanaTI.HanaFuture.global.security.CustomOAuth2AuthorizationRequestResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",               // 로그인/회원가입/SMS 인증 제외
                    "/api/health/**",             // 헬스체크
                    "/api/test/**",               // 테스트 API (임시)
                    "/api/welfare/**",            // 공개 복지 정보
                    "/api/childcare-welfare/**", // 공개 육아 복지 정보
                    "/api/smart-welfare/**",     // AI 필터링 복지 정보 (테스트용)
                    "/api/welfare-stats/**",     // 복지 통계 (테스트용)
                    "/api/hana-future-welfare/**", // 하나퓨처 맞춤 복지 정보 (테스트용)
                    "/api/simple-ai-filter/**", // 간단한 AI 필터링 (테스트용)
                    "/api/banking/**",            // 통합 벤킹 API (테스트용)
                    "/api/openbanking/**",        // 오픈뱅킹 API (테스트용)
                    "/api/savings/**",            // 함께 적금 API (테스트용)
                    "/api/user/bank-accounts/**", // 사용자 계좌 연동 API (테스트용)
                    "/api/group-accounts/*/invite-info", // 초대 정보 조회 (인증 불필요)
                    "/api/group-accounts/*/create-pending-invite", // PENDING 초대 생성 (인증 필요하나 테스트용)
                    "/api/notifications/**",      // 알림 API (JWT 인증 필요하지만 에러 방지용)
                    "/notifications/**",          // 알림 경로 (에러 방지용)
                    "/mock/**",                   // Mock API 엔드포인트 (테스트용)
                    "/actuator/**",               // 모니터링
                    "/swagger-ui/**", 
                    "/v3/api-docs/**", 
                    "/swagger-ui.html",
                    "/error",
                    "/login/oauth2/code/**"       // OAuth2 콜백
                ).permitAll()
                //  나머지는 모두 JWT 인증 필요!
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .authorizationRequestResolver(customOAuth2AuthorizationRequestResolver)
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
