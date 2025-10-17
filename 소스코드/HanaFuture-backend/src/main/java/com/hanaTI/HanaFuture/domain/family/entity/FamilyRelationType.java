package com.hanaTI.HanaFuture.domain.family.entity;

public enum FamilyRelationType {
    PARENT("부모"),
    CHILD("자녀"),
    SPOUSE("배우자"),
    SIBLING("형제자매"),
    GRANDPARENT("조부모"),
    GRANDCHILD("손자녀"),
    OTHER("기타");

    private final String description;

    FamilyRelationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

