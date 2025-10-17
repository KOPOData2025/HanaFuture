package com.hanaTI.HanaFuture.domain.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetAccountType {
    CHECKING("입출금통장"),
    SAVINGS("적금"),
    DEPOSIT("예금"),
    INVESTMENT("투자계좌"),
    STOCK("주식계좌"),
    FUND("펀드계좌"),
    LOAN("대출계좌"),
    MORTGAGE("주택담보대출"),
    CREDIT_CARD("신용카드"),
    DEBIT_CARD("체크카드"),
    FOREIGN_CURRENCY("외화계좌"),
    PENSION("연금계좌"),
    ISA("ISA계좌"),
    CMA("CMA계좌"),
    OTHER("기타");

    private final String description;
}
