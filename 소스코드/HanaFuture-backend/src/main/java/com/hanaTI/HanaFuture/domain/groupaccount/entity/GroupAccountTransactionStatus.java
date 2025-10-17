package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccountTransactionStatus {
    PENDING("대기중"),
    COMPLETED("완료"),
    FAILED("실패"),
    CANCELLED("취소");

    private final String description;
}
