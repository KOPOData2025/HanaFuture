package com.hanaTI.HanaFuture.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaritalStatus {
    
    SINGLE("미혼"),
    MARRIED("기혼"),
    DIVORCED("이혼"),
    WIDOWED("사별"),
    SEPARATED("별거");
    
    private final String displayName;
}
