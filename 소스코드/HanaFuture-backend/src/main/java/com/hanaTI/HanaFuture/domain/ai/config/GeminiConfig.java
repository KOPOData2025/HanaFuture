package com.hanaTI.HanaFuture.domain.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Configuration
public class GeminiConfig {
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent}")
    private String geminiApiUrl;
    
    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
                // baseUrl 제거 - 각 서비스에서 전체 URL 지정
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }
    
    public String getGeminiApiKey() {
        return geminiApiKey;
    }
    
    public String getGeminiApiUrl() {
        return geminiApiUrl;
    }
}
