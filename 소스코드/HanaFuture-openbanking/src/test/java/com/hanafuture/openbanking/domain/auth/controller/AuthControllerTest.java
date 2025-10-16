package com.hanafuture.openbanking.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanafuture.openbanking.domain.auth.dto.request.TokenRequest;
import com.hanafuture.openbanking.domain.auth.dto.response.TokenResponse;
import com.hanafuture.openbanking.domain.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth 2.0 인증 컨트롤러 테스트")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 성공")
    void issueToken_Success() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            "F999999999",
            "A111111111", 
            "sa",
            "client_credentials"
        );
        
        TokenResponse tokenResponse = TokenResponse.builder()
            .authAccessToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...")
            .tokenType("Bearer")
            .expiresIn(3600)
            .scope("sa")
            .clientUseCode("F999999999")
            .build();
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willReturn(tokenResponse);

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("토큰이 성공적으로 발급되었습니다."))
                .andExpect(jsonPath("$.data.authAccessToken").value("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.data.scope").value("sa"))
                .andExpect(jsonPath("$.data.clientUseCode").value("F999999999"));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 잘못된 client_id")
    void issueToken_InvalidClientId() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            "INVALID_CLIENT_ID",
            "A111111111",
            "sa",
            "client_credentials"
        );
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willThrow(new IllegalArgumentException("유효하지 않은 클라이언트 ID입니다."));

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 클라이언트 ID입니다."));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 잘못된 client_secret")
    void issueToken_InvalidClientSecret() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            "F999999999",
            "INVALID_SECRET",
            "sa",
            "client_credentials"
        );
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willThrow(new IllegalArgumentException("유효하지 않은 클라이언트 시크릿입니다."));

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 클라이언트 시크릿입니다."));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 잘못된 grant_type")
    void issueToken_InvalidGrantType() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            "F999999999",
            "A111111111",
            "sa",
            "authorization_code"
        );
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willThrow(new IllegalArgumentException("지원하지 않는 grant_type입니다."));

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("지원하지 않는 grant_type입니다."));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 잘못된 scope")
    void issueToken_InvalidScope() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            "F999999999",
            "A111111111",
            "invalid_scope",
            "client_credentials"
        );
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willThrow(new IllegalArgumentException("지원하지 않는 scope입니다."));

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("지원하지 않는 scope입니다."));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 시스템 오류")
    void issueToken_SystemError() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            "F999999999",
            "A111111111",
            "sa",
            "client_credentials"
        );
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("토큰 발급 중 시스템 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 필수 파라미터 누락")
    void issueToken_MissingParameter() throws Exception {
        // Given
        TokenRequest tokenRequest = new TokenRequest(
            null,  // clientId 누락
            "A111111111",
            "sa",
            "client_credentials"
        );
        
        given(authService.issueToken(any(TokenRequest.class)))
            .willThrow(new IllegalArgumentException("클라이언트 ID는 필수 파라미터입니다."));

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("클라이언트 ID는 필수 파라미터입니다."));
    }

    @Test
    @DisplayName("OAuth 2.0 토큰 발급 실패 - 잘못된 JSON 형식")
    void issueToken_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/oauth/2.0/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
} 