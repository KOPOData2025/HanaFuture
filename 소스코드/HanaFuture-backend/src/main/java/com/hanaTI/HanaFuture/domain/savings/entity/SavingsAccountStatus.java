package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 적금 계좌 상태
 */
@Getter
@RequiredArgsConstructor
public enum SavingsAccountStatus {
    
    /**
     * 활성 - 정상적으로 운영 중인 적금 계좌
     */
    ACTIVE("활성", "정상적으로 운영 중인 적금 계좌"),
    
    /**
     * 일시정지 - 자동이체가 일시 중단된 상태
     */
    SUSPENDED("일시정지", "자동이체가 일시 중단된 상태"),
    
    /**
     * 만기 - 가입 기간이 완료된 상태
     */
    MATURED("만기", "가입 기간이 완료된 상태"),
    
    /**
     * 해지 - 중도해지 또는 만기해지된 상태
     */
    TERMINATED("해지", "중도해지 또는 만기해지된 상태"),
    
    /**
     * 휴면 - 장기간 거래가 없는 상태
     */
    DORMANT("휴면", "장기간 거래가 없는 상태");

    private final String displayName;
    private final String description;
}








