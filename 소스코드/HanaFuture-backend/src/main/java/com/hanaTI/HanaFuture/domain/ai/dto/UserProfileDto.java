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
public class UserProfileDto {
    
    // 기본 사용자 정보
    private Long userId;
    private String name;
    private Integer age;
    private String email;
    
    // 가족 정보
    private Boolean hasChildren;
    private Integer childrenCount;
    private List<ChildInfo> children;
    private Boolean isPregnant;
    private Boolean isMarried;
    
    // 지역 정보
    private String sidoName;    // 시도명 (예: 서울특별시)
    private String sigunguName; // 시군구명 (예: 강남구)
    
    // 재정 정보
    private Long monthlyIncome;
    private Long totalAssets;
    private Boolean hasGroupAccount;
    private Boolean hasSavingsAccount;
    
    // 선호 정보
    private List<String> preferredCategories; // 관심 있는 복지 카테고리
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildInfo {
        private String name;
        private Integer age;
        private String schoolType; // 어린이집, 유치원, 초등학교, 중학교, 고등학교
        private Boolean hasSpecialNeeds; // 특수 지원 필요 여부
    }
}



