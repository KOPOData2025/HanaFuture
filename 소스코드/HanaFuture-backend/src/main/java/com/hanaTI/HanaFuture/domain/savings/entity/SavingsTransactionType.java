package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 적금 거래 유형
 */
@Getter
@RequiredArgsConstructor
public enum SavingsTransactionType {
    
    /**
     * 입금 - 적금 납입
     */
    DEPOSIT("입금", "적금 납입"),
    
    /**
     * 자동이체 - 자동이체를 통한 납입
     */
    AUTO_TRANSFER("자동이체", "자동이체를 통한 납입"),
    
    /**
     * 이자입금 - 이자 지급
     */
    INTEREST("이자입금", "이자 지급"),
    
    /**
     * 출금 - 중도인출 (가능한 경우)
     */
    WITHDRAWAL("출금", "중도인출"),
    
    /**
     * 해지 - 계좌 해지
     */
    TERMINATION("해지", "계좌 해지"),
    
    /**
     * 만기지급 - 만기 시 원금과 이자 지급
     */
    MATURITY_PAYMENT("만기지급", "만기 시 원금과 이자 지급");

    private final String displayName;
    private final String description;
}








