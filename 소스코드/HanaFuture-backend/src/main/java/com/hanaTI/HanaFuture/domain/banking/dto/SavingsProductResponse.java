package com.hanaTI.HanaFuture.domain.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsProductResponse {
    
    private String productId;
    private String productName;
    private String description;
    private Double interestRate;
    private Long minAmount;
    private Long maxAmount;
    private String period;
    private List<String> benefits;
    private String category; // 일반, 청년, 육아 등
    private Boolean isRecommended;
    private String recommendReason;
}
