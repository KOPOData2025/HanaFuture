package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalIrpDepositResponse {
    
    private boolean success;
    private String message;
    private ExternalIrpDepositData data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalIrpDepositData {
        
        private String wdAccountNum;
        private String rsvAccountNum;
        private String maskedAccountNum;
        private String rsvBankCodeStd;
        private String accountType;
        private String irpType;
        private String irpProductName;
        private String maturityDate;
        private String depositAmt;
        private String balanceAmt;
        private String paymentPrd;
        private String updatedAt;
    }
} 