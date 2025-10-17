package com.hanaTI.HanaFuture.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import com.hanaTI.HanaFuture.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // JWT 인증을 건너뛸 경로들
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
        "/api/auth/signup",
        "/api/auth/login", 
        "/api/auth/refresh",
        "/api/health",
        "/api/test",
        "/api/welfare",
        "/api/childcare-welfare",
        "/api/smart-welfare",
        "/api/welfare-stats",
        "/api/simple-ai-filter",
        "/api/hana-future-welfare",
        "/actuator/health",
        "/actuator/info",
        "/swagger-ui",
        "/v3/api-docs"
    );
    
    // AI API는 인증이 필요함 (welfare 경로에 포함되지만 예외)
    private static final List<String> AI_PATHS_REQUIRE_AUTH = Arrays.asList(
        "/api/ai/"
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            // 토큰이 없는 경우 - 인증이 필요한 엔드포인트에서는 Spring Security가 처리
            if (token == null) {
                log.debug(" Authorization 헤더에 토큰이 없음: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            
            log.debug(" JWT 토큰 수신: {} - 길이: {}", request.getRequestURI(), token.length());
            
            // 토큰 유효성 검증
            if (!jwtUtil.validateToken(token)) {
                log.warn("유효하지 않은 JWT 토큰: {}", request.getRequestURI());
                handleJwtException(response, "유효하지 않은 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            // 액세스 토큰인지 확인
            if (!jwtUtil.isAccessToken(token)) {
                log.warn("리프레시 토큰으로 API 접근 시도: {}", request.getRequestURI());
                handleJwtException(response, "액세스 토큰이 필요합니다.", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            String email = jwtUtil.extractEmail(token);
            
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT 인증 성공: {}", email);
                } catch (Exception e) {
                    log.warn("사용자 정보 로드 실패: {} - {}", email, e.getMessage());
                    handleJwtException(response, "사용자 정보를 찾을 수 없습니다.", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("JWT 필터에서 예상치 못한 오류 발생: {}", e.getMessage(), e);
            handleJwtException(response, "인증 처리 중 오류가 발생했습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // EXCLUDE_PATHS에 포함된 경로는 JWT 필터를 건너뜀
        boolean shouldSkip = EXCLUDE_PATHS.stream().anyMatch(excludePath -> 
            path.startsWith(excludePath) || path.contains(excludePath)
        );
        
        // AI API는 인증이 필요하므로 필터를 건너뛰지 않음 (단, 하나퓨처 API는 제외)
        boolean isAIPath = AI_PATHS_REQUIRE_AUTH.stream().anyMatch(aiPath -> 
            path.startsWith(aiPath) || path.contains(aiPath)
        ) && !path.contains("/api/hana-future-welfare");
        
        if (isAIPath) {
            log.debug(" AI API 인증 필요: {}", path);
            return false; // JWT 필터를 적용
        }
        
        if (shouldSkip) {
            log.debug("JWT 필터 건너뜀: {}", path);
        }
        
        return shouldSkip;
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * JWT 관련 예외 발생 시 클라이언트에게 JSON 응답을 반환
     */
    private void handleJwtException(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        
        ApiResponse<Object> errorResponse = ApiResponse.error(message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
