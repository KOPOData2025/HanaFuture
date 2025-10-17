package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.ai.service.WelfareDataMigrationService;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "HanaFuture Welfare", description = "하나퓨처 맞춤 복지 혜택 API (AI 필터링 완료)")
@RestController
@RequestMapping("/api/hana-future-welfare")
@RequiredArgsConstructor
public class HanaFutureWelfareController {

    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;
    private final WelfareDataMigrationService migrationService;

    @Operation(
            summary = "혜택 상세 조회",
            description = "혜택 ID로 단일 혜택의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WelfareBenefitResponse>> getBenefitById(
            @Parameter(description = "혜택 ID") @PathVariable Long id) {
        
        log.info(" 혜택 상세 조회 요청 - ID: {}", id);
        
        HanaFutureWelfareBenefit benefit = hanaFutureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("혜택을 찾을 수 없습니다: " + id));
        
        WelfareBenefitResponse response = convertToResponse(benefit);
        
        log.info("혜택 상세 조회 완료: {}", benefit.getServiceName());
        
        return ResponseEntity.ok(ApiResponse.success(
                "혜택 상세 정보를 조회했습니다.", response));
    }

    @Operation(
            summary = "하나퓨처 맞춤 전체 복지 혜택 조회",
            description = "AI로 필터링된 하나퓨처 서비스에 최적화된 전체 복지 혜택을 조회합니다. "
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getAllHanaFutureBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        log.info(" 하나퓨처 맞춤 전체 복지 혜택 요청 - 페이지: {}, 크기: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        // 균형있는 정렬을 위해 서비스 타입도 고려
        Page<HanaFutureWelfareBenefit> benefits = hanaFutureRepository
                .findByIsActiveTrueOrderByServiceTypeAscAiRelevanceScoreDescCreatedAtDesc(pageable);
        
        Page<WelfareBenefitResponse> response = benefits.map(this::convertToResponse);
        
        // 서비스 타입별 분포 디버깅
        long centralCount = response.getContent().stream()
                .mapToLong(b -> "CENTRAL".equals(b.getServiceType()) ? 1 : 0)
                .sum();
        long localCount = response.getContent().stream()
                .mapToLong(b -> "LOCAL".equals(b.getServiceType()) ? 1 : 0)
                .sum();
        
        log.info("하나퓨처 맞춤 복지 혜택 조회 완료: {}개 (전체 {}개)", 
                response.getNumberOfElements(), response.getTotalElements());
        log.info(" 서비스 타입 분포 - 중앙정부: {}개, 지자체: {}개", centralCount, localCount);
        
        return ResponseEntity.ok(ApiResponse.success(
                "하나퓨처 맞춤 복지 혜택을 조회했습니다.", response));
    }

    @Operation(
            summary = "하나퓨처 맞춤 중앙정부 복지 혜택 조회",
            description = "AI로 필터링된 중앙정부 복지 혜택을 조회합니다."
    )
    @GetMapping("/central")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getCentralBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        log.info(" 하나퓨처 맞춤 중앙정부 복지 혜택 요청 - 페이지: {}, 크기: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HanaFutureWelfareBenefit> benefits = hanaFutureRepository
                .findByServiceTypeAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc("CENTRAL", pageable);
        
        Page<WelfareBenefitResponse> response = benefits.map(this::convertToResponse);
        
        return ResponseEntity.ok(ApiResponse.success(
                "하나퓨처 맞춤 중앙정부 복지 혜택을 조회했습니다.", response));
    }

    @Operation(
            summary = "하나퓨처 맞춤 지자체 복지 혜택 조회",
            description = "AI로 필터링된 지자체 복지 혜택을 조회합니다."
    )
    @GetMapping("/local")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getLocalBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        log.info(" 하나퓨처 맞춤 지자체 복지 혜택 요청 - 페이지: {}, 크기: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HanaFutureWelfareBenefit> benefits = hanaFutureRepository
                .findByServiceTypeAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc("LOCAL", pageable);
        
        Page<WelfareBenefitResponse> response = benefits.map(this::convertToResponse);
        
        return ResponseEntity.ok(ApiResponse.success(
                "하나퓨처 맞춤 지자체 복지 혜택을 조회했습니다.", response));
    }

    @Operation(
            summary = "하나퓨처 맞춤 복지 혜택 검색",
            description = "키워드로 하나퓨처 맞춤 복지 혜택을 검색합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> searchBenefits(
            @Parameter(description = "검색 키워드", example = "육아") @RequestParam String keyword,
            @Parameter(description = "서비스 타입", example = "CENTRAL") @RequestParam(required = false) String serviceType,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        log.info(" 하나퓨처 맞춤 복지 혜택 검색 - 키워드: '{}', 타입: {}", keyword, serviceType);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HanaFutureWelfareBenefit> benefits;
        
        if (serviceType != null) {
            benefits = hanaFutureRepository.findByServiceTypeAndKeywordAndIsActiveTrue(serviceType, keyword, pageable);
        } else {
            benefits = hanaFutureRepository.findByKeywordAndIsActiveTrue(keyword, pageable);
        }
        
        Page<WelfareBenefitResponse> response = benefits.map(this::convertToResponse);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("'%s' 검색 결과입니다.", keyword), response));
    }

    @Operation(
            summary = "하나퓨처 맞춤 복지 혜택 통계",
            description = "하나퓨처 맞춤 복지 혜택의 통계 정보를 조회합니다."
    )
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getHanaFutureWelfareStats() {
        log.info(" 하나퓨처 맞춤 복지 혜택 통계 요청");
        
        long totalCount = hanaFutureRepository.countByIsActiveTrue();
        long centralCount = hanaFutureRepository.countByServiceTypeAndIsActiveTrue("CENTRAL");
        long localCount = hanaFutureRepository.countByServiceTypeAndIsActiveTrue("LOCAL");
        
        var stats = new java.util.HashMap<String, Object>();
        stats.put("totalCount", totalCount);
        stats.put("centralCount", centralCount);
        stats.put("localCount", localCount);
        stats.put("description", "AI로 필터링된 하나퓨처 맞춤 복지 혜택");
        
        return ResponseEntity.ok(ApiResponse.success("하나퓨처 맞춤 복지 혜택 통계입니다.", stats));
    }

    @Operation(
            summary = "AI 필터링 데이터 이관 실행",
            description = "원본 데이터에서 AI 필터링을 통해 하나퓨처 테이블로 데이터를 이관"
    )
    @PostMapping("/migrate")
    public ResponseEntity<ApiResponse<String>> migrateAIFilteredData() {
        log.info(" AI 필터링 데이터 이관 요청");
        
        try {
            migrationService.migrateAIFilteredData();
            
            // 이관 후 통계 출력
            migrationService.printHanaFutureWelfareStats();
            
            return ResponseEntity.ok(ApiResponse.success(
                    "AI 필터링 데이터 이관이 완료되었습니다.", 
                    "원본 데이터에서 AI 필터링을 통해 하나퓨처 맞춤 데이터로 이관했습니다."));
            
        } catch (Exception e) {
            log.error("데이터 이관 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(
                    "데이터 이관 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * HanaFutureWelfareBenefit을 WelfareBenefitResponse로 변환
     */
    private WelfareBenefitResponse convertToResponse(HanaFutureWelfareBenefit benefit) {
        WelfareType parsedType = parseServiceType(benefit.getServiceType());

        log.debug("혜택: '{}' - serviceType='{}' -> {}, sido='{}', sigungu='{}'", 
            benefit.getServiceName(), 
            benefit.getServiceType(), 
            parsedType,
            benefit.getSidoName(),
            benefit.getSigunguName());
        
        return WelfareBenefitResponse.builder()
                .id(benefit.getId())
                .serviceId(benefit.getServiceId())
                .serviceName(benefit.getServiceName())
                .serviceType(parsedType)
                .serviceContent(benefit.getServiceContent())
                .applicationMethod(benefit.getApplicationMethod())
                .targetDescription(benefit.getTargetDescription())
                .supportAmount(benefit.getSupportAmount())
                .category(benefit.getCategory())
                .lifeCycle(benefit.getLifeCycle())
                .areaCode(benefit.getRegionCode()) // regionCode를 areaCode로 매핑
                .sidoName(benefit.getSidoName())
                .sigunguName(benefit.getSigunguName())
                .inquiryUrl(getFirstAvailableUrl(benefit)) // 사용 가능한 첫 번째 URL 사용
                .jurisdictionName(benefit.getReceptionAgency()) // receptionAgency를 jurisdictionName으로 매핑
                .build();
    }

    /**
     * 사용 가능한 첫 번째 URL 반환
     */
    private String getFirstAvailableUrl(HanaFutureWelfareBenefit benefit) {
        // 우선순위: relatedUrl -> applicationSiteUrl -> referenceUrl1 -> referenceUrl2
        if (benefit.getRelatedUrl() != null && !benefit.getRelatedUrl().trim().isEmpty()) {
            return benefit.getRelatedUrl();
        }
        if (benefit.getApplicationSiteUrl() != null && !benefit.getApplicationSiteUrl().trim().isEmpty()) {
            return benefit.getApplicationSiteUrl();
        }
        if (benefit.getReferenceUrl1() != null && !benefit.getReferenceUrl1().trim().isEmpty()) {
            return benefit.getReferenceUrl1();
        }
        if (benefit.getReferenceUrl2() != null && !benefit.getReferenceUrl2().trim().isEmpty()) {
            return benefit.getReferenceUrl2();
        }
        return null; // 모든 URL이 없는 경우
    }

    /**
     * String을 WelfareType enum으로 안전하게 변환
     */
    private WelfareType parseServiceType(String serviceTypeStr) {
        if (serviceTypeStr == null || serviceTypeStr.trim().isEmpty()) {
            log.warn("serviceType이 null 또는 비어있음");
            return null;
        }
        
        String trimmed = serviceTypeStr.trim();
        
        // 1. 정확한 enum 값 시도
        try {
            WelfareType type = WelfareType.valueOf(trimmed.toUpperCase());
            log.debug("serviceType 변환 성공: '{}' -> {}", serviceTypeStr, type);
            return type;
        } catch (IllegalArgumentException e) {
            // 2. 한글 또는 다른 형식 처리
            if (trimmed.contains("중앙") || trimmed.equalsIgnoreCase("CENTRAL")) {
                log.info("serviceType '{}' -> CENTRAL로 변환", serviceTypeStr);
                return WelfareType.CENTRAL;
            }
            if (trimmed.contains("지자체") || trimmed.contains("지방") || trimmed.equalsIgnoreCase("LOCAL")) {
                log.info("serviceType '{}' -> LOCAL로 변환", serviceTypeStr);
                return WelfareType.LOCAL;
            }
            
            log.warn("알 수 없는 serviceType: '{}' - null로 처리", serviceTypeStr);
            return null;
        }
    }

}
