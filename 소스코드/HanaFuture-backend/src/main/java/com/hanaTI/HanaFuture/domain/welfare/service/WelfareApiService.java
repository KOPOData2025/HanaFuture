package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.config.WelfareApiProperties;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.WelfareApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
public class WelfareApiService {
    
    private final WelfareApiProperties apiProperties;
    private final WebClient webClient;
    
    public WelfareApiService(WelfareApiProperties apiProperties) {
        this.apiProperties = apiProperties;
        this.webClient = WebClient.builder()
                .baseUrl(apiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                .build();
    }
    
    /**
     * 중앙부처 복지서비스 조회
     */
    public Mono<String> getCentralWelfareServices(int pageNo, int numOfRows, 
                                                 String lifeArray, String intrsThemaArray, 
                                                 String searchWrd) {
        // API가 비활성화된 경우 빈 결과 반환
        if (!apiProperties.getEnabled()) {
            log.warn("복지 API가 비활성화되어 있습니다. (정부 전산망 오류로 인한 임시 조치)");
            return Mono.just("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><header><resultCode>00</resultCode><resultMsg>정상처리</resultMsg></header><body><items></items><numOfRows>0</numOfRows><pageNo>1</pageNo><totalCount>0</totalCount></body></response>");
        }
        
        log.info("중앙부처 복지서비스 조회 - 페이지: {}, 크기: {}, 생애주기: {}, 관심주제: {}, 키워드: {}", 
                pageNo, numOfRows, lifeArray, intrsThemaArray, searchWrd);
        
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path(apiProperties.getCentralServicePath() + "/NationalWelfarelistV001")
                            .queryParam("serviceKey", apiProperties.getServiceKey())
                            .queryParam("callTp", "L") // 목록 조회
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("srchKeyCode", "001"); // 기본값: 제목 검색
                    
                    if (lifeArray != null && !lifeArray.trim().isEmpty()) {
                        builder.queryParam("lifeArray", lifeArray);
                    }
                    if (intrsThemaArray != null && !intrsThemaArray.trim().isEmpty()) {
                        builder.queryParam("intrsThemaArray", intrsThemaArray);
                    }
                    if (searchWrd != null && !searchWrd.trim().isEmpty()) {
                        builder.queryParam("searchWrd", searchWrd);
                    }
                    
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("중앙부처 복지서비스 조회 실패 - 상태코드: {}, 응답: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(Exception.class, ex -> {
                    log.error("중앙부처 복지서비스 조회 중 예외 발생", ex);
                });
    }
    
    /**
     * 지자체 복지서비스 조회 (목록)
     */
    public Mono<String> getLocalWelfareServices(String ctpvCd, String sggCd,
                                              int pageNo, int numOfRows,
                                              String lifeArray, String searchWrd) {
        // API가 비활성화된 경우 빈 결과 반환
        if (!apiProperties.getEnabled()) {
            log.warn("복지 API가 비활성화되어 있습니다. (정부 전산망 오류로 인한 임시 조치)");
            return Mono.just("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><header><resultCode>00</resultCode><resultMsg>정상처리</resultMsg></header><body><items></items><numOfRows>0</numOfRows><pageNo>1</pageNo><totalCount>0</totalCount></body></response>");
        }
        
        log.info("지자체 복지서비스 조회 - 시도코드: {}, 시군구코드: {}, 페이지: {}, 크기: {}, 생애주기: {}, 키워드: {}", 
                ctpvCd, sggCd, pageNo, numOfRows, lifeArray, searchWrd);
        
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path(apiProperties.getLocalServicePath() + "/LcgvWelfarelist")
                            .queryParam("serviceKey", apiProperties.getServiceKey())
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("srchKeyCode", "001"); // 기본값: 제목 검색
                    
                    if (ctpvCd != null && !ctpvCd.trim().isEmpty()) {
                        builder.queryParam("ctpvNm", ctpvCd); // 시도명으로 변경
                    }
                    if (sggCd != null && !sggCd.trim().isEmpty()) {
                        builder.queryParam("sggNm", sggCd); // 시군구명으로 변경
                    }
                    if (lifeArray != null && !lifeArray.trim().isEmpty()) {
                        builder.queryParam("lifeArray", lifeArray);
                    }
                    if (searchWrd != null && !searchWrd.trim().isEmpty()) {
                        builder.queryParam("searchWrd", searchWrd);
                    }
                    
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(apiProperties.getMaxRetryCount(), Duration.ofSeconds(1)))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("지자체 복지서비스 조회 실패 - 상태코드: {}, 응답: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                })
                .doOnError(Exception.class, ex -> {
                    log.error("지자체 복지서비스 조회 중 예외 발생", ex);
                });
    }
    
    /**
     * API 연결 상태 확인
     */
    public Mono<Boolean> checkApiHealth() {
        return getCentralWelfareServices(1, 1, null, null, null)
                .map(response -> response != null && !response.trim().isEmpty())
                .onErrorReturn(false)
                .doOnNext(isHealthy -> {
                    if (isHealthy) {
                        log.info("복지로 API 연결 상태 정상");
                    } else {
                        log.warn("복지로 API 연결 상태 이상");
                    }
                });
    }
}
