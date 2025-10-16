package com.hanafuture.openbanking.domain.auth.service;

import com.hanafuture.openbanking.domain.account.dto.request.AccountInfoRequest;
import com.hanafuture.openbanking.domain.account.dto.request.ExternalAccountRequest;
import com.hanafuture.openbanking.domain.account.dto.response.AccountInfoResponse;
import com.hanafuture.openbanking.domain.account.dto.response.ExternalAccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AccountInfoService {
    
    private final RestTemplate restTemplate;
    
    // 외부 계좌 조회 API URL 목록
    private static final List<String> EXTERNAL_API_URLS = Arrays.asList(
            "http://15.164.94.74:8080/api/v1/accounts/search-by-user-num",
            "http://3.34.183.197:8080/api/v1/accounts/search-by-user-num"
    );
    
    @Transactional
    public AccountInfoResponse getAccountList(AccountInfoRequest request) {
        
        log.info("계좌통합조회 요청 수신 - userName: {}, inquiryBankType: {}, inquiryRecordCnt: {}", 
                request.getUserName(), request.getInquiryBankType(), request.getInquiryRecordCnt());
        
        // 1. 요청 유효성 검사
        validateAccountInfoRequest(request);
        
        // 2. 외부 API들로부터 계좌 정보 조회
        List<AccountInfoResponse.AccountInfo> allAccounts = new ArrayList<>();
        
        for (String apiUrl : EXTERNAL_API_URLS) {
            try {
                List<AccountInfoResponse.AccountInfo> accounts = fetchAccountsFromExternalApi(apiUrl, request.getUserNum(), request.getUserName());
                allAccounts.addAll(accounts);
                log.info("외부 API 조회 성공 - URL: {}, 조회 건수: {}", apiUrl, accounts.size());
            } catch (Exception e) {
                log.warn("외부 API 조회 실패 - URL: {}, 오류: {}", apiUrl, e.getMessage());
                // 하나의 API가 실패해도 다른 API로 계속 진행
            }
        }
        
        // 3. 요청한 건수만큼 제한
        int requestedCount = Integer.parseInt(request.getInquiryRecordCnt());
        if (allAccounts.size() > requestedCount) {
            allAccounts = allAccounts.subList(0, requestedCount);
        }
        
        log.info("계좌통합조회 완료 - 총 조회 건수: {}", allAccounts.size());
        
        // 4. 응답 생성
        return AccountInfoResponse.builder()
                .resList(allAccounts)
                .build();
    }
    
    /**
     * 외부 API로부터 계좌 정보 조회
     */
    private List<AccountInfoResponse.AccountInfo> fetchAccountsFromExternalApi(String apiUrl, String userNum, String userName) {
        try {
            // 요청 데이터 생성
            ExternalAccountRequest externalRequest = ExternalAccountRequest.builder()
                    .userNum(userNum)
                    .build();
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // HTTP 요청 엔티티 생성
            HttpEntity<ExternalAccountRequest> requestEntity = new HttpEntity<>(externalRequest, headers);
            
            // 외부 API 호출
            ResponseEntity<ExternalAccountResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    ExternalAccountResponse.class
            );
            
            // 응답 처리
            ExternalAccountResponse externalResponse = response.getBody();
            if (externalResponse != null && externalResponse.isSuccess() && externalResponse.getData() != null) {
                return convertToAccountInfoList(externalResponse.getData().getAccounts(), userName);
            } else {
                log.warn("외부 API 응답이 성공하지 않음 - URL: {}", apiUrl);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            log.error("외부 API 호출 오류 - URL: {}, 오류: {}", apiUrl, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 외부 API 응답을 내부 응답 형식으로 변환
     */
    private List<AccountInfoResponse.AccountInfo> convertToAccountInfoList(List<ExternalAccountResponse.Account> externalAccounts, String userName) {
        List<AccountInfoResponse.AccountInfo> accountInfoList = new ArrayList<>();
        
        int listCounter = 1;
        for (ExternalAccountResponse.Account external : externalAccounts) {
            AccountInfoResponse.AccountInfo accountInfo = AccountInfoResponse.AccountInfo.builder()
                    .listNum(String.valueOf(listCounter++))
                    .bankCodeStd(external.getBankCodeStd())
                    .activityType(external.getActivityType())
                    .accountType(external.getAccountType())
                    .accountNum(external.getAccountNum())
                    .accountNumMasked(external.getAccountNumMasked())
                    .accountSeq(external.getAccountSeq())
                    .accountHolderName(userName) // 요청의 userName 사용
                    .accountIssueDate(external.getAccountIssueDate())
                    .lastTranDate(external.getLastTranDate())
                    .productName(external.getProductName())
                    .productSubName(external.getProductSubName())
                    .dormancyYn(external.getDormancyYn())
                    .balanceAmt(external.getBalanceAmt() != null ? String.valueOf(external.getBalanceAmt()) : "0")
                    .depositAmt(external.getDepositAmt() != null ? String.valueOf(external.getDepositAmt()) : "0")
                    .balanceCalcBasis1(external.getBalanceCalcBasis1())
                    .balanceCalcBasis2(external.getBalanceCalcBasis2())
                    .investmentLinkedYn(external.getInvestmentLinkedYn())
                    .bankLinkedYn(external.getBankLinkedYn())
                    .balanceAfterCancelYn(external.getBalanceAfterCancelYn())
                    .savingsBankCode(external.getSavingsBankCode() != null ? external.getSavingsBankCode() : "")
                    .build();
            
            accountInfoList.add(accountInfo);
        }
        
        return accountInfoList;
    }
    
    private void validateAccountInfoRequest(AccountInfoRequest request) {
        
        // 필수 필드 검증
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명(userName)은 필수입니다.");
        }
        
        if (request.getUserNum() == null || request.getUserNum().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 주민등록번호(userNum)는 필수입니다.");
        }
        
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이메일(userEmail)은 필수입니다.");
        }
        
        if (request.getAinfoAgreeYn() == null || request.getAinfoAgreeYn().trim().isEmpty()) {
            throw new IllegalArgumentException("어카운트인포 서비스 동의여부(ainfoAgreeYn)는 필수입니다.");
        }
        
        if (request.getInquiryBankType() == null || request.getInquiryBankType().trim().isEmpty()) {
            throw new IllegalArgumentException("금융기관 업권 구분(inquiryBankType)은 필수입니다.");
        }
        
        if (request.getTraceNo() == null || request.getTraceNo().trim().isEmpty()) {
            throw new IllegalArgumentException("추적번호(traceNo)는 필수입니다.");
        }
        
        if (request.getInquiryRecordCnt() == null || request.getInquiryRecordCnt().trim().isEmpty()) {
            throw new IllegalArgumentException("조회 건수(inquiryRecordCnt)는 필수입니다.");
        }
        
        // 형식 검증
        if (request.getUserName().length() > 20) {
            throw new IllegalArgumentException("사용자명(userName)은 20자 이하여야 합니다.");
        }
        
        // 주민등록번호 형식 검증 (999999-9999999)
        if (request.getUserNum().length() != 14) {
            throw new IllegalArgumentException("사용자 주민등록번호(userNum)는 999999-9999999 형식(14자리)이어야 합니다.");
        }
        
        // 주민등록번호 패턴 검증
        if (!isValidUserNumFormat(request.getUserNum())) {
            throw new IllegalArgumentException("사용자 주민등록번호(userNum)는 999999-9999999 형식이어야 합니다.");
        }
        
        if (request.getUserEmail().length() > 100) {
            throw new IllegalArgumentException("사용자 이메일(userEmail)은 100자 이하여야 합니다.");
        }
        
        if (!"Y".equals(request.getAinfoAgreeYn())) {
            throw new IllegalArgumentException("어카운트인포 서비스 동의여부(ainfoAgreeYn)는 'Y'여야 합니다.");
        }
        
        if (!"1".equals(request.getInquiryBankType()) && 
            !"2".equals(request.getInquiryBankType()) && 
            !"4".equals(request.getInquiryBankType())) {
            throw new IllegalArgumentException("금융기관 업권 구분(inquiryBankType)은 '1', '2', '4' 중 하나여야 합니다.");
        }
        
        if (request.getTraceNo().length() != 6) {
            throw new IllegalArgumentException("추적번호(traceNo)는 6자리여야 합니다.");
        }
        
        try {
            int recordCnt = Integer.parseInt(request.getInquiryRecordCnt());
            if (recordCnt < 1 || recordCnt > 30) {
                throw new IllegalArgumentException("조회 건수(inquiryRecordCnt)는 1~30 사이여야 합니다.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("조회 건수(inquiryRecordCnt)는 숫자여야 합니다.");
        }
        
        // 이메일 형식 검증
        if (!isValidEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }
    
    /**
     * 이메일 형식 유효성 검증
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * 주민등록번호 형식 유효성 검증 (999999-9999999)
     */
    private boolean isValidUserNumFormat(String userNum) {
        String userNumRegex = "^[0-9]{6}-[0-9]{7}$";
        return userNum.matches(userNumRegex);
    }
} 