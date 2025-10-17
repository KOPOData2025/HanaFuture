package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DepositType {
    REGULAR("REGULAR", "정기 납입"),
    MANUAL("MANUAL", "수동 납입"),
    BONUS("BONUS", "보너스 납입"),
    TRANSFER("TRANSFER", "이체 납입");
    
    private final String key;
    private final String description;
}


















