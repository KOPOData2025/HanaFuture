package com.hanaTI.HanaFuture.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE("ACTIVE", "활성"),
    INACTIVE("INACTIVE", "비활성"),
    SUSPENDED("SUSPENDED", "일시정지");
    
    private final String key;
    private final String description;
}


















