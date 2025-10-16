package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalDepositResponse {
    
    private boolean success;
    private String message;
    private ExternalDepositData data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalDepositData {
        
        private String tranNo;
        private String bankTranId;
        private String bankTranDate;
        private String bankCodeTran;
        private String bankRspCode;
        private String bankRspMessage;
        private String bankName;
        private String accountNum;
        private String accountNumMasked;
        private String printContent;
        private String accountHolderName;
        private Long tranAmt;
        private String withdrawBankTranId;
    }
} 