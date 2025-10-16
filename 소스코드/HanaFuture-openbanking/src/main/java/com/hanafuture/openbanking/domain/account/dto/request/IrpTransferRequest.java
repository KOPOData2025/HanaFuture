package com.hanafuture.openbanking.domain.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "IRP 실물이전 요청")
public class IrpTransferRequest {
    
    @Schema(description = "사용자 식별자", example = "C408651451")
    private String userSeqNo;
    
    @Schema(description = "거래고유번호", example = "123456")
    private String bankTranId;
    
    @Schema(description = "출금계좌번호 (신한은행)", example = "110555888999")
    private String wdAccountNum;
    
    @Schema(description = "입금계좌번호 (하나은행)", example = "2345678901234567")
    private String rsvAccountNum;
    
    @Schema(description = "출금은행코드 (신한은행)", example = "088")
    private String wdBankCodeStd;
    
    @Schema(description = "입금은행코드 (하나은행)", example = "081")
    private String rsvBankCodeStd;
    
    @Schema(description = "요청일시", example = "20250704123045")
    private String tranDtime;
    
    @Schema(description = "요청고객명", example = "정민준")
    private String reqClientName;
} 