package com.hanaTI.HanaFuture.domain.allowancecard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "아이카드 충전 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardChargeRequest {
    
    @Schema(description = "충전 금액", example = "50000", required = true)
    @NotNull(message = "충전 금액은 필수입니다.")
    @Positive(message = "충전 금액은 양수여야 합니다.")
    private BigDecimal amount;
    
    @Schema(description = "충전 소스 유형", example = "PERSONAL_ACCOUNT", allowableValues = {"PERSONAL_ACCOUNT", "GROUP_ACCOUNT"})
    private String sourceType;
    
    @Schema(description = "충전 소스 ID", example = "110-123-456789")
    private String sourceId;
    
    @Schema(description = "충전 설명", example = "월간 용돈 충전")
    private String description;
}










