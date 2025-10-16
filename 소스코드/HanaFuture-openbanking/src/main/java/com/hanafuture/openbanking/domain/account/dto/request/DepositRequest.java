package com.hanafuture.openbanking.domain.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "입금이체 요청")
public class DepositRequest {
    
    @Schema(description = "성명 확인 옵션", example = "on")
    private String nameCheckOption;
    
    @Schema(description = "요청일시", example = "20250704123045")
    private String tranDtime;
    
    @Schema(description = "거래번호", example = "00001")
    private String tranNo;
    
    @Schema(description = "거래고유번호", example = "BANKTRAN123456789012")
    private String bankTranId;
    
    @Schema(description = "은행 표준코드", example = "081")
    private String bankCodeStd;
    
    @Schema(description = "송금인 계좌번호", example = "110-555-888999")
    private String accountNum;
    
    @Schema(description = "계좌주명", example = "정민준")
    private String accountHolderName;
    
    @Schema(description = "입금통장메모", example = "입금내역")
    private String printContent;
    
    @Schema(description = "거래금액", example = "10000")
    private String tranAmt;
    
    @Schema(description = "요청고객번호", example = "REQCLIENT0001")
    private String reqClientNum;
    
    @Schema(description = "수취인 계좌번호", example = "정민준")
    private String reqClientAccountNum;
    
    @Schema(description = "이체목적", example = "TR")
    private String transferPurpose;
} 