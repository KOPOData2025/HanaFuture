package com.hanaTI.HanaFuture.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 적금 상품 타입
 */
@Getter
@RequiredArgsConstructor
public enum SavingsType {
    
    /**
     * 정기적금 - 매월 일정 금액 납입
     */
    REGULAR("정기적금", "매월 일정 금액을 납입하는 기본 적금"),
    
    /**
     * 자유적금 - 자유롭게 납입
     */
    FREE("자유적금", "원하는 때에 자유롭게 납입할 수 있는 적금"),
    
    /**
     * 청년도약계좌 - 정부 지원 청년 적금
     */
    YOUTH_LEAP("청년도약계좌", "정부 지원 청년층 대상 특별 적금"),
    
    /**
     * 주택청약종합저축 - 주택 구입을 위한 적금
     */
    HOUSING("주택청약종합저축", "주택 구입을 위한 특별 적금"),
    
    /**
     * 연금저축 - 노후 준비를 위한 적금
     */
    PENSION("연금저축", "노후 준비를 위한 장기 적금"),
    
    /**
     * 목적적금 - 특정 목적을 위한 적금
     */
    PURPOSE("목적적금", "특정 목적 달성을 위한 적금");

    private final String displayName;
    private final String description;
}








