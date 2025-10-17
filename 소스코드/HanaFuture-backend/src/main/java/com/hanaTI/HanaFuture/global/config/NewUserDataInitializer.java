package com.hanaTI.HanaFuture.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@Profile("local")
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class NewUserDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(" 새로운 사용자 데이터 초기화 시작...");
        
        try {
            // 1. 새로운 사용자 생성
            createNewUser();
            
            // 2. 하나은행 Mock 계좌 데이터 생성
            createHanaBankMockAccounts();
            
            // 3. 거래내역 데이터 생성
            createTransactionHistory();
            
            log.info("새로운 사용자 데이터 초기화 완료!");
            
        } catch (Exception e) {
            log.error("새로운 사용자 데이터 초기화 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 새로운 테스트 사용자 생성
     */
    private void createNewUser() {
        try {
            // 사용자 존재 여부 확인
            Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", 
                Integer.class, 
                "leehana@naver.com"
            );
            
            if (userCount != null && userCount > 0) {
                log.info(" 사용자 leehana@naver.com 이미 존재함");
                return;
            }
            
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode("password1234");
            
            // 새로운 사용자 생성
            jdbcTemplate.update("""
                INSERT INTO users (
                    email, password, name, phone_number, birth_date, gender,
                    residence_sido, residence_sigungu, marital_status, 
                    has_children, number_of_children, income, employment_status,
                    is_active, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
                """,
                "leehana@naver.com",
                encodedPassword,
                "이하나",
                "010-9876-5432",
                "1985-08-20",
                "FEMALE",
                "서울특별시",
                "서초구",
                "SINGLE",
                false,
                0,
                4500000,
                "EMPLOYED",
                true
            );
            
            log.info(" 새로운 사용자 생성 완료: leehana@naver.com");
            
        } catch (Exception e) {
            log.error("사용자 생성 실패", e);
        }
    }

    /**
     * 하나은행 Mock 계좌 데이터 생성
     */
    private void createHanaBankMockAccounts() {
        try {
            // 하나은행 Mock 계좌 테이블 생성 (존재하지 않을 경우)
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS hanabank_mock_accounts (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_ci VARCHAR(100) NOT NULL COMMENT '사용자 CI (연계정보)',
                    user_name VARCHAR(50) NOT NULL COMMENT '사용자 이름',
                    user_num VARCHAR(20) NOT NULL COMMENT '주민등록번호',
                    account_num VARCHAR(20) NOT NULL UNIQUE COMMENT '계좌번호',
                    product_name VARCHAR(100) NOT NULL COMMENT '상품명',
                    balance_amt DECIMAL(15,2) DEFAULT 0.00 COMMENT '잔액',
                    account_type VARCHAR(2) NOT NULL COMMENT '계좌유형 (1:입출금, 2:적금, 3:예금, 4:투자)',
                    bank_code VARCHAR(10) DEFAULT '081' COMMENT '은행코드',
                    bank_name VARCHAR(50) DEFAULT '하나은행' COMMENT '은행명',
                    fintech_use_num VARCHAR(50) COMMENT '핀테크이용번호',
                    is_active BOOLEAN DEFAULT TRUE COMMENT '활성상태',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_user_ci (user_ci),
                    INDEX idx_account_num (account_num)
                ) COMMENT='하나은행 Mock 계좌 정보'
                """);

            // 계좌 존재 여부 확인
            Integer accountCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM hanabank_mock_accounts WHERE user_ci = ?", 
                Integer.class, 
                "CI2002HANAFUTURE2024TEST"
            );
            
            if (accountCount != null && accountCount > 0) {
                log.info(" 하나은행 Mock 계좌 이미 존재함");
                return;
            }

            // 새로운 사용자의 하나은행 계좌들 생성
            String[][] accounts = {
                {"81700888999000", "하나 1등통장", "3200000.00", "1", "M202401234567890123456789012349"},
                {"81700777888999", "하나 적금통장", "1800000.00", "2", "M202401234567890123456789012350"},
                {"81700666777888", "하나 청년적금", "950000.00", "2", "M202401234567890123456789012351"}
            };

            for (String[] account : accounts) {
                jdbcTemplate.update("""
                    INSERT INTO hanabank_mock_accounts (
                        user_ci, user_name, user_num, account_num, product_name, 
                        balance_amt, account_type, fintech_use_num
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    "CI2002HANAFUTURE2024TEST",
                    "이하나",
                    "8508201234567",
                    account[0], // account_num
                    account[1], // product_name
                    Double.parseDouble(account[2]), // balance_amt
                    account[3], // account_type
                    account[4]  // fintech_use_num
                );
            }
            
            log.info(" 하나은행 Mock 계좌 {} 개 생성 완료", accounts.length);
            
        } catch (Exception e) {
            log.error("하나은행 Mock 계좌 생성 실패", e);
        }
    }

    /**
     * 거래내역 데이터 생성
     */
    private void createTransactionHistory() {
        try {
            // 거래내역 테이블 생성 (존재하지 않을 경우)
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS hanabank_mock_transactions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    account_num VARCHAR(20) NOT NULL COMMENT '계좌번호',
                    tran_date DATE NOT NULL COMMENT '거래일자',
                    tran_time TIME NOT NULL COMMENT '거래시간',
                    tran_type VARCHAR(10) NOT NULL COMMENT '거래구분 (입금/출금)',
                    tran_amt DECIMAL(15,2) NOT NULL COMMENT '거래금액',
                    after_balance_amt DECIMAL(15,2) NOT NULL COMMENT '거래후잔액',
                    print_content VARCHAR(200) COMMENT '거래내용',
                    branch_name VARCHAR(100) COMMENT '취급점명',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_account_num (account_num),
                    INDEX idx_tran_date (tran_date)
                ) COMMENT='하나은행 Mock 거래내역'
                """);

            // 거래내역 존재 여부 확인
            Integer transactionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM hanabank_mock_transactions WHERE account_num = ?", 
                Integer.class, 
                "81700888999000"
            );
            
            if (transactionCount != null && transactionCount > 0) {
                log.info(" 거래내역 이미 존재함");
                return;
            }

            // 주거래 통장 거래내역
            jdbcTemplate.update("""
                INSERT INTO hanabank_mock_transactions (
                    account_num, tran_date, tran_time, tran_type, tran_amt, 
                    after_balance_amt, print_content, branch_name
                ) VALUES 
                ('81700888999000', CURDATE(), '14:30:00', '입금', 400000.00, 3200000.00, '급여입금', '하나은행 서초지점'),
                ('81700888999000', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '09:15:00', '출금', 120000.00, 2800000.00, '생활비출금', '하나은행 서초지점'),
                ('81700888999000', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '16:45:00', '입금', 50000.00, 2920000.00, '용돈입금', '하나은행 서초지점')
                """);
            
            log.info(" 거래내역 생성 완료");
            
        } catch (Exception e) {
            log.error("거래내역 생성 실패", e);
        }
    }
}



