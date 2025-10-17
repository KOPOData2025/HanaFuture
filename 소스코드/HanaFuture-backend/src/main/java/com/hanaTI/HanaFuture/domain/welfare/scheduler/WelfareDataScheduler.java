package com.hanaTI.HanaFuture.domain.welfare.scheduler;

import com.hanaTI.HanaFuture.domain.welfare.service.WelfareBenefitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 복지 데이터 주기적 동기화 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "welfare.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class WelfareDataScheduler {
    
    private final WelfareBenefitService welfareBenefitService;
    
    /**
     * 매일 새벽 2시에 중앙정부 복지서비스 동기화
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void syncCentralWelfareData() {
        log.info("=== 중앙정부 복지서비스 정기 동기화 시작 ===");
        
        try {
            welfareBenefitService.syncCentralWelfareServices();
            log.info("중앙정부 복지서비스 정기 동기화 완료");
        } catch (Exception e) {
            log.error("중앙정부 복지서비스 정기 동기화 실패", e);
        }
    }
    
    /**
     * 매일 새벽 3시에 주요 지자체 복지서비스 동기화
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void syncLocalWelfareData() {
        log.info("=== 지자체 복지서비스 정기 동기화 시작 ===");
        
        // 주요 광역시/도 동기화
        String[] majorCities = {"11", "26", "27", "28", "29", "30", "31", "41", "42"};
        String[] cityNames = {"서울", "부산", "대구", "인천", "광주", "대전", "울산", "경기", "강원"};
        
        for (int i = 0; i < majorCities.length; i++) {
            try {
                log.info("{} 지자체 복지서비스 동기화 시작", cityNames[i]);
                welfareBenefitService.syncLocalWelfareServices(majorCities[i], null);
                log.info("{} 지자체 복지서비스 동기화 완료", cityNames[i]);
                
                // API 호출 간격 조절
                Thread.sleep(3000);
                
            } catch (Exception e) {
                log.error("{} 지자체 복지서비스 동기화 실패: {}", cityNames[i], e.getMessage());
            }
        }
        
        log.info("=== 지자체 복지서비스 정기 동기화 완료 ===");
    }
    
    /**
     * 매주 일요일 새벽 1시에 비활성 데이터 정리
     */
    @Scheduled(cron = "0 0 1 * * SUN")
    public void cleanupInactiveData() {
        log.info("=== 복지 데이터 정리 작업 시작 ===");
        
        try {
            // 30일 이상 업데이트되지 않은 데이터 비활성화
            // 실제 구현은 필요에 따라 추가
            log.info("복지 데이터 정리 작업 완료");
        } catch (Exception e) {
            log.error("복지 데이터 정리 작업 실패", e);
        }
    }
}
