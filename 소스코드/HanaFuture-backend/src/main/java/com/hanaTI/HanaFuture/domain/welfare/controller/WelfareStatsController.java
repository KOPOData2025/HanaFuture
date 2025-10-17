package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "Welfare Statistics", description = "복지 데이터 통계 API")
@RestController
@RequestMapping("/api/welfare-stats")
@RequiredArgsConstructor
public class WelfareStatsController {

    private final WelfareBenefitRepository welfareBenefitRepository;

    @Operation(
            summary = "복지 데이터 통계 조회",
            description = "데이터베이스에 저장된 복지 혜택 데이터의 통계를 조회합니다."
    )
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWelfareStats() {
        log.info(" 복지 데이터 통계 조회 요청");

        try {
            // 전체 복지 데이터 개수
            long totalCount = welfareBenefitRepository.count();
            
            // 활성 복지 데이터 개수
            long activeCount = welfareBenefitRepository.countByIsActiveTrue();
            
            // 중앙정부 복지 데이터 개수
            long centralCount = welfareBenefitRepository.countByServiceTypeAndIsActiveTrue(WelfareType.CENTRAL);
            
            // 지자체 복지 데이터 개수
            long localCount = welfareBenefitRepository.countByServiceTypeAndIsActiveTrue(WelfareType.LOCAL);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", totalCount);
            stats.put("activeCount", activeCount);
            stats.put("inactiveCount", totalCount - activeCount);
            stats.put("centralCount", centralCount);
            stats.put("localCount", localCount);
            
            log.info(" 복지 데이터 통계: 전체 {}개, 활성 {}개, 중앙정부 {}개, 지자체 {}개", 
                    totalCount, activeCount, centralCount, localCount);

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("복지 데이터 통계를 조회했습니다. (전체: %d개, 활성: %d개)", totalCount, activeCount),
                    stats));

        } catch (Exception e) {
            log.error("복지 데이터 통계 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error("복지 데이터 통계 조회에 실패했습니다."));
        }
    }

    @Operation(
            summary = "복지 데이터 샘플 조회",
            description = "복지 데이터의 샘플을 조회합니다."
    )
    @GetMapping("/sample")
    public ResponseEntity<ApiResponse<Object>> getWelfareSample(
            @RequestParam(defaultValue = "5") int limit) {
        log.info(" 복지 데이터 샘플 조회 요청: {}개", limit);

        try {
            var sampleData = welfareBenefitRepository.findByIsActiveTrueOrderByCreatedAtDesc(
                    org.springframework.data.domain.PageRequest.of(0, limit));

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("복지 데이터 샘플 %d개를 조회했습니다.", sampleData.getContent().size()),
                    sampleData.getContent()));

        } catch (Exception e) {
            log.error("복지 데이터 샘플 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error("복지 데이터 샘플 조회에 실패했습니다."));
        }
    }
}



