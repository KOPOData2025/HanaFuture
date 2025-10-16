package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExternalBalanceResponse {
    private boolean success;
    private String message;
    private ExternalBalanceData data;

    @Getter
    @NoArgsConstructor
    public static class ExternalBalanceData {
        private String bankName;
        private String accountNum;
        private String balanceAmt;
        private String availableAmt;
        private String accountType;
        private String productName;
        private String accountIssueDate;
        private String maturityDate;
        private String lastTranDate;
    }
} 