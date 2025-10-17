package com.hanaTI.HanaFuture.domain.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetConnectionStatus {
    CONNECTED("연결됨"),
    DISCONNECTED("연결해제"),
    ERROR("오류"),
    EXPIRED("만료됨"),
    PENDING("연결대기"),
    SUSPENDED("일시정지");

    private final String description;
}
