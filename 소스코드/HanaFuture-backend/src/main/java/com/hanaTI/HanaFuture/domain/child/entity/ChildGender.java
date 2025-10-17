package com.hanaTI.HanaFuture.domain.child.entity;

/**
 * 자녀 성별
 */
public enum ChildGender {
    MALE("남자"),
    FEMALE("여자");

    private final String description;

    ChildGender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
