package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccountMemberStatus {
    ACTIVE("활성"),
    INVITED("초대됨"),
    PENDING("대기중"),
    REJECTED("거절됨"),
    SUSPENDED("정지"),
    LEFT("탈퇴");

    private final String description;
}
