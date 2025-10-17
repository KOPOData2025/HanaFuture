package com.hanaTI.HanaFuture.domain.banking.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebClientConfig {
    
    private final HanaBankApiProperties apiProperties;
    
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, apiProperties.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(apiProperties.getReadTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(apiProperties.getReadTimeout(), TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(apiProperties.getConnectTimeout(), TimeUnit.MILLISECONDS))
                );
        
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("X-API-Key", apiProperties.getApiKey())
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }
    
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("HanaBank API 요청: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
    
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("HanaBank API 응답: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}