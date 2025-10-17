package com.hanaTI.HanaFuture.global.controller;

import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 컨트롤러 (인증 불필요)
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final WelfareBenefitRepository originalRepository;
    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;

    @GetMapping("/data-status")
    public Map<String, Object> getDataStatus() {
        log.info(" 데이터 상태 확인 요청");
        
        Map<String, Object> status = new HashMap<>();
        
        try {
            long originalCount = originalRepository.countByIsActiveTrue();
            long hanaFutureCount = hanaFutureRepository.count();
            long hanaFutureActiveCount = hanaFutureRepository.countByIsActiveTrue();
            
            status.put("success", true);
            status.put("originalWelfareCount", originalCount);
            status.put("hanaFutureWelfareCount", hanaFutureCount);
            status.put("hanaFutureActiveCount", hanaFutureActiveCount);
            status.put("migrationStatus", hanaFutureCount > 0 ? "COMPLETED" : "NOT_STARTED");
            status.put("timestamp", System.currentTimeMillis());
            
            log.info(" 데이터 상태: 원본={}개, 하나퓨처={}개(활성={}개)", 
                    originalCount, hanaFutureCount, hanaFutureActiveCount);
            
        } catch (Exception e) {
            log.error("데이터 상태 확인 실패: {}", e.getMessage(), e);
            status.put("success", false);
            status.put("error", e.getMessage());
        }
        
        return status;
    }
}



