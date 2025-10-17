package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccountPermission {
    VIEW_ACCOUNT("계좌 조회"),
    MANAGE_MEMBERS("멤버 관리"),
    MANAGE_SETTINGS("설정 관리"),
    WITHDRAW_MONEY("출금"),
    VIEW_TRANSACTIONS("거래내역 조회"),
    DELETE_ACCOUNT("계좌 삭제");

    private final String description;
}
