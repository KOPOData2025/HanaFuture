package com.hanaTI.HanaFuture.domain.child.entity;

/**
 * 자녀 상태
 */
public enum ChildStatus {
    ACTIVE("활성"),           // 정상 사용
    INACTIVE("비활성"),       // 일시 사용 중지
    GRADUATED("졸업");        // 성인이 되어 서비스 졸업

    private final String description;

    ChildStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
