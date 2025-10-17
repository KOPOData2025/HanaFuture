package com.hanaTI.HanaFuture.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmploymentStatus {
    EMPLOYED("재직중"),
    UNEMPLOYED("구직중"),
    SELF_EMPLOYED("자영업"),
    STUDENT("학생"),
    HOUSEWIFE("주부"),
    RETIRED("은퇴"),
    OTHER("기타");

    private final String description;
}




















