package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalIrpDepositRequest {
    
    private String userCI;
    private String tranDtime;
    private String bankTranId;
    private String rsvBankCodeStd;
    private String wdAccountNum;
    private String rsvAccountNum;
    private String depositAmt;
    private String reqClientName;
} 