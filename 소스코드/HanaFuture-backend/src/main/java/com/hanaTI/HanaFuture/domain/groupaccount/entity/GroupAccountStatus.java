package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccountStatus {
    ACTIVE("활성"),
    SUSPENDED("일시정지"),
    CLOSED("해지"),
    PENDING("대기중");

    private final String description;
}








