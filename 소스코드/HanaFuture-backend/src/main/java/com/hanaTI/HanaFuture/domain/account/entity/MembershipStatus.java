package com.hanaTI.HanaFuture.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipStatus {
    PENDING("PENDING", "승인 대기"),
    ACTIVE("ACTIVE", "활성"),
    INACTIVE("INACTIVE", "비활성"),
    REJECTED("REJECTED", "거절됨");
    
    private final String key;
    private final String description;
}


















