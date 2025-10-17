package com.hanaTI.HanaFuture.domain.banking.mock.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 하나은행 Mock 계좌 엔티티
 */
@Entity
@Table(name = "hanabank_mock_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class HanaBankMockAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_ci", nullable = false)
    private String userCi;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_num", nullable = false)
    private String userNum;

    @Column(name = "account_num", nullable = false, unique = true)
    private String accountNum;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "balance_amt", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balanceAmt = BigDecimal.ZERO;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "bank_code")
    @Builder.Default
    private String bankCode = "081";

    @Column(name = "bank_name")
    @Builder.Default
    private String bankName = "하나은행";

    @Column(name = "fintech_use_num")
    private String fintechUseNum;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 잔액 업데이트
    public void updateBalance(BigDecimal newBalance) {
        this.balanceAmt = newBalance;
    }
}




