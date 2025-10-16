package com.hanafuture.openbanking.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {
    
    private final SecretKey secretKey;
    
    @Autowired
    public JwtUtil(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    
    /**
     * Authorization 헤더에서 토큰 추출
     */
    public String extractTokenFromHeader(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 올바르지 않습니다. 'Bearer {token}' 형식이어야 합니다.");
        }
        return authorization.substring(7); // "Bearer " 제거
    }

    /**
     * Access Token 생성 (Client Credentials Grant용)
     * 만료기간: 90일 (7,776,000초)
     */
    public String generateAccessToken(String clientId, String scope, Duration expiry) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expiry);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("clientId", clientId);
        claims.put("scope", scope);
        claims.put("type", "access");
        
        log.info("JWT Access Token 생성 중 - clientId: {}, scope: {}, expiry: {}초", 
                clientId, scope, expiry.toSeconds());
        
        String token = Jwts.builder()
                .claims(claims)
                .subject(clientId) // Client Credentials에서는 clientId를 subject로 사용
                .issuer("https://www.openbanking.or.kr")
                .audience().add(clientId).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .id(UUID.randomUUID().toString())
                .signWith(secretKey)
                .compact();
        
        log.info("JWT Access Token 생성 완료 - 페이로드에 scope '{}' 포함됨", scope);
        return token;
    }
    
    /**
     * JWT 토큰에서 Claims 추출
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * JWT 토큰에서 Client ID 추출
     */
    public String extractClientId(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("clientId", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰에서 Client ID를 추출할 수 없습니다: " + e.getMessage());
        }
    }
    
    /**
     * JWT 토큰에서 Scope 추출
     */
    public String extractScope(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("scope", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰에서 Scope를 추출할 수 없습니다: " + e.getMessage());
        }
    }
    
    /**
     * JWT 토큰의 전체 Claims 정보를 로그로 출력 (디버깅용)
     */
    public void debugTokenClaims(String token) {
        try {
            Claims claims = extractClaims(token);
            log.info("=== JWT 토큰 페이로드 디버깅 ===");
            log.info("Subject: {}", claims.getSubject());
            log.info("Issuer: {}", claims.getIssuer());
            log.info("ClientId: {}", claims.get("clientId"));
            log.info("Scope: {}", claims.get("scope"));
            log.info("Type: {}", claims.get("type"));
            log.info("IssuedAt: {}", claims.getIssuedAt());
            log.info("Expiration: {}", claims.getExpiration());
            log.info("JTI: {}", claims.getId());
            log.info("================================");
        } catch (Exception e) {
            log.error("JWT 토큰 디버깅 실패: {}", e.getMessage());
        }
    }
} 