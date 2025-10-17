package com.hanaTI.HanaFuture.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * - AI 추천 백그라운드 처리용
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "aiRecommendationExecutor")
    public Executor aiRecommendationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // 기본 스레드 수
        executor.setMaxPoolSize(5);         // 최대 스레드 수
        executor.setQueueCapacity(100);     // 대기 큐 크기
        executor.setThreadNamePrefix("AI-Recommendation-");
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn(" AI 추천 작업 거부됨: 큐가 가득참");
        });
        executor.initialize();
        return executor;
    }
}



