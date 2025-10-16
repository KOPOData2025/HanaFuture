package com.hana.hanabank.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HanaBank Backend API")
                        .description("하나은행 백엔드 서비스 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("HanaBank Team")
                                .email("support@hanabank.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development Server"),
                        new Server().url("https://api.hanabank.com").description("Production Server")
                ));
    }
}
