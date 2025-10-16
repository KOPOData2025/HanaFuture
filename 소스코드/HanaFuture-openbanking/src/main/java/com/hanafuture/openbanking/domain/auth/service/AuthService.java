package com.hanafuture.openbanking.domain.auth.service;

import com.hanafuture.openbanking.domain.auth.dto.request.TokenRequest;
import com.hanafuture.openbanking.domain.auth.dto.response.TokenResponse;
import com.hanafuture.openbanking.domain.client.entity.Client;
import com.hanafuture.openbanking.domain.client.repository.ClientRepository;
import com.hanafuture.openbanking.global.config.JwtConfig;
import com.hanafuture.openbanking.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {
    
    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public TokenResponse issueToken(TokenRequest request) {
        
        // 1. 요청 유효성 검사
        validateTokenRequest(request);
        
        // 2. 클라이언트 인증
        Client client = authenticateClient(request.getClientId(), request.getClientSecret());
        
        // 3. JWT 토큰 생성 (90일 만료)
        String accessToken = jwtUtil.generateAccessToken(
                request.getClientId(),
                request.getScope(),
                JwtConfig.ACCESS_TOKEN_EXPIRY
        );
        
        // 4. 생성된 토큰의 페이로드 디버깅 (scope 포함 확인)
        jwtUtil.debugTokenClaims(accessToken);
        
        log.info("Token issued successfully for client: {}, scope: {}, expiry: {}초", 
                request.getClientId(), request.getScope(), JwtConfig.ACCESS_TOKEN_EXPIRY_SECONDS);
        
        // 5. 응답 생성
        return TokenResponse.builder()
                .authAccessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(JwtConfig.ACCESS_TOKEN_EXPIRY_SECONDS)  // 90일 = 7,776,000초
                .scope(request.getScope())
                .clientUseCode(client.getClientUseCode())
                .build();
    }
    
    private void validateTokenRequest(TokenRequest request) {
        
        // Grant Type 검증
        if (!"client_credentials".equals(request.getGrantType())) {
            throw new IllegalArgumentException("지원하지 않는 grant_type입니다. 'client_credentials'만 지원됩니다.");
        }
        
        // Client ID 검증
        if (request.getClientId() == null || request.getClientId().trim().isEmpty()) {
            throw new IllegalArgumentException("client_id는 필수입니다.");
        }
        
        // Client Secret 검증
        if (request.getClientSecret() == null || request.getClientSecret().trim().isEmpty()) {
            throw new IllegalArgumentException("client_secret는 필수입니다.");
        }
        
        // Scope 검증
        if (request.getScope() == null || request.getScope().trim().isEmpty()) {
            throw new IllegalArgumentException("scope는 필수입니다.");
        }
        
        // Scope 값 검증 (고정값 "sa")
        if (!"sa".equals(request.getScope())) {
            throw new IllegalArgumentException("지원하지 않는 scope입니다. 'sa'만 지원됩니다.");
        }
    }
    
    private Client authenticateClient(String clientId, String clientSecret) {
        
        // 클라이언트 조회
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 client_id입니다."));
        
        // 요청받은 client_secret을 SHA256으로 해시
        String hashedClientSecret = sha256Hash(clientSecret);
        
        // 클라이언트 시크릿 검증 (해시값 비교)
        if (!hashedClientSecret.equals(client.getClientSecret())) {
            throw new IllegalArgumentException("잘못된 client_secret입니다.");
        }
        
        log.info("Client authentication successful for client_id: {}", clientId);
        return client;
    }
    
    /**
     * SHA256 해시 생성
     */
    private String sha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // 바이트 배열을 16진수 문자열로 변환 (소문자)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = String.format("%02x", b);  // 소문자 16진수, 2자리 패딩
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
} 