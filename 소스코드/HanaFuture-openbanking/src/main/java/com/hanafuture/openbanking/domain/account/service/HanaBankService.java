package com.hanafuture.openbanking.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HanaBankService {
    
    private final RestTemplate restTemplate;
    
    @Value("${hanabank.server.url:http://localhost:8081}")
    private String hanaBankServerUrl;
    
    /**
     * 하나은행 서버에서 사용자 계좌 조회
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserAccountsFromHanaBank(String userId) {
        try {
            String url = hanaBankServerUrl + "/api/accounts/user/" + userId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("data")) {
                    return (List<Map<String, Object>>) responseBody.get("data");
                }
            }
            
            throw new RuntimeException("하나은행 계좌 조회 실패: " + response.getStatusCode());
            
        } catch (Exception e) {
            log.error("하나은행 계좌 조회 중 오류 발생", e);
            throw new RuntimeException("하나은행 계좌 조회 실패", e);
        }
    }
    
    /**
     * 하나은행 서버에서 계좌 잔액 조회
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAccountBalanceFromHanaBank(String accountNum) {
        try {
            String url = hanaBankServerUrl + "/api/accounts/" + accountNum + "/balance";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
            throw new RuntimeException("하나은행 잔액 조회 실패: " + response.getStatusCode());
            
        } catch (Exception e) {
            log.error("하나은행 잔액 조회 중 오류 발생", e);
            throw new RuntimeException("하나은행 잔액 조회 실패", e);
        }
    }
    
    /**
     * 하나은행 서버에서 계좌 상세 정보 조회
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAccountDetailFromHanaBank(String accountNum) {
        try {
            String url = hanaBankServerUrl + "/api/accounts/" + accountNum;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
            throw new RuntimeException("하나은행 계좌 상세 조회 실패: " + response.getStatusCode());
            
        } catch (Exception e) {
            log.error("하나은행 계좌 상세 조회 중 오류 발생", e);
            throw new RuntimeException("하나은행 계좌 상세 조회 실패", e);
        }
    }
}

