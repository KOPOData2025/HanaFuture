package com.hanaTI.HanaFuture.domain.allowancecard.entity;

/**
 * 아이카드 사용 상태
 */
public enum AllowanceCardUsageStatus {
    APPROVED("승인"),         // 사용 승인
    PENDING("대기"),          // 부모 승인 대기
    CANCELLED("취소"),        // 사용 취소
    REFUNDED("환불");         // 환불 처리

    private final String description;

    AllowanceCardUsageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
