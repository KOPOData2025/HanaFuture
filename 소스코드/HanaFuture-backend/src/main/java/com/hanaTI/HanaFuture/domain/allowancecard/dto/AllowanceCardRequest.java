package com.hanaTI.HanaFuture.domain.allowancecard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "아이카드 생성 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceCardRequest {
    
    @Schema(description = "카드 이름", example = "철수의 용돈카드", required = true)
    @NotBlank(message = "카드 이름은 필수입니다.")
    private String cardName;
    
    @Schema(description = "자녀 ID", example = "1", required = true)
    @NotNull(message = "자녀 ID는 필수입니다.")
    private Long childId;
    
    @Schema(description = "부모 사용자 ID", example = "1", required = true)
    @NotNull(message = "부모 사용자 ID는 필수입니다.")
    private Long parentUserId;
    
    @Schema(description = "월 용돈 한도", example = "100000")
    private BigDecimal monthlyLimit;
    
    @Schema(description = "일 사용 한도", example = "50000")
    private BigDecimal dailyLimit;
    
    @Schema(description = "자동 충전 활성화", example = "true")
    @Builder.Default
    private Boolean autoChargeEnabled = false;
    
    @Schema(description = "자동 충전 금액", example = "50000")
    private BigDecimal autoChargeAmount;
    
    @Schema(description = "자동 충전 임계값", example = "10000")
    private BigDecimal autoChargeThreshold;
    
    @Schema(description = "충전 소스 유형", example = "PERSONAL_ACCOUNT", allowableValues = {"PERSONAL_ACCOUNT", "GROUP_ACCOUNT"})
    private String chargeSourceType;
    
    @Schema(description = "충전 소스 ID", example = "110-123-456789")
    private String chargeSourceId;
    
    @Schema(description = "사용 제한", example = "편의점,온라인쇼핑")
    private String usageRestrictions;
    
    @Schema(description = "알림 활성화", example = "true")
    @Builder.Default
    private Boolean notificationEnabled = true;
    
    @Schema(description = "잔액 부족 알림 임계값", example = "5000")
    private BigDecimal lowBalanceAlert;
}
