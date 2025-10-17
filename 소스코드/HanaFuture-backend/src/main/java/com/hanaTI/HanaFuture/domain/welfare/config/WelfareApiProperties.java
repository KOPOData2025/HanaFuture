package com.hanaTI.HanaFuture.domain.welfare.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "welfare.api")
public class WelfareApiProperties {
    
    /**
     * API 활성화 여부 (임시 비활성화 가능)
     */
    private Boolean enabled = true;
    
    /**
     * 공공데이터포털 서비스 키
     */
    private String serviceKey;
    
    /**
     * API 기본 URL
     */
    private String baseUrl = "https://apis.data.go.kr";
    
    /**
     * 중앙부처 복지서비스 API 경로
     */
    private String centralServicePath = "/B554287/NationalWelfareInformationsV001";
    
    /**
     * 지자체 복지서비스 API 경로
     */
    private String localServicePath = "/B554287/LocalGovernmentWelfareInformations";
    
    /**
     * 연결 타임아웃 (밀리초)
     */
    private Integer connectTimeout = 10000;
    
    /**
     * 읽기 타임아웃 (밀리초)
     */
    private Integer readTimeout = 30000;
    
    /**
     * 최대 재시도 횟수
     */
    private Integer maxRetryCount = 3;
    
    /**
     * 배치 작업 시 한 번에 가져올 데이터 수
     */
    private Integer batchSize = 100;
}
