package com.hana.hanabank.domain.savings.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsRecommendationRequest {
    private String userId;
    private Long monthlyAmount;
    private Integer periodMonths;
    private String purpose; // EDUCATION, HOUSING, RETIREMENT, etc.
    private String targetCustomer; // 육아, 청년, 직장인, etc.
}
