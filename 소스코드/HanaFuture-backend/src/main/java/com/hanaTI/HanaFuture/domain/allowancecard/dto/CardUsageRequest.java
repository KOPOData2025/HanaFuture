package com.hanaTI.HanaFuture.domain.allowancecard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "아이카드 사용 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardUsageRequest {
    
    @Schema(description = "사용 금액", example = "5000", required = true)
    @NotNull(message = "사용 금액은 필수입니다.")
    @Positive(message = "사용 금액은 양수여야 합니다.")
    private BigDecimal amount;
    
    @Schema(description = "가맹점명", example = "GS25 강남점", required = true)
    @NotBlank(message = "가맹점명은 필수입니다.")
    private String merchantName;
    
    @Schema(description = "카테고리", example = "편의점")
    private String category;
    
    @Schema(description = "사용 설명", example = "간식 구매")
    private String description;
}










