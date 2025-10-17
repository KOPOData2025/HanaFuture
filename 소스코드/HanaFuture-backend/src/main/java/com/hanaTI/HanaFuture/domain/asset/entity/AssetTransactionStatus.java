package com.hanaTI.HanaFuture.domain.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetTransactionStatus {
    COMPLETED("완료"),
    PENDING("대기중"),
    FAILED("실패"),
    CANCELLED("취소");

    private final String description;
}
