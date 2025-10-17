package com.hanaTI.HanaFuture.global.util;

import com.hanaTI.HanaFuture.global.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final JwtProperties jwtProperties;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    
    public String generateAccessToken(String email, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public String generateRefreshToken(String email, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }
    
    public String extractTokenType(String token) {
        return extractClaims(token).get("type", String.class);
    }
    
    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean isAccessToken(String token) {
        return "access".equals(extractTokenType(token));
    }
    
    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }
    
    /**
     * OAuth2 신규 사용자를 위한 임시 토큰 생성
     * 추가 정보 입력 완료 후 실제 사용자 등록에 사용
     */
    public String generateTempOAuth2Token(String email, String name, String provider, String providerId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000); // 1시간 유효
        
        return Jwts.builder()
                .setSubject(email)
                .claim("name", name)
                .claim("provider", provider)
                .claim("providerId", providerId)
                .claim("type", "temp_oauth2")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 임시 OAuth2 토큰에서 사용자 정보 추출
     */
    public String extractProviderFromTempToken(String token) {
        return extractClaims(token).get("provider", String.class);
    }
    
    public String extractProviderIdFromTempToken(String token) {
        return extractClaims(token).get("providerId", String.class);
    }
    
    public String extractNameFromTempToken(String token) {
        return extractClaims(token).get("name", String.class);
    }
    
    public boolean isTempOAuth2Token(String token) {
        return "temp_oauth2".equals(extractTokenType(token));
    }
}
