package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipantStatus {
    PENDING("PENDING", "참여 대기"),
    ACTIVE("ACTIVE", "활성 참여"),
    INACTIVE("INACTIVE", "비활성");
    
    private final String key;
    private final String description;
}


















