package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SavingStatus {
    ACTIVE("ACTIVE", "진행 중"),
    COMPLETED("COMPLETED", "목표 달성"),
    EXPIRED("EXPIRED", "만료됨"),
    TERMINATED("TERMINATED", "중도 해지"),
    PAUSED("PAUSED", "일시 중단");
    
    private final String key;
    private final String description;
}


















