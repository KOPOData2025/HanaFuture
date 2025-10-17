package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccountPurpose {
    TRAVEL("여행"),
    WEDDING("결혼"),
    SAVINGS("적금"),
    INVESTMENT("투자"),
    EMERGENCY("비상금"),
    GIFT("선물"),
    EDUCATION("교육"),
    HOUSING("주거"),
    BUSINESS("사업"),
    FAMILY("가족"),
    OTHER("기타");

    private final String description;
}
