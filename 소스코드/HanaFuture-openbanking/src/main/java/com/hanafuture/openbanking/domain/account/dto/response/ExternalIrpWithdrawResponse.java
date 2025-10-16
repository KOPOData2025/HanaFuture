package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalIrpWithdrawResponse {
    
    private boolean success;
    private String message;
    private ExternalIrpWithdrawData data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalIrpWithdrawData {
        
        private String wdAccountNum;
        private String maskedAccountNum;
        private String rsvAccountNum;
        private String wdBankCodeStd;
        private String accountType;
        private String irpType;
        private String irpProductName;
        private String maturityDate;
        private String withdrawAmt;
        private String paymentPrd;
        private String updatedAt;
    }
} 