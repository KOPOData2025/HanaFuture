package com.hanaTI.HanaFuture.domain.banking.service;

import com.hanaTI.HanaFuture.domain.banking.config.HanaBankApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HanaBankApiClient {
    
    private final WebClient webClient;
    private final HanaBankApiProperties apiProperties;
    
    /**
     * 사용자 계좌 목록 조회
     */
    public Mono<Map<String, Object>> getUserAccounts(String userId) {
        String url = "/mock/hanabank/api/v1/accounts/user/" + userId;
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank API 호출 실패 - URL: {}, 상태코드: {}, 응답: {}", 
                            url, ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank API 호출 중 예외 발생 - URL: {}", url, ex);
                })
                .onErrorReturn(createErrorResponse("계좌 조회 실패", "ACCOUNT_FETCH_ERROR"))
                .map(response -> (Map<String, Object>) response);
    }
    
    /**
     * 적금 상품 추천 조회
     */
    public Mono<Map<String, Object>> getSavingsRecommendations(String targetCustomer) {
        String url = "/api/savings/recommendations";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("targetCustomer", targetCustomer);
        
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank 적금상품 추천 API 호출 실패 - URL: {}, 상태코드: {}, 응답: {}", 
                            url, ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank 적금상품 추천 API 호출 중 예외 발생 - URL: {}", url, ex);
                })
                .onErrorReturn(createErrorResponse("적금상품 추천 조회 실패", "SAVINGS_RECOMMENDATION_ERROR"))
                .map(response -> (Map<String, Object>) response);
    }
    
    /**
     * 육아 전용 적금 상품 추천 조회
     */
    public Mono<Map<String, Object>> getChildcareSavingsRecommendations() {
        String url = "/api/savings/childcare-recommendations";
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank 육아적금 추천 API 호출 실패 - URL: {}, 상태코드: {}, 응답: {}", 
                            url, ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank 육아적금 추천 API 호출 중 예외 발생 - URL: {}", url, ex);
                })
                .onErrorReturn(createErrorResponse("육아적금 추천 조회 실패", "CHILDCARE_SAVINGS_ERROR"))
                .map(response -> (Map<String, Object>) response);
    }

    /**
     * 육아 적금 상품 생성
     */
    public Mono<Map<String, Object>> createChildcareSavings(String userId, String productType, 
                                                           Double monthlyAmount, Integer periodMonths) {
        String url = "/api/accounts/childcare-savings";
        
        Map<String, Object> request = new HashMap<>();
        request.put("userId", userId);
        request.put("productType", productType);
        request.put("monthlyAmount", monthlyAmount);
        request.put("periodMonths", periodMonths);
        
        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank API 호출 실패 - 상태코드: {}, 응답: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank API 호출 중 예외 발생", ex);
                })
                .onErrorReturn(createErrorResponse("육아 적금 생성 실패"))
                .map(response -> (Map<String, Object>) response);
    }
    
    /**
     * 육아 상품 추천 조회
     */
    public Mono<Map<String, Object>> getChildcareProductRecommendations(String userId) {
        String url = "/api/accounts/childcare-recommendations/" + userId;
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank API 호출 실패 - 상태코드: {}, 응답: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank API 호출 중 예외 발생", ex);
                })
                .onErrorReturn(createErrorResponse("상품 추천 조회 실패"))
                .map(response -> (Map<String, Object>) response);
    }
    
    /**
     * 거래내역 조회
     */
    public Mono<Map<String, Object>> getTransactionHistory(String accountNum, String startDate, String endDate) {
        String url = "/api/accounts/" + accountNum + "/transactions";
        
        Map<String, Object> request = new HashMap<>();
        request.put("startDate", startDate);
        request.put("endDate", endDate);
        request.put("pageSize", 100);
        
        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank API 호출 실패 - 상태코드: {}, 응답: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank API 호출 중 예외 발생", ex);
                })
                .onErrorReturn(createErrorResponse("거래내역 조회 실패"))
                .map(response -> (Map<String, Object>) response);
    }
    
    /**
     * 계좌 잔액 조회
     */
    public Mono<Map<String, Object>> getAccountBalance(String accountNum) {
        String url = "/mock/hanabank/api/v1/accounts/" + accountNum + "/balance";
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .cast(Map.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("HanaBank API 호출 실패 - 상태코드: {}, 응답: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(ex -> {
                    log.error("HanaBank API 호출 중 예외 발생", ex);
                })
                .onErrorReturn(createErrorResponse("잔액 조회 실패"))
                .map(response -> (Map<String, Object>) response);
    }
    
    /**
     * 오류 응답 생성
     */
    private Map<String, Object> createErrorResponse(String message) {
        return createErrorResponse(message, "UNKNOWN_ERROR");
    }
    
    /**
     * 상세 오류 응답 생성
     */
    private Map<String, Object> createErrorResponse(String message, String errorCode) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        errorResponse.put("data", null);
        return errorResponse;
    }
    
    /**
     * Mock 데이터 생성 (HanaBank 서버가 없을 때 사용)
     */
    public Map<String, Object> createMockAccountData(String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "계좌 조회 성공 (Mock 데이터)");
        
        List<Map<String, Object>> accounts = List.of(
            Map.of(
                "accountNum", "81700123456789",
                "productName", "하나 입출금통장",
                "balanceAmt", 1500000,
                "accountType", "1"
            ),
            Map.of(
                "accountNum", "81700234567890", 
                "productName", "하나 육아적금",
                "balanceAmt", 500000,
                "accountType", "2"
            )
        );
        
        response.put("data", accounts);
        return response;
    }
}