package com.hanafuture.openbanking.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanafuture.openbanking.domain.user.dto.request.UserRegisterRequest;
import com.hanafuture.openbanking.domain.user.dto.response.UserRegisterResponse;
import com.hanafuture.openbanking.domain.user.service.UserService;
import com.hanafuture.openbanking.global.util.JwtUtil;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 관리 컨트롤러 테스트")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    private UserRegisterRequest createValidUserRegisterRequest() {
        UserRegisterRequest request = new UserRegisterRequest();
        ReflectionTestUtils.setField(request, "bankTranId", "12345678901234567890");
        ReflectionTestUtils.setField(request, "bankCodeStd", "004");
        ReflectionTestUtils.setField(request, "registerAccountNum", "1234567890123456");
        ReflectionTestUtils.setField(request, "userInfo", "19901201");
        ReflectionTestUtils.setField(request, "userName", "홍길동");
        ReflectionTestUtils.setField(request, "userEmail", "hong@example.com");
        ReflectionTestUtils.setField(request, "userCi", "abcd1234567890abcd1234567890abcd1234567890abcd1234567890abcd1234");
        ReflectionTestUtils.setField(request, "scope", "inquiry");
        return request;
    }

    @Test
    @DisplayName("사용자 계좌 등록 성공")
    void registerUser_Success() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        UserRegisterResponse response = new UserRegisterResponse(
            "U123456789",
            "A1B2C3D4E5F6G7H8",
            "1234567890123456"
        );
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자 계좌가 성공적으로 등록되었습니다."))
                .andExpect(jsonPath("$.data.userSeqNo").value("U123456789"))
                .andExpect(jsonPath("$.data.fintechUseNum").value("A1B2C3D4E5F6G7H8"))
                .andExpect(jsonPath("$.data.accountNum").value("1234567890123456"));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 유효하지 않은 토큰")
    void registerUser_InvalidToken() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("invalid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(false);

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - Authorization 헤더 누락")
    void registerUser_MissingAuthHeader() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 잘못된 요청 데이터")
    void registerUser_InvalidRequest() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        ReflectionTestUtils.setField(request, "bankTranId", ""); // 빈 값
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willThrow(new IllegalArgumentException("거래고유번호는 20자리여야 합니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("거래고유번호는 20자리여야 합니다."));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 잘못된 은행코드")
    void registerUser_InvalidBankCode() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        ReflectionTestUtils.setField(request, "bankCodeStd", "999"); // 잘못된 은행코드
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willThrow(new IllegalArgumentException("지원하지 않는 은행코드입니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("지원하지 않는 은행코드입니다."));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 잘못된 계좌번호")
    void registerUser_InvalidAccountNum() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        ReflectionTestUtils.setField(request, "registerAccountNum", "12345"); // 잘못된 계좌번호
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willThrow(new IllegalArgumentException("계좌번호는 16자리여야 합니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("계좌번호는 16자리여야 합니다."));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 잘못된 scope")
    void registerUser_InvalidScope() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        ReflectionTestUtils.setField(request, "scope", "invalid_scope"); // 잘못된 scope
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willThrow(new IllegalArgumentException("scope는 inquiry 또는 transfer여야 합니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("scope는 inquiry 또는 transfer여야 합니다."));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 시스템 오류")
    void registerUser_SystemError() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사용자 등록 중 시스템 오류가 발생했습니다: 데이터베이스 연결 오류"));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 중복된 사용자")
    void registerUser_DuplicateUser() throws Exception {
        // Given
        UserRegisterRequest request = createValidUserRegisterRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(userService.registerUser(any(UserRegisterRequest.class)))
            .willThrow(new IllegalArgumentException("이미 등록된 사용자입니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 등록된 사용자입니다."));
    }

    @Test
    @DisplayName("사용자 계좌 등록 실패 - 잘못된 JSON 형식")
    void registerUser_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);

        // When & Then
        mockMvc.perform(post("/v2.0/user/register")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
} 