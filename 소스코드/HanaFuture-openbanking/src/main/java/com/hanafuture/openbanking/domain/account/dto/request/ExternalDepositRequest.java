package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalDepositRequest {
    
    private String tranDtime;
    
    private String tranNo;
    
    private String bankTranId;
    
    private String bankCodeStd;
    
    private String accountNum;
    
    private String accountHolderName;
    
    private String printContent;
    
    private Long tranAmt;
    
    private String reqClientNum;
    
    private String transferPurpose;
} 