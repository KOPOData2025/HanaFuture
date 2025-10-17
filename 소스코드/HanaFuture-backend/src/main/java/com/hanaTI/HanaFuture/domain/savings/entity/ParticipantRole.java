package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipantRole {
    OWNER("OWNER", "주 계약자"),
    CO_SAVER("CO_SAVER", "공동 저축자"),
    SUPPORTER("SUPPORTER", "응원자");
    
    private final String key;
    private final String description;
}


















