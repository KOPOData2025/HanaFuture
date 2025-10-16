package com.hanafuture.openbanking.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanafuture.openbanking.domain.account.dto.request.*;
import com.hanafuture.openbanking.domain.account.dto.response.*;
import com.hanafuture.openbanking.domain.account.service.AccountService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("계좌 관리 컨트롤러 테스트")
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    private AccountInfoRequest createValidAccountInfoRequest() {
        AccountInfoRequest request = new AccountInfoRequest();
        ReflectionTestUtils.setField(request, "userName", "홍길동");
        ReflectionTestUtils.setField(request, "userNum", "901201-1234567");
        ReflectionTestUtils.setField(request, "userEmail", "hong@example.com");
        ReflectionTestUtils.setField(request, "ainfoAgreeYn", "Y");
        ReflectionTestUtils.setField(request, "inquiryBankType", "1");
        ReflectionTestUtils.setField(request, "traceNo", "123456");
        ReflectionTestUtils.setField(request, "inquiryRecordCnt", "10");
        return request;
    }

    @Test
    @DisplayName("계좌통합조회 성공")
    void getAccountList_Success() throws Exception {
        // Given
        AccountInfoRequest request = createValidAccountInfoRequest();
        
        AccountInfoResponse response = new AccountInfoResponse();
        ReflectionTestUtils.setField(response, "resList", Arrays.asList(
            createAccountInfo("1234567890123456", "004", "KB국민은행")
        ));
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.getAccountList(any(AccountInfoRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/accountinfo/num_list")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("계좌통합조회가 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.data.resList").isArray());
    }

    @Test
    @DisplayName("계좌통합조회 실패 - Authorization 헤더 누락")
    void getAccountList_MissingAuthHeader() throws Exception {
        // Given
        AccountInfoRequest request = createValidAccountInfoRequest();

        // When & Then
        mockMvc.perform(post("/v2.0/accountinfo/num_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Authorization 헤더가 필요합니다. 'Authorization: Bearer {토큰}' 형식으로 요청해주세요."));
    }

    @Test
    @DisplayName("계좌통합조회 실패 - 유효하지 않은 토큰")
    void getAccountList_InvalidToken() throws Exception {
        // Given
        AccountInfoRequest request = createValidAccountInfoRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("invalid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(false);

        // When & Then
        mockMvc.perform(post("/v2.0/accountinfo/num_list")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("계좌 잔액 조회 성공")
    void getAccountBalance_Success() throws Exception {
        // Given
        BalanceRequest request = new BalanceRequest();
        ReflectionTestUtils.setField(request, "accountNum", "1234567890123456");
        
        BalanceResponse response = new BalanceResponse();
        ReflectionTestUtils.setField(response, "balanceAmt", "1000000");
        ReflectionTestUtils.setField(response, "accountNum", "1234567890123456");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(accountService.getAccountBalance(any(BalanceRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/account/balance/acnt_num")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("계좌 잔액 조회가 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("계좌 잔액 조회 실패 - 유효하지 않은 토큰")
    void getAccountBalance_InvalidToken() throws Exception {
        // Given
        BalanceRequest request = new BalanceRequest();
        ReflectionTestUtils.setField(request, "accountNum", "1234567890123456");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("invalid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(false);

        // When & Then
        mockMvc.perform(post("/v2.0/account/balance/acnt_num")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("거래내역조회 성공")
    void getTransactionList_Success() throws Exception {
        // Given
        TransactionListRequest request = new TransactionListRequest();
        ReflectionTestUtils.setField(request, "accountNum", "1234567890123456");
        ReflectionTestUtils.setField(request, "inquiryType", "A");
        ReflectionTestUtils.setField(request, "inquiryBase", "D");
        ReflectionTestUtils.setField(request, "fromDate", "20230101");
        ReflectionTestUtils.setField(request, "toDate", "20231231");
        
        TransactionListResponse response = new TransactionListResponse();
        ReflectionTestUtils.setField(response, "resList", Collections.emptyList());
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.getTransactionList(any(TransactionListRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/account/transaction_list/acnt_num")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("거래내역조회가 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("거래내역조회 실패 - 유효하지 않은 토큰")
    void getTransactionList_InvalidToken() throws Exception {
        // Given
        TransactionListRequest request = new TransactionListRequest();
        ReflectionTestUtils.setField(request, "accountNum", "1234567890123456");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("invalid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(false);

        // When & Then
        mockMvc.perform(post("/v2.0/account/transaction_list/acnt_num")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("출금이체 성공")
    void withdraw_Success() throws Exception {
        // Given
        WithdrawRequest request = new WithdrawRequest();
        ReflectionTestUtils.setField(request, "wdAccountNum", "1234567890123456");
        ReflectionTestUtils.setField(request, "tranAmt", "100000");
        
        WithdrawResponse response = new WithdrawResponse();
        ReflectionTestUtils.setField(response, "tranAmt", "100000");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.withdraw(any(WithdrawRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/transfer/withdraw/acnt_num")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("출금이체가 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("입금이체 성공")
    void deposit_Success() throws Exception {
        // Given
        DepositRequest request = new DepositRequest();
        ReflectionTestUtils.setField(request, "accountNum", "1234567890123456");
        ReflectionTestUtils.setField(request, "tranAmt", "100000");
        
        DepositResponse response = new DepositResponse();
        ReflectionTestUtils.setField(response, "tranAmt", "100000");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.deposit(any(DepositRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/transfer/deposit/acnt_num")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("입금이체가 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("계좌 상세 정보 조회 성공")
    void getAccountDetail_Success() throws Exception {
        // Given
        AccountDetailsRequest request = new AccountDetailsRequest();
        ReflectionTestUtils.setField(request, "accountNum", "1234567890123456");
        
        AccountDetailInfoResponse response = new AccountDetailInfoResponse();
        ReflectionTestUtils.setField(response, "accountNum", "1234567890123456");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(accountService.getAccountDetailInfo(anyString(), any(AccountDetailsRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/account/info")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("계좌 상세 정보 조회가 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("IRP 실물이전 성공")
    void irpTransfer_Success() throws Exception {
        // Given
        IrpTransferRequest request = new IrpTransferRequest();
        ReflectionTestUtils.setField(request, "userSeqNo", "U123456789");
        ReflectionTestUtils.setField(request, "wdAccountNum", "1234567890123456");
        ReflectionTestUtils.setField(request, "rsvAccountNum", "9876543210987654");
        
        IrpTransferResponse response = new IrpTransferResponse();
        ReflectionTestUtils.setField(response, "depositAmt", "1000000");
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.irpTransfer(any(IrpTransferRequest.class)))
            .willReturn(response);

        // When & Then
        mockMvc.perform(post("/v2.0/retirement/transfer")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("IRP 실물이전이 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("계좌 API 공통 실패 - 시스템 오류")
    void accountApi_SystemError() throws Exception {
        // Given
        AccountInfoRequest request = createValidAccountInfoRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.getAccountList(any(AccountInfoRequest.class)))
            .willThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        mockMvc.perform(post("/v2.0/accountinfo/num_list")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("계좌통합조회 중 시스템 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("계좌 API 공통 실패 - 잘못된 요청")
    void accountApi_InvalidRequest() throws Exception {
        // Given
        AccountInfoRequest request = createValidAccountInfoRequest();
        
        given(jwtUtil.extractTokenFromHeader(anyString()))
            .willReturn("valid_token");
        given(jwtUtil.validateToken(anyString()))
            .willReturn(true);
        given(jwtUtil.extractClientId(anyString()))
            .willReturn("F999999999");
        given(jwtUtil.extractScope(anyString()))
            .willReturn("sa");
        given(accountService.getAccountList(any(AccountInfoRequest.class)))
            .willThrow(new IllegalArgumentException("잘못된 계좌번호 형식입니다."));

        // When & Then
        mockMvc.perform(post("/v2.0/accountinfo/num_list")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 계좌번호 형식입니다."));
    }
    
    // Helper method to create AccountInfo object
    private Object createAccountInfo(String accountNum, String bankCodeStd, String bankName) {
        // Since we can't access the actual AccountInfo class structure, 
        // we'll create a simple object that can be used in tests
        return new Object() {
            public String getAccountNum() { return accountNum; }
            public String getBankCodeStd() { return bankCodeStd; }
            public String getBankName() { return bankName; }
        };
    }
} 