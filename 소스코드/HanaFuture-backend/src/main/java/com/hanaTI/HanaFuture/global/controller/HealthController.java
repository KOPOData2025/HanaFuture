package com.hanaTI.HanaFuture.global.controller;

import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Health Check", description = "시스템 상태 확인 API")
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final DataSource dataSource;

    @Operation(
            summary = "전체 시스템 헬스체크",
            description = "애플리케이션과 데이터베이스 연결 상태를 확인합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "시스템 정상"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "시스템 장애"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();
        boolean isHealthy = true;

        // 애플리케이션 상태
        healthStatus.put("application", Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now()
        ));

        // 데이터베이스 상태 확인
        Map<String, Object> dbStatus = checkDatabaseHealth();
        healthStatus.put("database", dbStatus);
        
        if (!"UP".equals(dbStatus.get("status"))) {
            isHealthy = false;
        }

        healthStatus.put("overall", isHealthy ? "UP" : "DOWN");

        if (isHealthy) {
            return ResponseEntity.ok(
                    ApiResponse.success("시스템이 정상 작동 중입니다.", healthStatus)
            );
        } else {
            return ResponseEntity.status(503).body(
                    ApiResponse.error("시스템에 문제가 발생했습니다.", healthStatus)
            );
        }
    }

    @Operation(
            summary = "데이터베이스 헬스체크",
            description = "데이터베이스 연결 상태만 확인합니다."
    )
    @GetMapping("/database")
    public ResponseEntity<ApiResponse<Map<String, Object>>> databaseHealthCheck() {
        Map<String, Object> dbStatus = checkDatabaseHealth();
        
        if ("UP".equals(dbStatus.get("status"))) {
            return ResponseEntity.ok(
                    ApiResponse.success("데이터베이스 연결이 정상입니다.", dbStatus)
            );
        } else {
            return ResponseEntity.status(503).body(
                    ApiResponse.error("데이터베이스 연결에 문제가 있습니다.", dbStatus)
            );
        }
    }

    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbStatus = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 연결 테스트
            boolean isValid = connection.isValid(5); // 5초 타임아웃
            
            if (isValid) {
                // 데이터베이스 정보 수집
                String url = connection.getMetaData().getURL();
                String driverName = connection.getMetaData().getDriverName();
                String driverVersion = connection.getMetaData().getDriverVersion();
                String productName = connection.getMetaData().getDatabaseProductName();
                String productVersion = connection.getMetaData().getDatabaseProductVersion();
                
                dbStatus.put("status", "UP");
                dbStatus.put("url", maskPassword(url));
                dbStatus.put("driver", driverName + " " + driverVersion);
                dbStatus.put("database", productName + " " + productVersion);
                dbStatus.put("timestamp", LocalDateTime.now());
                
                log.info("Database health check passed: {}", productName);
            } else {
                dbStatus.put("status", "DOWN");
                dbStatus.put("error", "Connection validation failed");
                dbStatus.put("timestamp", LocalDateTime.now());
                
                log.warn("Database health check failed: Connection validation failed");
            }
            
        } catch (SQLException e) {
            dbStatus.put("status", "DOWN");
            dbStatus.put("error", e.getMessage());
            dbStatus.put("timestamp", LocalDateTime.now());
            
            log.error("Database health check failed", e);
        }
        
        return dbStatus;
    }

    private String maskPassword(String url) {
        if (url == null) return null;
        
        // URL에서 비밀번호 마스킹 (password=xxx 부분을 password=*** 로 변경)
        return url.replaceAll("password=[^&;]*", "password=***");
    }
}
