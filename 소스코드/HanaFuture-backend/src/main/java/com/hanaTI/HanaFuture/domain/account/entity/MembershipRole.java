package com.hanaTI.HanaFuture.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipRole {
    OWNER("OWNER", "소유자"),
    CO_OWNER("CO_OWNER", "공동소유자"),
    MEMBER("MEMBER", "멤버");
    
    private final String key;
    private final String description;
}


















