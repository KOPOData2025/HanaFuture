package com.hanaTI.HanaFuture.domain.allowancecard.entity;

/**
 * 아이카드 상태
 */
public enum AllowanceCardStatus {
    ACTIVE("활성"),           // 정상 사용 가능
    SUSPENDED("일시정지"),     // 부모가 일시정지
    BLOCKED("차단"),          // 분실/도난 등으로 차단
    EXPIRED("만료");          // 카드 만료

    private final String description;

    AllowanceCardStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
