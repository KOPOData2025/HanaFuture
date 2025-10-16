package com.hanafuture.openbanking.domain.transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_transaction_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "TRANSACTION_SEQ", allocationSize = 1)
    @Column(name = "api_tran_id")
    private Long apiTranId;
    
    @Column(name = "fintech_use_num", length = 50)
    private String fintechUseNum;
    
    @Column(name = "tran_amt", precision = 15, scale = 2)
    private BigDecimal tranAmt;
    
    @Column(name = "wd_bank_code_std", length = 3)
    private String wdBankCodeStd;
    
    @Column(name = "wd_account_num_masked", length = 50)
    private String wdAccountNumMasked;
    
    @Column(name = "wd_print_content", length = 100)
    private String wdPrintContent;
    
    @Column(name = "req_client_name", length = 50)
    private String reqClientName;
    
    @Column(name = "req_client_fintech_use_num", length = 50)
    private String reqClientFintechUseNum;
    
    @Column(name = "req_client_num", length = 50)
    private String reqClientNum;
    
    @Column(name = "transfer_purpose", length = 2)
    private String transferPurpose;
    
    @Column(name = "api_type", length = 20)
    private String apiType;
    
    @Column(name = "response_code", length = 10)
    private String responseCode;
    
    @Column(name = "response_message", length = 200)
    private String responseMessage;
    
    @Column(name = "bank_tran_id", length = 50)
    private String bankTranId;
    
    @Column(name = "bank_tran_date", length = 8)
    private String bankTranDate;
    
    @Column(name = "bank_code_tran", length = 3)
    private String bankCodeTran;
    
    @Column(name = "bank_rsp_code", length = 4)
    private String bankRspCode;
    
    @Column(name = "bank_rsp_message", length = 200)
    private String bankRspMessage;
    
    @Column(name = "fintech_group_no", length = 5)
    private String fintechGroupNo;
    
    @Column(name = "cms_no", length = 15)
    private String cmsNo;
    
    @Column(name = "savings_bank_name", length = 50)
    private String savingsBankName;
    
    @Column(name = "req_cnt", length = 10)
    private String reqCnt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
} 