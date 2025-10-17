package com.hanaTI.HanaFuture.domain.lifecycle.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LifecycleEventType {
    
    // 임신 관련
    PREGNANCY_CONFIRMED("임신 확인", "임신이 확인되었습니다"),
    PREGNANCY_TRIMESTER_2("임신 중기", "임신 중기(4-6개월)에 접어들었습니다"),
    PREGNANCY_TRIMESTER_3("임신 후기", "임신 후기(7-9개월)에 접어들었습니다"),
    
    // 출산 관련
    BIRTH_EXPECTED("출산 예정", "출산 예정일이 다가왔습니다"),
    BIRTH_COMPLETED("출산 완료", "출산을 축하드립니다"),
    
    // 자녀 성장 관련
    CHILD_100_DAYS("백일", "아이의 백일을 축하드립니다"),
    CHILD_1_YEAR("돌", "아이의 첫 번째 생일을 축하드립니다"),
    CHILD_2_YEARS("만 2세", "아이가 만 2세가 되었습니다"),
    CHILD_3_YEARS("만 3세", "아이가 만 3세가 되었습니다"),
    CHILD_4_YEARS("만 4세", "아이가 만 4세가 되었습니다"),
    CHILD_5_YEARS("만 5세", "아이가 만 5세가 되었습니다"),
    CHILD_6_YEARS("만 6세", "아이가 만 6세가 되었습니다"),
    CHILD_7_YEARS("만 7세", "아이가 만 7세가 되었습니다"),
    
    // 교육 관련
    DAYCARE_ELIGIBLE("어린이집 입소 가능", "어린이집 입소가 가능한 나이가 되었습니다"),
    KINDERGARTEN_ELIGIBLE("유치원 입학 가능", "유치원 입학이 가능한 나이가 되었습니다"),
    ELEMENTARY_SCHOOL_ELIGIBLE("초등학교 입학 가능", "초등학교 입학이 가능한 나이가 되었습니다"),
    
    // 건강 관련
    HEALTH_CHECKUP_DUE("건강검진 시기", "정기 건강검진 시기입니다"),
    VACCINATION_DUE("예방접종 시기", "예방접종 시기입니다"),
    
    // 가족 상황 변화
    MARRIAGE("결혼", "결혼을 축하드립니다"),
    DIVORCE("이혼", "가족 상황이 변경되었습니다"),
    RELOCATION("이사", "거주지가 변경되었습니다"),
    INCOME_CHANGE("소득 변화", "소득 수준이 변경되었습니다"),
    
    // 기타
    BIRTHDAY("생일", "생일을 축하드립니다"),
    ANNIVERSARY("기념일", "특별한 기념일입니다");
    
    private final String displayName;
    private final String description;
    
    // 비즈니스 메서드
    public boolean isChildRelated() {
        return this.name().startsWith("CHILD_") || 
               this == BIRTH_COMPLETED || 
               this == CHILD_100_DAYS ||
               this.name().contains("ELIGIBLE");
    }
    
    public boolean isPregnancyRelated() {
        return this.name().startsWith("PREGNANCY_") || 
               this == BIRTH_EXPECTED || 
               this == BIRTH_COMPLETED;
    }
    
    public boolean isEducationRelated() {
        return this.name().contains("ELIGIBLE") || 
               this.name().contains("SCHOOL") || 
               this.name().contains("DAYCARE") || 
               this.name().contains("KINDERGARTEN");
    }
    
    public boolean isHealthRelated() {
        return this.name().contains("HEALTH") || 
               this.name().contains("VACCINATION");
    }
    
    public boolean requiresImmediateAction() {
        return this == BIRTH_EXPECTED || 
               this == HEALTH_CHECKUP_DUE || 
               this == VACCINATION_DUE ||
               this.name().contains("ELIGIBLE");
    }
    
    public int getPriorityLevel() {
        if (requiresImmediateAction()) return 1; // 높음
        if (isChildRelated() || isPregnancyRelated()) return 2; // 중간
        return 3; // 낮음
    }
}
