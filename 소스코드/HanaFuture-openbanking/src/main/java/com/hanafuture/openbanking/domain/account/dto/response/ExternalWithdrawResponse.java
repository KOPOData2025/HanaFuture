package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalWithdrawResponse {
    
    private boolean success;
    private String message;
    private ExternalWithdrawData data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalWithdrawData {
        
        private String bankTranId;
        private String dpsPrintContent;
        private String accountNum;
        private String accountAlias;
        private String bankCodeStd;
        private String bankCodeSub;
        private String bankName;
        private String accountNumMasked;
        private String printContent;
        private String accountHolderName;
        private Long tranAmt;
        private Long wdLimitRemainAmt;
    }
} 