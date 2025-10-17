package com.hanaTI.HanaFuture.domain.allowancecard.entity;

/**
 * 아이카드 충전 유형
 */
public enum AllowanceCardChargeType {
    MANUAL("수동 충전"),       // 부모가 직접 충전
    AUTO("자동 충전"),         // 잔액 부족 시 자동 충전
    SCHEDULED("정기 충전");    // 매월 정기 충전

    private final String description;

    AllowanceCardChargeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
