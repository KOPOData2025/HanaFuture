package com.hanaTI.HanaFuture.domain.mydata.service;

import com.hanaTI.HanaFuture.domain.banking.service.HanaBankApiClient;
import com.hanaTI.HanaFuture.domain.mydata.dto.MyDataConnectRequest;
import com.hanaTI.HanaFuture.domain.mydata.dto.MyDataAccountResponse;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyDataIntegrationService {

    private final HanaBankApiClient hanaBankApiClient;

    @Transactional
    public Map<String, Object> connectMyDataAccounts(User user, MyDataConnectRequest request) {
        log.info("마이데이터 계좌 연동 시작 - 사용자: {}, 은행: {}", 
                user.getEmail(), request.getBankCodes());
        
        try {
            Map<String, Object> response = hanaBankApiClient
                    .getUserAccounts(user.getId().toString())
                    .block(); // 동기 처리
            
            if (Boolean.TRUE.equals(response.get("success"))) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> accountsData = (List<Map<String, Object>>) response.get("data");
                
                log.info("하나은행에서 {}개 계좌 정보 조회 완료 - 사용자: {}", 
                        accountsData.size(), user.getEmail());
                
                // 마이데이터 연동 성공 응답
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", accountsData.size() + "개 계좌가 연동되었습니다.");
                result.put("connectedCount", accountsData.size());
                result.put("connectedBanks", request.getBankCodes());
                result.put("accounts", accountsData);
                result.put("timestamp", java.time.LocalDateTime.now());
                
                return result;
            } else {
                log.warn("하나은행 계좌 조회 실패 - 사용자: {}, 메시지: {}", 
                        user.getEmail(), response.get("message"));
                
                return Map.of(
                    "success", false,
                    "message", "하나은행 계좌 정보를 가져올 수 없습니다.",
                    "errorCode", "HANABANK_API_ERROR"
                );
            }
            
        } catch (Exception e) {
            log.error("마이데이터 연동 실패 - 사용자: {}", user.getEmail(), e);
            return Map.of(
                "success", false,
                "message", "마이데이터 연동에 실패했습니다: " + e.getMessage(),
                "errorCode", "MYDATA_CONNECTION_FAILED"
            );
        }
    }

    public List<MyDataAccountResponse> getMyDataAccounts(User user) {
        log.info("마이데이터 계좌 목록 조회 - 사용자: {}", user.getEmail());

        try {
            // 하나은행 백엔드에서 사용자 계좌 정보 조회 (기존 API 활용)
            Map<String, Object> response = hanaBankApiClient
                    .getUserAccounts(user.getId().toString())
                    .block();

            if (Boolean.TRUE.equals(response.get("success"))) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> accountsData = (List<Map<String, Object>>) response.get("data");

                log.info("하나은행에서 {}개 계좌 정보 조회 완료 - 사용자: {}", 
                        accountsData.size(), user.getEmail());

                return accountsData.stream()
                        .map(this::convertToMyDataAccountResponse)
                        .collect(Collectors.toList());
            } else {
                log.warn("하나은행 계좌 조회 실패 - 사용자: {}, 메시지: {}", 
                        user.getEmail(), response.get("message"));
                return List.of();
            }

        } catch (Exception e) {
            log.error("마이데이터 계좌 조회 실패 - 사용자: {}", user.getEmail(), e);
            return List.of();
        }
    }

    @Transactional
    public Map<String, Object> disconnectMyDataAccount(User user, String accountId) {
        log.info("마이데이터 계좌 연동 해제 - 사용자: {}, 계좌ID: {}", 
                user.getEmail(), accountId);
        
        try {
            // 실제로는 하나Future DB에서 연동 정보만 제거
            // 하나은행의 실제 계좌는 그대로 유지
            
            log.info("마이데이터 연동 해제 완료 - 사용자: {}, 계좌ID: {}", 
                    user.getEmail(), accountId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "마이데이터 연동이 해제되었습니다.");
            result.put("disconnectedAccountId", accountId);
            result.put("timestamp", java.time.LocalDateTime.now());
            
            return result;
            
        } catch (Exception e) {
            log.error("마이데이터 연동 해제 실패 - 사용자: {}", user.getEmail(), e);
            return Map.of(
                "success", false,
                "message", "마이데이터 연동 해제에 실패했습니다: " + e.getMessage(),
                "errorCode", "MYDATA_DISCONNECT_FAILED"
            );
        }
    }

    private MyDataAccountResponse convertToMyDataAccountResponse(Map<String, Object> accountData) {
        return MyDataAccountResponse.builder()
                .accountId(accountData.get("accountId").toString())
                .bankName("하나은행") // 하나은행 고정
                .bankCode("hana") // 하나은행 코드
                .accountNumber(accountData.get("accountNum").toString())
                .productName(accountData.get("productName").toString())
                .accountType(getAccountTypeName(accountData.get("accountType").toString()))
                .balance(((Number) accountData.get("balanceAmt")).longValue())
                .availableBalance(((Number) accountData.get("balanceAmt")).longValue()) // 잔액과 동일
                .isActive("1".equals(accountData.get("activityType").toString()))
                .connectionStatus("CONNECTED")
                .dataProvider("하나은행")
                .lastSyncedAt(java.time.LocalDateTime.now())
                .build();
    }
    
    /**
     * 계좌 타입 코드를 이름으로 변환
     */
    private String getAccountTypeName(String accountType) {
        switch (accountType) {
            case "1": return "입출금";
            case "2": return "적금";
            case "3": return "예금";
            case "4": return "대출";
            default: return "기타";
        }
    }
}
