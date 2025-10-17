package com.hanaTI.HanaFuture.domain.child.entity;

/**
 * 용돈 지급 주기
 */
public enum AllowanceCycle {
    WEEKLY("매주"),          // 주 단위 (초등 저학년 68%)
    MONTHLY("매월"),         // 월 단위 (중학생 45%)
    DAILY("매일"),           // 일 단위
    IRREGULAR("비정기");     // 필요할 때마다

    private final String description;

    AllowanceCycle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
