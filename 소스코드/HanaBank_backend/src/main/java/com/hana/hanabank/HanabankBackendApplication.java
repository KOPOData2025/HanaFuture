package com.hana.hanabank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HanabankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanabankBackendApplication.class, args);
    }
}
