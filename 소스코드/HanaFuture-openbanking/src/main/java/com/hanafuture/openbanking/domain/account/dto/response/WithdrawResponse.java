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
@Schema(description = "출금이체 응답")
public class WithdrawResponse {
    
    @Schema(description = "거래고유번호", example = "WD20250704004339fb0f941a")
    private String bankTranId;
    
    @Schema(description = "입금통장메모", example = "ATM출금")
    private String dpsPrintContent;
    
    @Schema(description = "계좌번호", example = "1234567890123456")
    private String accountNum;
    
    @Schema(description = "계좌별명", example = "하나 자유적금")
    private String accountAlias;
    
    @Schema(description = "은행 표준코드", example = "081")
    private String bankCodeStd;
    
    @Schema(description = "은행 세부코드", example = "0810001")
    private String bankCodeSub;
    
    @Schema(description = "은행명", example = "하나은행")
    private String bankName;
    
    @Schema(description = "마스킹된 계좌번호", example = "123456****3456")
    private String accountNumMasked;
    
    @Schema(description = "적요", example = "ATM출금")
    private String printContent;
    
    @Schema(description = "계좌주명", example = "정민준")
    private String accountHolderName;
    
    @Schema(description = "거래금액", example = "100000")
    private Long tranAmt;
    
    @Schema(description = "출금한도잔여금액", example = "1200000")
    private Long wdLimitRemainAmt;
} 