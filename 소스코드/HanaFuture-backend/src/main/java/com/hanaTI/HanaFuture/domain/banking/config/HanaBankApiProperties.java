package com.hanaTI.HanaFuture.domain.banking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hanabank.api")
public class HanaBankApiProperties {
    
    private String baseUrl = "http://localhost:8081/api";
    private String version = "v1";
    private int connectTimeout = 10000;
    private int readTimeout = 30000;
    private int maxRetryCount = 3;
    private String apiKey = "hana_api_key_2024";
}