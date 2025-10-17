package com.hanaTI.HanaFuture.domain.allowancecard.entity;

/**
 * 부모 승인 상태
 */
public enum ParentApprovalStatus {
    PENDING("승인 대기"),      // 부모 승인 대기 중
    APPROVED("승인됨"),       // 부모가 승인
    REJECTED("거부됨");       // 부모가 거부

    private final String description;

    ParentApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
