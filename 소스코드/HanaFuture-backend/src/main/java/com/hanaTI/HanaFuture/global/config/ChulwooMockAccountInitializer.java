package com.hanaTI.HanaFuture.global.config;

import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("oracle")  // Oracle 프로파일에서만 실행
public class ChulwooMockAccountInitializer implements CommandLineRunner {

    private final HanaBankMockAccountRepository mockAccountRepository;

    @Override
    public void run(String... args) {
        log.info(" 이철우 Mock 계좌 데이터 초기화 시작...");

        String chulwooCi = "CI3003HANAFUTURE2024CHULWOO";

        // 이미 존재하는지 확인
        if (!mockAccountRepository.findByUserCiAndIsActiveTrue(chulwooCi).isEmpty()) {
            log.info("이철우 Mock 계좌가 이미 존재합니다.");
            return;
        }

        // 이철우 Mock 계좌 1: 하나 자유입출금 통장
        HanaBankMockAccount account1 = HanaBankMockAccount.builder()
                .userCi(chulwooCi)
                .userName("이철우")
                .userNum("CI3003HANAFUTURE2024CHULWOO")
                .accountNum("81701234567890")
                .productName("하나 자유입출금 통장")
                .balanceAmt(new BigDecimal("5000000"))
                .accountType("1")  // 입출금
                .isActive(true)
                .build();

        // 이철우 Mock 계좌 2: 하나 적금통장
        HanaBankMockAccount account2 = HanaBankMockAccount.builder()
                .userCi(chulwooCi)
                .userName("이철우")
                .userNum("CI3003HANAFUTURE2024CHULWOO")
                .accountNum("81709876543210")
                .productName("하나 적금통장")
                .balanceAmt(new BigDecimal("3000000"))
                .accountType("3")  // 적금
                .isActive(true)
                .build();

        // 이철우 Mock 계좌 3: 하나 예금통장
        HanaBankMockAccount account3 = HanaBankMockAccount.builder()
                .userCi(chulwooCi)
                .userName("이철우")
                .userNum("CI3003HANAFUTURE2024CHULWOO")
                .accountNum("81705556667778")
                .productName("하나 예금통장")
                .balanceAmt(new BigDecimal("10000000"))
                .accountType("2")  // 예금
                .isActive(true)
                .build();

        mockAccountRepository.save(account1);
        mockAccountRepository.save(account2);
        mockAccountRepository.save(account3);

        log.info("이철우 Mock 계좌 3개 생성 완료!");
    }
}

