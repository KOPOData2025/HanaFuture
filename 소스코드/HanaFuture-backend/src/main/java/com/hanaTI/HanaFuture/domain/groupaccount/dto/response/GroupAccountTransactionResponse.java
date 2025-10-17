package com.hanaTI.HanaFuture.domain.groupaccount.dto.response;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAccountTransactionResponse {
    
    private Long id;
    private GroupAccountTransaction.TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String sourceAccountNumber;
    private String sourceBankName;
    private String targetAccountNumber;
    private String targetBankName;
    private Long userId;
    private String userName;
    private LocalDateTime transactionDate;
    
    public static GroupAccountTransactionResponse from(GroupAccountTransaction transaction) {
        return GroupAccountTransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .sourceAccountNumber(transaction.getSourceAccountNumber())
                .sourceBankName(transaction.getSourceBankName())
                .targetAccountNumber(transaction.getTargetAccountNumber())
                .targetBankName(transaction.getTargetBankName())
                .userId(transaction.getUser() != null ? transaction.getUser().getId() : null)
                .userName(transaction.getUser() != null ? transaction.getUser().getName() : null)
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}

