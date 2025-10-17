package com.hanaTI.HanaFuture.domain.asset.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAssetSettingsRequest {

    @Size(max = 100, message = "별칭은 100자를 초과할 수 없습니다")
    private String alias;

    private Boolean syncEnabled;

    private Boolean notificationEnabled;

    private Boolean isPrimary;

    // 카테고리 설정 (거래내역 자동 분류용)
    private Boolean enableAutoCategories;

    // 알림 설정 세부사항
    private Boolean notifyOnLargeTransactions;
    private Boolean notifyOnLowBalance;
    private Boolean notifyOnInvestmentChanges;
    private Boolean notifyOnLoanPayments;
    private Boolean notifyOnCardPayments;

    // 동기화 설정 세부사항
    private Integer syncFrequencyHours;
    private Boolean syncOnlyOnWifi;
}
