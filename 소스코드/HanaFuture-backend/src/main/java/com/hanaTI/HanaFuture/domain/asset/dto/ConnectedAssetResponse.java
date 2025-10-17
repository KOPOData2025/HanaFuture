package com.hanaTI.HanaFuture.domain.asset.dto;

import com.hanaTI.HanaFuture.domain.asset.entity.*;
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
public class ConnectedAssetResponse {

    private Long id;
    private String accountNumber;
    private String maskedAccountNumber;
    private String bankCode;
    private String bankName;
    private String accountName;
    private AssetAccountType accountType;
    private String accountTypeDescription;
    private BigDecimal balance;
    private String currency;
    private AssetConnectionStatus connectionStatus;
    private String connectionStatusDescription;
    private String alias;
    private String displayName;
    private Boolean isPrimary;
    private Boolean syncEnabled;
    private Boolean notificationEnabled;
    private LocalDateTime lastSyncedAt;
    private LocalDateTime consentExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 투자 관련 필드들
    private String investmentType;
    private BigDecimal purchaseAmount;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossRate;

    // 대출 관련 필드들
    private BigDecimal loanAmount;
    private BigDecimal remainingAmount;
    private BigDecimal interestRate;
    private LocalDateTime loanStartDate;
    private LocalDateTime loanEndDate;

    // 카드 관련 필드들
    private String cardType;
    private BigDecimal creditLimit;
    private BigDecimal usedAmount;
    private BigDecimal availableAmount;
    private LocalDateTime paymentDueDate;

    // 계산된 필드들
    private Boolean needsSync;
    private Boolean isConsentExpired;

    // 최근 거래내역 (요약)
    private List<AssetTransactionSummary> recentTransactions;

    @Data
    @Builder
    public static class AssetTransactionSummary {
        private Long transactionId;
        private String externalTransactionId;
        private AssetTransactionType transactionType;
        private String transactionTypeDescription;
        private BigDecimal amount;
        private String description;
        private String counterparty;
        private AssetTransactionStatus status;
        private String statusDescription;
        private LocalDateTime transactionDate;
        private String category;
        private String memo;
    }

    // Entity에서 DTO로 변환하는 정적 메서드
    public static ConnectedAssetResponse from(ConnectedAsset asset) {
        ConnectedAssetResponseBuilder builder = ConnectedAssetResponse.builder()
                .id(asset.getId())
                .accountNumber(asset.getAccountNumber())
                .maskedAccountNumber(asset.getMaskedAccountNumber())
                .bankCode(asset.getBankCode())
                .bankName(asset.getBankName())
                .accountName(asset.getAccountName())
                .accountType(asset.getAccountType())
                .accountTypeDescription(asset.getAccountType().getDescription())
                .balance(asset.getBalance())
                .currency(asset.getCurrency())
                .connectionStatus(asset.getConnectionStatus())
                .connectionStatusDescription(asset.getConnectionStatus().getDescription())
                .alias(asset.getAlias())
                .displayName(asset.getDisplayName())
                .isPrimary(asset.getIsPrimary())
                .syncEnabled(asset.getSyncEnabled())
                .notificationEnabled(asset.getNotificationEnabled())
                .lastSyncedAt(asset.getLastSyncedAt())
                .consentExpiresAt(asset.getConsentExpiresAt())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .needsSync(asset.needsSync())
                .isConsentExpired(asset.isConsentExpired());

        // 투자 계좌인 경우
        if (asset.isInvestmentAccount()) {
            builder.investmentType(asset.getInvestmentType())
                   .purchaseAmount(asset.getPurchaseAmount())
                   .currentValue(asset.getCurrentValue())
                   .profitLoss(asset.getProfitLoss())
                   .profitLossRate(asset.getProfitLossRate());
        }

        // 대출 계좌인 경우
        if (asset.isLoanAccount()) {
            builder.loanAmount(asset.getLoanAmount())
                   .remainingAmount(asset.getRemainingAmount())
                   .interestRate(asset.getInterestRate())
                   .loanStartDate(asset.getLoanStartDate())
                   .loanEndDate(asset.getLoanEndDate());
        }

        // 카드 계좌인 경우
        if (asset.isCardAccount()) {
            builder.cardType(asset.getCardType())
                   .creditLimit(asset.getCreditLimit())
                   .usedAmount(asset.getUsedAmount())
                   .availableAmount(asset.getAvailableAmount())
                   .paymentDueDate(asset.getPaymentDueDate());
        }

        return builder.build();
    }
}
