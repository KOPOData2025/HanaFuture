package com.hanafuture.openbanking.domain.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalTransactionResponse {
    
    private boolean success;
    private String message;
    private ExternalTransactionData data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalTransactionData {
        
        private String bankName;
        private String accountNum;
        private String balanceAmt;
        private Integer pageRecordCnt;
        private String nextPageYn;
        private String beforInquiryTraceInfo;
        private List<ExternalTransactionInfo> resList;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalTransactionInfo {
        
        private String tranDate;
        private String tranTime;
        private String inoutType;
        private String tranType;
        private String printedContent;
        private String tranAmt;
        private String afterBalanceAmt;
        private String branchName;
    }
} 