package com.hana.hanabank.domain.account.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hanabank_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_num", nullable = false, unique = true, length = 20)
    private String accountNum;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "bank_code_std", nullable = false, length = 3)
    private String bankCodeStd;

    @Column(name = "activity_type", nullable = false, length = 1)
    private String activityType;

    @Column(name = "account_type", nullable = false, length = 1)
    private String accountType;

    @Column(name = "account_num_masked", nullable = false, length = 20)
    private String accountNumMasked;

    @Column(name = "account_seq", nullable = false, length = 2)
    private String accountSeq;

    @Column(name = "account_local_code", nullable = false, length = 7)
    private String accountLocalCode;

    @Column(name = "account_issue_date", nullable = false, length = 8)
    private String accountIssueDate;

    @Column(name = "maturity_date", length = 8)
    private String maturityDate;

    @Column(name = "last_tran_date", nullable = false, length = 8)
    private String lastTranDate;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_sub_name", length = 10)
    private String productSubName;

    @Column(name = "dormancy_yn", nullable = false, length = 1)
    private String dormancyYn;

    @Column(name = "balance_amt", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAmt;

    @Column(name = "deposit_amt", precision = 15, scale = 2)
    private BigDecimal depositAmt;

    @Column(name = "balance_calc_basis_1", length = 1)
    private String balanceCalcBasis1;

    @Column(name = "balance_calc_basis_2", length = 1)
    private String balanceCalcBasis2;

    @Column(name = "investment_linked_yn", length = 1)
    private String investmentLinkedYn;

    @Column(name = "bank_linked_yn", length = 1)
    private String bankLinkedYn;

    @Column(name = "balance_after_cancel_yn", length = 1)
    private String balanceAfterCancelYn;

    @Column(name = "savings_bank_code", length = 3)
    private String savingsBankCode;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 비즈니스 메서드
    public void updateBalance(BigDecimal newBalance) {
        this.balanceAmt = newBalance;
    }
    
    public boolean isActive() {
        return "1".equals(this.activityType);
    }
    
    public boolean isDormant() {
        return "Y".equals(this.dormancyYn);
    }
}
