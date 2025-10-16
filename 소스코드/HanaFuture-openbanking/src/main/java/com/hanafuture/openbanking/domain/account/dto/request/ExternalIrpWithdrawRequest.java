package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalIrpWithdrawRequest {
    
    private String userCi;
    private String bankTranId;
    private String wdBankCodeStd;
    private String wdAccountNum;
    private String rsvAccountNum;
    private String tranDtime;
    private String reqClientName;
} 