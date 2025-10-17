package com.hanaTI.HanaFuture.domain.welfare.config;

import com.hanaTI.HanaFuture.domain.welfare.service.WelfareBenefitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 애플리케이션 시작 시 복지 데이터 초기화
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class WelfareDataInitializer implements ApplicationRunner {
    
    private final WelfareBenefitService welfareBenefitService;
    private final WelfareApiProperties welfareApiProperties;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== 복지 데이터 초기화 시작 ===");
        
        // 서비스 키가 설정되어 있는지 확인
        if (welfareApiProperties.getServiceKey() == null || 
            welfareApiProperties.getServiceKey().equals("your-service-key-here")) {
            log.warn("복지로 API 서비스 키가 설정되지 않았습니다. 데이터 동기화를 건너뜁니다.");
            log.warn("환경변수 WELFARE_API_KEY를 설정해주세요.");
            return;
        }
        
        // 비동기로 데이터 동기화 실행 (애플리케이션 시작 속도에 영향 없음)
        CompletableFuture.runAsync(() -> {
            try {
                log.info("백그라운드에서 복지 데이터 동기화 시작");
                
                // 1. 중앙정부 복지서비스 동기화
                log.info("중앙정부 복지서비스 동기화 시작...");
                welfareBenefitService.syncCentralWelfareServices();
                log.info("중앙정부 복지서비스 동기화 완료");
                
                // 2. 지자체 복지서비스 동기화 (주요 지역)
                String[] majorCities = {"서울특별시", "부산광역시", "경기도"};
                for (String city : majorCities) {
                    try {
                        log.info("{} 지자체 복지서비스 동기화 시작...", city);
                        welfareBenefitService.syncLocalWelfareServices(city, null);
                        log.info("{} 지자체 복지서비스 동기화 완료", city);
                        Thread.sleep(2000); // API 호출 간격
                    } catch (Exception e) {
                        log.error("{} 지자체 동기화 실패: {}", city, e.getMessage());
                    }
                }
                
                log.info("=== 전체 복지 데이터 동기화 완료 ===");
                
            } catch (Exception e) {
                log.error("복지 데이터 초기화 실패", e);
            }
        });
        
        log.info("복지 데이터 동기화가 백그라운드에서 시작되었습니다.");
    }
}
