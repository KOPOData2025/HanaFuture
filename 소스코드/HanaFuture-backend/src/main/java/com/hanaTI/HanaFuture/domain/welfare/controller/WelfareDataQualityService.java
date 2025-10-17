package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 복지 데이터 품질 분석 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareDataQualityService {
    
    private final WelfareBenefitRepository welfareBenefitRepository;
    
    /**
     * 데이터 품질 리포트 생성
     */
    public Map<String, Object> generateQualityReport() {
        Map<String, Object> report = new HashMap<>();
        
        // 전체 데이터 개수
        long totalCount = welfareBenefitRepository.count();
        report.put("totalCount", totalCount);
        
        // NULL 값 통계
        Map<String, Long> nullCounts = new HashMap<>();
        nullCounts.put("supportAmount", countNullSupportAmount());
        nullCounts.put("applicationMethod", countNullApplicationMethod());
        nullCounts.put("category", countNullCategory());
        nullCounts.put("areaCode", countNullAreaCode());
        nullCounts.put("supportCycle", countNullSupportCycle());
        
        report.put("nullCounts", nullCounts);
        
        // NULL 비율 계산
        Map<String, Double> nullRatios = new HashMap<>();
        for (Map.Entry<String, Long> entry : nullCounts.entrySet()) {
            double ratio = totalCount > 0 ? (entry.getValue() * 100.0 / totalCount) : 0.0;
            nullRatios.put(entry.getKey(), Math.round(ratio * 100.0) / 100.0);
        }
        report.put("nullRatios", nullRatios);
        
        // 데이터 품질 점수 (100 - 평균 NULL 비율)
        double avgNullRatio = nullRatios.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        double qualityScore = Math.max(0, 100 - avgNullRatio);
        report.put("overallQualityScore", Math.round(qualityScore * 100.0) / 100.0);
        
        log.info("데이터 품질 리포트 생성 완료 - 전체: {}건, 품질점수: {}%", 
                totalCount, qualityScore);
        
        return report;
    }
    
    /**
     * 품질이 낮은 데이터 조회
     */
    public Page<WelfareBenefit> getLowQualityData(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 중요 필드가 null인 데이터들 조회
        return welfareBenefitRepository.findBySupportAmountIsNullOrApplicationMethodIsNull(pageable);
    }
    
    // === NULL 개수 조회 메서드들 ===
    
    private long countNullSupportAmount() {
        return welfareBenefitRepository.countBySupportAmountIsNull();
    }
    
    private long countNullApplicationMethod() {
        return welfareBenefitRepository.countByApplicationMethodIsNull();
    }
    
    private long countNullCategory() {
        return welfareBenefitRepository.countByCategoryIsNull();
    }
    
    private long countNullAreaCode() {
        return welfareBenefitRepository.countByAreaCodeIsNull();
    }
    
    private long countNullSupportCycle() {
        return welfareBenefitRepository.countBySupportCycleIsNull();
    }
}

