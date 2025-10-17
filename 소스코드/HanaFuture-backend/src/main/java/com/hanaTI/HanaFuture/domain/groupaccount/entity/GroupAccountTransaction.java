package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_account_transactions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupAccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_account_transaction_seq")
    @SequenceGenerator(name = "group_account_transaction_seq", sequenceName = "group_account_transaction_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_account_id", nullable = false)
    private GroupAccount groupAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private com.hanaTI.HanaFuture.domain.user.entity.User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "source_account_number", length = 50)
    private String sourceAccountNumber;

    @Column(name = "source_bank_name", length = 50)
    private String sourceBankName;

    @Column(name = "target_account_number", length = 50)
    private String targetAccountNumber;

    @Column(name = "target_bank_name", length = 50)
    private String targetBankName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TransactionType {
        DEPOSIT("입금"),
        WITHDRAWAL("출금"),
        TRANSFER_IN("이체입금"),
        TRANSFER_OUT("이체출금"),
        AUTO_TRANSFER("자동이체"),
        INTEREST("이자"),
        FEE("수수료");

        private final String description;

        TransactionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum TransactionStatus {
        PENDING("처리중"),
        COMPLETED("완료"),
        FAILED("실패"),
        CANCELLED("취소");

        private final String description;

        TransactionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 정적 팩토리 메서드
    public static GroupAccountTransaction createDeposit(
            GroupAccount groupAccount,
            com.hanaTI.HanaFuture.domain.user.entity.User user,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String sourceAccountNumber,
            String sourceBankName,
            String description) {
        
        return GroupAccountTransaction.builder()
                .groupAccount(groupAccount)
                .user(user)
                .transactionType(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .sourceAccountNumber(sourceAccountNumber)
                .sourceBankName(sourceBankName)
                .targetAccountNumber(groupAccount.getGroupAccountNumber())
                .targetBankName("하나은행")
                .status(TransactionStatus.COMPLETED)
                .transactionDate(LocalDateTime.now())
                .referenceNumber(generateReferenceNumber())
                .build();
    }

    public static GroupAccountTransaction createWithdrawal(
            GroupAccount groupAccount,
            com.hanaTI.HanaFuture.domain.user.entity.User user,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String targetAccountNumber,
            String targetBankName,
            String description) {
        
        return GroupAccountTransaction.builder()
                .groupAccount(groupAccount)
                .user(user)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .sourceAccountNumber(groupAccount.getGroupAccountNumber())
                .sourceBankName("하나은행")
                .targetAccountNumber(targetAccountNumber)
                .targetBankName(targetBankName)
                .status(TransactionStatus.COMPLETED)
                .transactionDate(LocalDateTime.now())
                .referenceNumber(generateReferenceNumber())
                .build();
    }

    private static String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}