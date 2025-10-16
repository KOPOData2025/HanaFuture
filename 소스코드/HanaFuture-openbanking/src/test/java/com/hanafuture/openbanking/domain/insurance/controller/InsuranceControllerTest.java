package com.hanafuture.openbanking.domain.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanafuture.openbanking.domain.insurance.dto.request.InsuranceListRequest;
import com.hanafuture.openbanking.domain.insurance.dto.response.InsuranceListResponse;
import com.hanafuture.openbanking.domain.insurance.service.InsuranceService;
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

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("보험 조회 컨트롤러 테스트")
class InsuranceControllerTest {

    @Mock
    private InsuranceService insuranceService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private InsuranceController insuranceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(insuranceController).build();
        objectMapper = new ObjectMapper();
    }

    private InsuranceListRequest createValidInsuranceListRequest() {
        InsuranceListRequest request = new InsuranceListRequest();
        ReflectionTestUtils.setField(request, "bankTranId", "BANKTRAN123456789012");
        ReflectionTestUtils.setField(request, "userSeqNo", "U001111111");
        ReflectionTestUtils.setField(request, "bankCodeStd", "436");
        return request;
    }

    @Test
    @DisplayName("보험 목록 조회 성공")
    void getInsuranceList_Success() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        
        InsuranceListResponse.InsuInfo insuInfo = InsuranceListResponse.InsuInfo.builder()
            .insuNum("INSU1234567890123456")
            .prodName("자동차종합보험")
            .insuType("01")
            .insuranceCompany("교보생명")
            .insuStatus("02")
            .issueDate("20200101")
            .expDate("20250101")
            .build();
        
        InsuranceListResponse response = InsuranceListResponse.builder()
            .insuCnt(1)
            .insuList(Arrays.asList(insuInfo))
            .build();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자 보험 목록이 성공적으로 조회되었습니다."))
                .andExpect(jsonPath("$.data.insuCnt").value(1))
                .andExpect(jsonPath("$.data.insuList").isArray())
                .andExpect(jsonPath("$.data.insuList[0].insuNum").value("INSU1234567890123456"))
                .andExpect(jsonPath("$.data.insuList[0].prodName").value("자동차종합보험"))
                .andExpect(jsonPath("$.data.insuList[0].insuType").value("01"))
                .andExpect(jsonPath("$.data.insuList[0].insuranceCompany").value("교보생명"))
                .andExpect(jsonPath("$.data.insuList[0].insuStatus").value("02"))
                .andExpect(jsonPath("$.data.insuList[0].issueDate").value("20200101"))
                .andExpect(jsonPath("$.data.insuList[0].expDate").value("20250101"));
    }

    @Test
    @DisplayName("보험 목록 조회 성공 - 보험 없음")
    void getInsuranceList_Success_EmptyList() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        
        InsuranceListResponse response = InsuranceListResponse.builder()
            .insuCnt(0)
            .insuList(Collections.emptyList())
            .build();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자 보험 목록이 성공적으로 조회되었습니다."))
                .andExpect(jsonPath("$.data.insuCnt").value(0))
                .andExpect(jsonPath("$.data.insuList").isArray())
                .andExpect(jsonPath("$.data.insuList").isEmpty());
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - 유효하지 않은 토큰")
    void getInsuranceList_InvalidToken() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("invalid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(false);

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - Authorization 헤더 누락")
    void getInsuranceList_MissingAuthHeader() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - 잘못된 요청 데이터")
    void getInsuranceList_InvalidRequest() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        ReflectionTestUtils.setField(request, "bankTranId", ""); // 빈 값
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willThrow(new IllegalArgumentException("거래고유번호는 필수 항목입니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("거래고유번호는 필수 항목입니다."));
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - 잘못된 사용자 고유번호")
    void getInsuranceList_InvalidUserSeqNo() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        ReflectionTestUtils.setField(request, "userSeqNo", "INVALID_USER_SEQ_NO");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willThrow(new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - 잘못된 보험사 코드")
    void getInsuranceList_InvalidBankCodeStd() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        ReflectionTestUtils.setField(request, "bankCodeStd", "999"); // 잘못된 보험사 코드
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willThrow(new IllegalArgumentException("지원하지 않는 보험사 코드입니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("지원하지 않는 보험사 코드입니다."));
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - 시스템 오류")
    void getInsuranceList_SystemError() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willThrow(new RuntimeException("외부 보험사 시스템 연결 오류"));

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("보험 목록 조회 중 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("보험 목록 조회 실패 - 잘못된 JSON 형식")
    void getInsuranceList_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("보험 목록 조회 성공 - 다중 보험 계약")
    void getInsuranceList_Success_MultipleInsurances() throws Exception {
        // Given
        InsuranceListRequest request = createValidInsuranceListRequest();
        
        InsuranceListResponse.InsuInfo insuInfo1 = InsuranceListResponse.InsuInfo.builder()
            .insuNum("INSU1234567890123456")
            .prodName("자동차종합보험")
            .insuType("01")
            .insuranceCompany("교보생명")
            .insuStatus("02")
            .issueDate("20200101")
            .expDate("20250101")
            .build();
        
        InsuranceListResponse.InsuInfo insuInfo2 = InsuranceListResponse.InsuInfo.builder()
            .insuNum("INSU9876543210987654")
            .prodName("건강보험")
            .insuType("02")
            .insuranceCompany("삼성생명")
            .insuStatus("01")
            .issueDate("20210101")
            .expDate("20260101")
            .build();
        
        InsuranceListResponse response = InsuranceListResponse.builder()
            .insuCnt(2)
            .insuList(Arrays.asList(insuInfo1, insuInfo2))
            .build();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(insuranceService.getInsuranceList(any(InsuranceListRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/insurances")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자 보험 목록이 성공적으로 조회되었습니다."))
                .andExpect(jsonPath("$.data.insuCnt").value(2))
                .andExpect(jsonPath("$.data.insuList").isArray())
                .andExpect(jsonPath("$.data.insuList[0].insuNum").value("INSU1234567890123456"))
                .andExpect(jsonPath("$.data.insuList[0].prodName").value("자동차종합보험"))
                .andExpect(jsonPath("$.data.insuList[1].insuNum").value("INSU9876543210987654"))
                .andExpect(jsonPath("$.data.insuList[1].prodName").value("건강보험"));
    }
} 