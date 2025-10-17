package com.hanaTI.HanaFuture.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "coolsms")
@Getter
@Setter
public class CoolSmsProperties {
    
    private String apiKey;
    private String apiSecret;
    private String fromNumber;
    
    private Verification verification = new Verification();
    
    @Getter
    @Setter
    public static class Verification {
        private int expiryMinutes = 5;
        private int codeLength = 6;
        private boolean testMode = true;  // 테스트 모드
        private String testCode = "123456";  // 테스트용 고정 인증번호
    }
}
