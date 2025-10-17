package com.hanaTI.HanaFuture.domain.asset.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "connected_assets")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "masked_account_number", length = 50)
    private String maskedAccountNumber;

    @Column(name = "bank_code", nullable = false, length = 10)
    private String bankCode;

    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    @Column(name = "account_name", length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AssetAccountType accountType;

    @Column(name = "balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "KRW";

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false)
    @Builder.Default
    private AssetConnectionStatus connectionStatus = AssetConnectionStatus.CONNECTED;

    @Column(name = "consent_id", length = 100)
    private String consentId;

    @Column(name = "consent_expires_at")
    private LocalDateTime consentExpiresAt;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "sync_enabled")
    @Builder.Default
    private Boolean syncEnabled = true;

    @Column(name = "notification_enabled")
    @Builder.Default
    private Boolean notificationEnabled = true;

    @Column(name = "alias", length = 100)
    private String alias;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    // 투자 관련 필드들
    @Column(name = "investment_type", length = 50)
    private String investmentType;

    @Column(name = "purchase_amount", precision = 15, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(name = "current_value", precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "profit_loss", precision = 15, scale = 2)
    private BigDecimal profitLoss;

    @Column(name = "profit_loss_rate", precision = 5, scale = 2)
    private BigDecimal profitLossRate;

    // 대출 관련 필드들
    @Column(name = "loan_amount", precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "remaining_amount", precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "loan_start_date")
    private LocalDateTime loanStartDate;

    @Column(name = "loan_end_date")
    private LocalDateTime loanEndDate;

    // 카드 관련 필드들
    @Column(name = "card_type", length = 50)
    private String cardType;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "used_amount", precision = 15, scale = 2)
    private BigDecimal usedAmount;

    @Column(name = "available_amount", precision = 15, scale = 2)
    private BigDecimal availableAmount;

    @Column(name = "payment_due_date")
    private LocalDateTime paymentDueDate;

    @OneToMany(mappedBy = "connectedAsset", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AssetTransaction> transactions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비즈니스 메서드들
    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
        this.lastSyncedAt = LocalDateTime.now();
    }

    public void updateConnectionStatus(AssetConnectionStatus status) {
        this.connectionStatus = status;
    }

    public void updateConsent(String consentId, LocalDateTime expiresAt) {
        this.consentId = consentId;
        this.consentExpiresAt = expiresAt;
    }

    public void updateInvestmentInfo(BigDecimal currentValue, BigDecimal profitLoss, BigDecimal profitLossRate) {
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossRate = profitLossRate;
        this.lastSyncedAt = LocalDateTime.now();
    }

    public void updateLoanInfo(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
        this.lastSyncedAt = LocalDateTime.now();
    }

    public void updateCardInfo(BigDecimal usedAmount, BigDecimal availableAmount, LocalDateTime paymentDueDate) {
        this.usedAmount = usedAmount;
        this.availableAmount = availableAmount;
        this.paymentDueDate = paymentDueDate;
        this.lastSyncedAt = LocalDateTime.now();
    }

    public void updateSettings(Boolean syncEnabled, Boolean notificationEnabled, String alias) {
        this.syncEnabled = syncEnabled;
        this.notificationEnabled = notificationEnabled;
        this.alias = alias;
    }

    public void setPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getDisplayName() {
        if (alias != null && !alias.trim().isEmpty()) {
            return alias;
        }
        return bankName + " " + getMaskedAccountNumber();
    }

    public String getMaskedAccountNumber() {
        if (maskedAccountNumber != null) {
            return maskedAccountNumber;
        }
        if (accountNumber != null && accountNumber.length() > 4) {
            return accountNumber.substring(0, 4) + "****" + 
                   accountNumber.substring(accountNumber.length() - 4);
        }
        return accountNumber;
    }

    public boolean isInvestmentAccount() {
        return accountType == AssetAccountType.INVESTMENT || 
               accountType == AssetAccountType.STOCK || 
               accountType == AssetAccountType.FUND;
    }

    public boolean isLoanAccount() {
        return accountType == AssetAccountType.LOAN || 
               accountType == AssetAccountType.MORTGAGE;
    }

    public boolean isCardAccount() {
        return accountType == AssetAccountType.CREDIT_CARD || 
               accountType == AssetAccountType.DEBIT_CARD;
    }

    public boolean needsSync() {
        if (!syncEnabled || connectionStatus != AssetConnectionStatus.CONNECTED) {
            return false;
        }
        
        if (lastSyncedAt == null) {
            return true;
        }
        
        // 1시간 이상 동기화되지 않았으면 동기화 필요
        return lastSyncedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean isConsentExpired() {
        return consentExpiresAt != null && consentExpiresAt.isBefore(LocalDateTime.now());
    }
}
