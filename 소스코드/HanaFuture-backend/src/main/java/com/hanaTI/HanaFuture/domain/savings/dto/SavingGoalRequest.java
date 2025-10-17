package com.hanaTI.HanaFuture.domain.savings.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "적금 목표 생성 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalRequest {
    
    @Schema(description = "사용자 ID", example = "1", required = true)
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    @Schema(description = "적금 목표명", example = "우리 아기 출산 준비", required = true)
    @NotBlank(message = "적금 목표명은 필수입니다.")
    private String goalName;
    
    @Schema(description = "목표 설명", example = "첫째 아기 출산 관련 비용 마련")
    private String description;
    
    @Schema(description = "목표 금액", example = "5000000", required = true)
    @NotNull(message = "목표 금액은 필수입니다.")
    @Positive(message = "목표 금액은 0보다 커야 합니다.")
    private Long targetAmount;
    
    @Schema(description = "월 납입 목표", example = "500000", required = true)
    @NotNull(message = "월 납입 목표는 필수입니다.")
    @Positive(message = "월 납입 목표는 0보다 커야 합니다.")
    private Long monthlyTarget;
    
    @Schema(description = "적금 카테고리", example = "BIRTH_PREPARATION")
    private String category;
    
    @Schema(description = "시작일", example = "2025-01-01", required = true)
    @NotBlank(message = "시작일은 필수입니다.")
    private String startDate;
    
    @Schema(description = "종료일", example = "2025-12-31", required = true)
    @NotBlank(message = "종료일은 필수입니다.")
    private String endDate;
    
    @Schema(description = "이자율", example = "4.1")
    @Builder.Default
    private Double interestRate = 3.5;
    
    @Schema(description = "출금 계좌 유형", example = "PERSONAL_ACCOUNT", allowableValues = {"PERSONAL_ACCOUNT", "GROUP_ACCOUNT"})
    @NotBlank(message = "출금 계좌 유형은 필수입니다.")
    private String sourceType;
    
    @Schema(description = "출금 계좌 ID", example = "110-123-456789")
    @NotBlank(message = "출금 계좌 ID는 필수입니다.")
    private String sourceAccountId;
    
    @Schema(description = "매월 자동이체일", example = "25")
    @Builder.Default
    private Integer autoTransferDay = 25;
    
    @Schema(description = "공동 적금 여부", example = "true")
    @Builder.Default
    private Boolean isJointSavings = true;
    
    @Schema(description = "참여자 이메일 목록")
    private List<String> participantEmails;
}
