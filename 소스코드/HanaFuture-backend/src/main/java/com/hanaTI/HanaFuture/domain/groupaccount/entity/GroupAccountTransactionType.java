package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccountTransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    TRANSFER_IN("이체입금"),
    TRANSFER_OUT("이체출금"),
    INTEREST("이자"),
    FEE("수수료"),
    REFUND("환불"),
    ADJUSTMENT("조정");

    private final String description;
}
