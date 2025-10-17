package com.hanaTI.HanaFuture.domain.notification.entity;

public enum NotificationStatus {
    UNREAD("읽지않음"),
    READ("읽음"),
    PENDING("대기중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨"),
    EXPIRED("만료됨");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

