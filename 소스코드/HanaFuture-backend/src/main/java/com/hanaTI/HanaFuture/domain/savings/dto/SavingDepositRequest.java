package com.hanaTI.HanaFuture.domain.savings.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "적금 납입 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingDepositRequest {
    
    @Schema(description = "사용자 ID", example = "1", required = true)
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    @Schema(description = "납입 금액", example = "500000", required = true)
    @NotNull(message = "납입 금액은 필수입니다.")
    @Positive(message = "납입 금액은 0보다 커야 합니다.")
    private Long amount;
    
    @Schema(description = "납입 설명", example = "9월 정기 납입")
    private String description;
    
    @Schema(description = "출금 계좌 유형", example = "PERSONAL_ACCOUNT", allowableValues = {"PERSONAL_ACCOUNT", "GROUP_ACCOUNT"})
    private String sourceType;
    
    @Schema(description = "출금 계좌 ID", example = "110-123-456789")
    private String sourceAccountId;
}
