package com.hanaTI.HanaFuture.domain.account.dto;

import com.hanaTI.HanaFuture.domain.account.entity.Account;
import com.hanaTI.HanaFuture.domain.account.entity.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "계좌 정보 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    
    @Schema(description = "계좌 ID", example = "1")
    private Long id;
    
    @Schema(description = "계좌번호", example = "110-123-456789")
    private String accountNumber;
    
    @Schema(description = "계좌명", example = "가족 공동계좌")
    private String accountName;
    
    @Schema(description = "계좌 유형")
    private AccountType accountType;
    
    @Schema(description = "계좌 유형 표시명", example = "가족공동계좌")
    private String accountTypeDisplayName;
    
    @Schema(description = "잔액", example = "15420000.00")
    private BigDecimal balance;
    
    @Schema(description = "은행명", example = "하나은행")
    private String bankName;
    
    @Schema(description = "계좌 설명", example = "가족이 함께 사용하는 계좌입니다")
    private String description;
    
    @Schema(description = "활성 상태", example = "true")
    private Boolean isActive;
    
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
    
    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .accountTypeDisplayName(account.getAccountType().getDisplayName())
                .balance(account.getBalance())
                .bankName(account.getBankName())
                .description(account.getDescription())
                .isActive(account.getIsActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

