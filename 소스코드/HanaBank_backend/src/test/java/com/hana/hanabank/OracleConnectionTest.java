package com.hana.hanabank;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Oracle RDS 연결 테스트
 */
public class OracleConnectionTest {
    
    private static final Logger log = LoggerFactory.getLogger(OracleConnectionTest.class);
    
    @Test
    public void testOracleConnection() {
        String url = "jdbc:oracle:thin:@hanabank-db.czuy6awycddc.ap-northeast-2.rds.amazonaws.com:1521:HANADB";
        String username = "admin";
        String password = "cjfdn7328!";
        
        log.info("Oracle RDS 연결 테스트 시작...");
        log.info("URL: {}", url);
        log.info("Username: {}", username);
        
        try {
            // Oracle JDBC 드라이버 로드
            Class.forName("oracle.jdbc.OracleDriver");
            log.info("Oracle JDBC 드라이버 로드 성공");
            
            // 데이터베이스 연결
            long startTime = System.currentTimeMillis();
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                long connectionTime = System.currentTimeMillis() - startTime;
                log.info("Oracle RDS 연결 성공! ({}ms)", connectionTime);
                
                // 연결 정보 확인
                log.info("연결 정보:");
                log.info("  - Auto Commit: {}", connection.getAutoCommit());
                log.info("  - Catalog: {}", connection.getCatalog());
                log.info("  - Schema: {}", connection.getSchema());
                
                // 간단한 쿼리 테스트
                try (Statement stmt = connection.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT SYSDATE FROM DUAL");
                    if (rs.next()) {
                        String currentTime = rs.getString(1);
                        log.info("쿼리 테스트 성공 - 현재 시간: {}", currentTime);
                    }
                }
                
                // 테이블 존재 확인
                try (Statement stmt = connection.createStatement()) {
                    ResultSet rs = stmt.executeQuery(
                        "SELECT COUNT(*) FROM user_tables WHERE table_name = 'HANABANK_USER'"
                    );
                    if (rs.next()) {
                        int tableCount = rs.getInt(1);
                        if (tableCount > 0) {
                            log.info("HANABANK_USER 테이블 존재함");
                        } else {
                            log.warn("HANABANK_USER 테이블이 존재하지 않음 - 스키마 생성 필요");
                        }
                    }
                } catch (Exception e) {
                    log.warn("테이블 확인 실패 (스키마 미생성 가능성): {}", e.getMessage());
                }
                
                log.info("Oracle RDS 연결 테스트 완료!");
                
            }
        } catch (ClassNotFoundException e) {
            log.error("Oracle JDBC 드라이버를 찾을 수 없습니다: {}", e.getMessage());
            throw new RuntimeException("Oracle JDBC 드라이버 오류", e);
        } catch (Exception e) {
            log.error("Oracle RDS 연결 실패: {}", e.getMessage());
            log.error("상세 오류: ", e);
            throw new RuntimeException("Oracle 연결 실패", e);
        }
    }
}
