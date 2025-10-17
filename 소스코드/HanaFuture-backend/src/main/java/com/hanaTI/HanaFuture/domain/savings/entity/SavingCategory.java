package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SavingCategory {
    BIRTH_PREPARATION("BIRTH_PREPARATION", "출산 준비", "", "출산 관련 비용 준비"),
    CHILDCARE("CHILDCARE", "육아 자금", "", "육아용품, 육아휴직 대비"),
    EDUCATION("EDUCATION", "교육비", "", "자녀 교육비 마련"),
    HOUSING("HOUSING", "내집 마련", "", "주택 구입 자금"),
    EMERGENCY("EMERGENCY", "비상 자금", "", "응급 상황 대비"),
    VACATION("VACATION", "가족 여행", "", "가족 여행 경비"),
    WEDDING("WEDDING", "결혼 자금", "", "결혼 준비 비용"),
    OTHER("OTHER", "기타 목표", "", "기타 저축 목표");
    
    private final String key;
    private final String name;
    private final String emoji;
    private final String description;
}


















