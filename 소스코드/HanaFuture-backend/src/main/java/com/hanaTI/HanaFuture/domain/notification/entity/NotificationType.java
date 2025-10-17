package com.hanaTI.HanaFuture.domain.notification.entity;

public enum NotificationType {
    GROUP_ACCOUNT_INVITE("모임통장 초대"),
    GROUP_ACCOUNT_DEPOSIT("모임통장 입금"),
    GROUP_ACCOUNT_WITHDRAW("모임통장 출금"),
    SAVINGS_MATURE("적금 만기"),
    WELFARE_RECOMMEND("복지혜택 추천"),
    SYSTEM_NOTICE("시스템 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

