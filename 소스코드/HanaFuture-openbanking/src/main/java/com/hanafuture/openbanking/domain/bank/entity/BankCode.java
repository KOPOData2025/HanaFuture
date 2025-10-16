package com.hanafuture.openbanking.domain.bank.entity;

import com.hanafuture.openbanking.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_bank_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCode extends BaseEntity {
    
    @Id
    @Column(name = "bank_code_std", length = 3)
    private String bankCodeStd;
    
    @Column(name = "bank_name", length = 50)
    private String bankName;
    
    @Column(name = "bank_type", length = 10)
    private String bankType;

    @Column(name = "bank_endpoint", length = 255)
    private String bankEndpoint;
} 