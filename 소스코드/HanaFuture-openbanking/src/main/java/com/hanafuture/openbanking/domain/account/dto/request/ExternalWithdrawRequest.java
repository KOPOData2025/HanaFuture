package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalWithdrawRequest {
    
    private String userCI;
    
    private String bankTranId;
    
    private String dpsPrintContent;
    
    private String wdBankCodeStd;
    
    private String wdAccountNum;
    
    private Long tranAmt;
    
    private String tranDtime;
    
    private String reqClientName;
} 