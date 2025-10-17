package com.hanaTI.HanaFuture.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelfareRecommendationDto {
    
    private Long userId;
    private String recommendationSummary; // AI가 생성한 추천 요약
    private List<String> recommendedKeywords; // 추천 키워드들
    private List<String> priorityCategories; // 우선순위 카테고리
    private String reasoningExplanation; // 추천 근거 설명
    private Integer confidenceScore; // 신뢰도 점수 (1-100)
    
    // 필터링 조건들
    private FilterCriteria filterCriteria;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterCriteria {
        private List<String> includeKeywords;  // 포함해야 할 키워드
        private List<String> excludeKeywords;  // 제외해야 할 키워드
        private List<String> lifeCycles;       // 해당 생애주기
        private List<String> serviceTypes;     // 서비스 유형 (CENTRAL/LOCAL)
        private String region;                 // 지역 (시도명)
        private Integer minSupportAmount;      // 최소 지원금액
        private Integer maxAge;                // 최대 연령 제한
        private Boolean requiresChildren;      // 자녀 필수 여부
    }
}



