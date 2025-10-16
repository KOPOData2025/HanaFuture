package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExternalAccountResponse {
    
    private boolean success;
    private String message;
    private Data data;
    
    @Getter
    @NoArgsConstructor
    public static class Data {
        private List<Account> accounts;
        private int totalCount;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Account {
        
        private int listNum;
        private String bankCodeStd;
        private String activityType;
        private String accountType;
        private String accountNum;
        private String accountNumMasked;
        private String accountSeq;
        private String accountLocalCode;
        private String accountIssueDate;
        private String maturityDate;
        private String lastTranDate;
        private String productName;
        private String productSubName;
        private String dormancyYn;
        private Long balanceAmt;
        private Long depositAmt;
        private String balanceCalcBasis1;
        private String balanceCalcBasis2;
        private String investmentLinkedYn;
        private String bankLinkedYn;
        private String balanceAfterCancelYn;
        private String savingsBankCode;
    }
} 