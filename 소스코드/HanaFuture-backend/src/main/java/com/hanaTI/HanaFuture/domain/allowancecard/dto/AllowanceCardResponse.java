package com.hanaTI.HanaFuture.domain.allowancecard.dto;

import com.hanaTI.HanaFuture.domain.allowancecard.entity.AllowanceCardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "아이카드 정보 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceCardResponse {
    
    @Schema(description = "카드 ID", example = "1")
    private Long id;
    
    @Schema(description = "카드 번호", example = "1234567890123456")
    private String cardNumber;
    
    @Schema(description = "카드 이름", example = "철수의 용돈카드")
    private String cardName;
    
    @Schema(description = "자녀 ID", example = "1")
    private Long childId;
    
    @Schema(description = "자녀 이름", example = "김철수")
    private String childName;
    
    @Schema(description = "부모 사용자 ID", example = "1")
    private Long parentUserId;
    
    @Schema(description = "현재 잔액", example = "25000")
    private BigDecimal currentBalance;
    
    @Schema(description = "월 용돈 한도", example = "100000")
    private BigDecimal monthlyLimit;
    
    @Schema(description = "일 사용 한도", example = "50000")
    private BigDecimal dailyLimit;
    
    @Schema(description = "오늘 사용 금액", example = "15000")
    private BigDecimal todayUsage;
    
    @Schema(description = "이번 달 사용 금액", example = "75000")
    private BigDecimal monthlyUsage;
    
    @Schema(description = "카드 상태", example = "ACTIVE")
    private AllowanceCardStatus status;
    
    @Schema(description = "자동 충전 활성화", example = "true")
    private Boolean autoChargeEnabled;
    
    @Schema(description = "자동 충전 금액", example = "50000")
    private BigDecimal autoChargeAmount;
    
    @Schema(description = "자동 충전 임계값", example = "10000")
    private BigDecimal autoChargeThreshold;
    
    @Schema(description = "충전 소스 유형", example = "PERSONAL_ACCOUNT")
    private String chargeSourceType;
    
    @Schema(description = "충전 소스 ID", example = "110-123-456789")
    private String chargeSourceId;
    
    @Schema(description = "사용 제한", example = "편의점,온라인쇼핑")
    private String usageRestrictions;
    
    @Schema(description = "알림 활성화", example = "true")
    private Boolean notificationEnabled;
    
    @Schema(description = "잔액 부족 알림 임계값", example = "5000")
    private BigDecimal lowBalanceAlert;
    
    @Schema(description = "생성일", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일", example = "2024-01-20T14:20:00")
    private LocalDateTime updatedAt;
}










