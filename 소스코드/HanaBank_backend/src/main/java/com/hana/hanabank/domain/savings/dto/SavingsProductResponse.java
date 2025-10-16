package com.hana.hanabank.domain.savings.dto;

import com.hana.hanabank.domain.savings.entity.SavingsProduct;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class SavingsProductResponse {
    private Long id;
    private String name;
    private String productCode;
    private BigDecimal baseInterestRate;
    private BigDecimal maxInterestRate;
    private Integer minPeriodMonths;
    private Integer maxPeriodMonths;
    private Long minMonthlyAmount;
    private Long maxMonthlyAmount;
    private String description;
    private String targetCustomer;
    private List<String> benefits;
    private List<String> features;

    public static SavingsProductResponse from(SavingsProduct product) {
        return SavingsProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .productCode(product.getProductCode())
                .baseInterestRate(product.getBaseInterestRate())
                .maxInterestRate(product.getMaxInterestRate())
                .minPeriodMonths(product.getMinPeriodMonths())
                .maxPeriodMonths(product.getMaxPeriodMonths())
                .minMonthlyAmount(product.getMinMonthlyAmount())
                .maxMonthlyAmount(product.getMaxMonthlyAmount())
                .description(product.getDescription())
                .targetCustomer(product.getTargetCustomer())
                .benefits(product.getBenefits())
                .features(product.getFeatures())
                .build();
    }
}
