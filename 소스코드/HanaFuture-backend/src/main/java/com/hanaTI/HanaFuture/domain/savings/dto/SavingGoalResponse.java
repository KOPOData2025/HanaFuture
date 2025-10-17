package com.hanaTI.HanaFuture.domain.savings.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "적금 목표 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalResponse {
    
    @Schema(description = "적금 목표 ID")
    private Long id;
    
    @Schema(description = "적금 목표명")
    private String goalName;
    
    @Schema(description = "목표 설명")
    private String description;
    
    @Schema(description = "목표 금액")
    private BigDecimal targetAmount;
    
    @Schema(description = "현재 적립 금액")
    private BigDecimal currentAmount;
    
    @Schema(description = "월 납입 목표")
    private BigDecimal monthlyTarget;
    
    @Schema(description = "적금 카테고리")
    private String category;
    
    @Schema(description = "적금 계좌번호")
    private String savingsAccountNumber;
    
    @Schema(description = "이자율")
    private BigDecimal interestRate;
    
    @Schema(description = "적금 상태")
    private String status;
    
    @Schema(description = "출금 계좌 유형")
    private String sourceType;
    
    @Schema(description = "출금 계좌 ID")
    private String sourceAccountId;
    
    @Schema(description = "자동이체일")
    private Integer autoTransferDay;
    
    @Schema(description = "달성률")
    private Double achievementRate;
    
    @Schema(description = "참여자 목록")
    private List<SavingParticipantDto> participants;
    
    @Schema(description = "최근 납입 내역")
    private List<SavingDepositDto> recentDeposits;
    
    @Schema(description = "생성일")
    private LocalDateTime createdAt;
    
    // 내부 DTO 클래스들
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavingParticipantDto {
        private String name;
        private String email;
        private String role; // OWNER, CO_SAVER
        private BigDecimal personalAmount;
        private BigDecimal personalTarget;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavingDepositDto {
        private Long id;
        private String userName;
        private BigDecimal amount;
        private String description;
        private LocalDateTime depositDate;
        private BigDecimal balanceAfter;
    }
}
