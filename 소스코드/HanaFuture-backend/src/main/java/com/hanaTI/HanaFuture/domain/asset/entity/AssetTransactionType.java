package com.hanaTI.HanaFuture.domain.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetTransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    TRANSFER_IN("이체입금"),
    TRANSFER_OUT("이체출금"),
    PAYMENT("결제"),
    REFUND("환불"),
    INTEREST("이자"),
    FEE("수수료"),
    DIVIDEND("배당금"),
    BUY("매수"),
    SELL("매도"),
    LOAN_DISBURSEMENT("대출실행"),
    LOAN_REPAYMENT("대출상환"),
    CARD_PAYMENT("카드결제"),
    CARD_REFUND("카드환불"),
    OTHER("기타");

    private final String description;
}
