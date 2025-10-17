package com.hanaTI.HanaFuture.domain.asset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_transactions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_asset_id", nullable = false)
    private ConnectedAsset connectedAsset;

    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private AssetTransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "counterparty", length = 100)
    private String counterparty;

    @Column(name = "counterparty_account", length = 50)
    private String counterpartyAccount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "KRW";

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "memo", length = 200)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private AssetTransactionStatus status = AssetTransactionStatus.COMPLETED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 비즈니스 메서드들
    public boolean isDebit() {
        return transactionType == AssetTransactionType.WITHDRAWAL ||
               transactionType == AssetTransactionType.TRANSFER_OUT ||
               transactionType == AssetTransactionType.PAYMENT ||
               transactionType == AssetTransactionType.FEE;
    }

    public boolean isCredit() {
        return transactionType == AssetTransactionType.DEPOSIT ||
               transactionType == AssetTransactionType.TRANSFER_IN ||
               transactionType == AssetTransactionType.INTEREST ||
               transactionType == AssetTransactionType.REFUND;
    }

    public void updateStatus(AssetTransactionStatus status) {
        this.status = status;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateCategory(String category) {
        this.category = category;
    }
}
