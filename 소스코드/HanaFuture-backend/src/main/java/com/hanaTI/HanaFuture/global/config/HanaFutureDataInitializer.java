package com.hanaTI.HanaFuture.global.config;

import com.hanaTI.HanaFuture.domain.ai.service.WelfareDataMigrationService;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 하나퓨처 서비스 초기화
 * - AI 필터링된 복지 데이터 자동 이관
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HanaFutureDataInitializer implements ApplicationRunner {

    private final WelfareDataMigrationService migrationService;
    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(" 하나퓨처 데이터 초기화 시작...");

        try {
            // 기존 하나퓨처 데이터 확인
            long existingCount = hanaFutureRepository.count();
            
            if (existingCount > 0) {
                log.info("하나퓨처 복지 데이터가 이미 존재합니다: {}개", existingCount);
                migrationService.printHanaFutureWelfareStats();
                return;
            }

            log.info(" 하나퓨처 복지 데이터가 없습니다. AI 필터링 데이터 이관을 시작합니다...");
            
            // AI 필터링 데이터 이관 실행
            migrationService.migrateAIFilteredData();
            
            log.info(" 하나퓨처 데이터 초기화 완료!");

        } catch (Exception e) {
            log.error("하나퓨처 데이터 초기화 실패: {}", e.getMessage(), e);
            // 초기화 실패해도 서버는 계속 실행
        }
    }
}



