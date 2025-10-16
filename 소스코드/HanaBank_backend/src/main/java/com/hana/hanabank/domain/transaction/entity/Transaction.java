package com.hana.hanabank.domain.transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hanabank_transaction")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @Column(name = "tran_id", nullable = false, length = 50)
    private String tranId;
    
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    
    @Column(name = "tran_date", nullable = false)
    private LocalDate tranDate;
    
    @Column(name = "tran_time", nullable = false, length = 6)
    private String tranTime;
    
    @Column(name = "inout_type", nullable = false, length = 8)
    private String inoutType;
    
    @Column(name = "tran_type", length = 10)
    private String tranType;
    
    @Column(name = "print_content", length = 20)
    private String printContent;
    
    @Column(name = "tran_amt", nullable = false, precision = 12, scale = 0)
    private BigDecimal tranAmt;
    
    @Column(name = "after_balance_amt", precision = 13, scale = 0)
    private BigDecimal afterBalanceAmt;
    
    @Column(name = "branch_name", length = 20)
    private String branchName;
    
    @Column(name = "wd_bank_code_std", nullable = false, length = 3)
    private String wdBankCodeStd;
    
    @Column(name = "wd_account_num", nullable = false, length = 16)
    private String wdAccountNum;
    
    @Column(name = "req_client_name", nullable = false, length = 20)
    private String reqClientName;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 비즈니스 메서드
    public boolean isDeposit() {
        return "입금".equals(this.inoutType);
    }
    
    public boolean isWithdraw() {
        return "출금".equals(this.inoutType);
    }
}
