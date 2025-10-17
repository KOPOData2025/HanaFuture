package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SavingType {
    REGULAR("REGULAR", "정기 적금", "매월 일정 금액 납입"),
    FREE("FREE", "자유 적금", "자유롭게 납입 가능"),
    MIXED("MIXED", "혼합형", "정기 + 자유 납입");
    
    private final String key;
    private final String name;
    private final String description;
}


















