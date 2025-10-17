package com.hanaTI.HanaFuture.domain.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetSummaryResponse {

    // 전체 자산 요약
    private BigDecimal totalAssets;
    private BigDecimal totalInvestments;
    private BigDecimal totalLoans;
    private BigDecimal totalCardUsage;
    private BigDecimal netWorth;

    // 투자 수익률
    private BigDecimal totalInvestmentProfitLoss;
    private BigDecimal totalInvestmentProfitLossRate;

    // 연결 상태 요약
    private Long connectedAccountsCount;
    private Long disconnectedAccountsCount;
    private Long errorAccountsCount;

    // 최근 업데이트 정보
    private LocalDateTime lastSyncedAt;
    private Long accountsNeedingSync;

    // 계좌 유형별 통계
    private List<AccountTypeStat> accountTypeStats;

    // 은행별 통계
    private List<BankStat> bankStats;

    // 최근 거래내역
    private List<ConnectedAssetResponse.AssetTransactionSummary> recentTransactions;

    @Data
    @Builder
    public static class AccountTypeStat {
        private String accountType;
        private String accountTypeDescription;
        private Long count;
        private BigDecimal totalBalance;
    }

    @Data
    @Builder
    public static class BankStat {
        private String bankCode;
        private String bankName;
        private Long count;
        private BigDecimal totalBalance;
    }
}
