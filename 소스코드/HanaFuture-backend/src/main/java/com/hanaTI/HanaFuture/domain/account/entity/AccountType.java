package com.hanaTI.HanaFuture.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    
    CHECKING("입출금통장", "일반적인 입출금 계좌"),
    SAVINGS("적금", "정기적으로 돈을 저축하는 계좌"),
    DEPOSIT("예금", "일정 기간 돈을 맡기는 계좌"),
    FAMILY_JOINT("가족공동계좌", "가족이 함께 사용하는 계좌"),
    CHILD_EDUCATION("자녀교육비", "자녀 교육을 위한 전용 계좌"),
    EMERGENCY("비상금", "비상시를 대비한 계좌");
    
    private final String displayName;
    private final String description;
    
    public String getKey() {
        return name();
    }
}

