package com.hanafuture.openbanking.domain.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "출금이체 요청")
public class WithdrawRequest {
    
    @Schema(description = "거래고유번호", example = "BANKTRAN123456789012")
    private String bankTranId;
    
    @Schema(description = "약정 계좌 타입", example = "N")
    private String cntrAccountType;
    
    @Schema(description = "약정 계좌번호", example = "1234567890123456")
    private String cntrAccountNum;
    
    @Schema(description = "입금통장메모", example = "쇼핑몰환불")
    private String dpsPrintContent;
    
    @Schema(description = "출금은행 표준코드", example = "081")
    private String wdBankCodeStd;
    
    @Schema(description = "출금계좌번호", example = "1101230000678")
    private String wdAccountNum;
    
    @Schema(description = "거래금액", example = "500000")
    private String tranAmt;
    
    @Schema(description = "사용자일련번호", example = "U111111111")
    private String userSeqNo;
    
    @Schema(description = "요청일시", example = "20250704123045")
    private String tranDtime;
    
    @Schema(description = "요청고객명", example = "홍길동")
    private String reqClientName;
    
    @Schema(description = "요청고객번호", example = "HONGGILDONG1234")
    private String reqClientNum;
    
    @Schema(description = "이체목적", example = "TR")
    private String transferPurpose;
    
    @Schema(description = "받는분성명", example = "이철우")
    private String recvClientName;
    
    @Schema(description = "받는분은행코드", example = "088")
    private String recvClientBankCode;
    
    @Schema(description = "받는분계좌번호", example = "232000067812")
    private String recvClientAccountNum;
} 