package com.hanafuture.openbanking.domain.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "입금이체 응답")
public class DepositResponse {
    
    @Schema(description = "거래번호", example = "4")
    private String tranNo;
    
    @Schema(description = "거래고유번호", example = "F123456789U4BC34242Z")
    private String bankTranId;
    
    @Schema(description = "거래일자", example = "20250705")
    private String bankTranDate;
    
    @Schema(description = "거래은행코드", example = "081")
    private String bankCodeTran;
    
    @Schema(description = "은행응답코드", example = "000")
    private String bankRspCode;
    
    @Schema(description = "은행응답메시지", example = "")
    private String bankRspMessage;
    
    @Schema(description = "은행명", example = "하나은행")
    private String bankName;
    
    @Schema(description = "계좌번호", example = "1234567890123456")
    private String accountNum;
    
    @Schema(description = "마스킹된 계좌번호", example = "1234-56**-****-3456")
    private String accountNumMasked;
    
    @Schema(description = "적요", example = "월세결제")
    private String printContent;
    
    @Schema(description = "계좌주명", example = "이영희")
    private String accountHolderName;
    
    @Schema(description = "거래금액", example = "300000")
    private Long tranAmt;
    
    @Schema(description = "출금거래고유번호", example = "F123456789U4BC34242Z")
    private String withdrawBankTranId;
} 