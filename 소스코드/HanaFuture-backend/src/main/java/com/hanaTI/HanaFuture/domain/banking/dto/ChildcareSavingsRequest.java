package com.hanaTI.HanaFuture.domain.banking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildcareSavingsRequest {
    
    @NotBlank(message = "상품 유형은 필수입니다")
    private String productType;
    
    @NotNull(message = "월 납입 금액은 필수입니다")
    @DecimalMin(value = "10000", message = "월 납입 금액은 최소 10,000원 이상이어야 합니다")
    @DecimalMax(value = "1000000", message = "월 납입 금액은 최대 1,000,000원 이하여야 합니다")
    private BigDecimal monthlyAmount;
    
    @NotNull(message = "적금 기간은 필수입니다")
    @Min(value = 6, message = "적금 기간은 최소 6개월 이상이어야 합니다")
    @Max(value = 60, message = "적금 기간은 최대 60개월 이하여야 합니다")
    private Integer periodMonths;
    
    @Size(max = 100, message = "계좌 별칭은 100자를 초과할 수 없습니다")
    private String accountAlias;
    
    @Builder.Default
    private Boolean autoTransfer = true;
    
    @Min(value = 1, message = "자동이체 날짜는 1일 이상이어야 합니다")
    @Max(value = 28, message = "자동이체 날짜는 28일 이하여야 합니다")
    @Builder.Default
    private Integer autoTransferDay = 25;
    
    // === 출금계좌 정보 ===
    
    @NotNull(message = "출금계좌 ID는 필수입니다")
    private Long withdrawAccountId;
}
