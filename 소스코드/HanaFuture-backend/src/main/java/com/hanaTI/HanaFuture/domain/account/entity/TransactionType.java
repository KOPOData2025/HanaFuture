package com.hanaTI.HanaFuture.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT("DEPOSIT", "입금"),
    WITHDRAWAL("WITHDRAWAL", "출금"),
    TRANSFER_IN("TRANSFER_IN", "이체 입금"),
    TRANSFER_OUT("TRANSFER_OUT", "이체 출금"),
    CARD_PAYMENT("CARD_PAYMENT", "카드 결제"),
    GOVERNMENT_BENEFIT("GOVERNMENT_BENEFIT", "정부 지원금"),
    AUTO_TRANSFER("AUTO_TRANSFER", "자동 이체"),
    SALARY("SALARY", "급여"),
    ONLINE_PAYMENT("ONLINE_PAYMENT", "온라인 결제");
    
    private final String key;
    private final String description;
}