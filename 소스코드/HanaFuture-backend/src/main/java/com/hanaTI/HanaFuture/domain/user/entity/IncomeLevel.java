package com.hanaTI.HanaFuture.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IncomeLevel {
    
    BASIC_LIVELIHOOD("기초생활수급자", "기준 중위소득 30% 이하"),
    LOW_INCOME("차상위계층", "기준 중위소득 50% 이하"),
    MIDDLE_LOW("중위소득 80% 이하", "일반 복지서비스 대상"),
    MIDDLE("중위소득 100% 이하", "일부 복지서비스 대상"),
    MIDDLE_HIGH("중위소득 150% 이하", "제한적 복지서비스 대상"),
    HIGH("고소득", "일반적으로 복지서비스 대상 아님");
    
    private final String displayName;
    private final String description;
}
