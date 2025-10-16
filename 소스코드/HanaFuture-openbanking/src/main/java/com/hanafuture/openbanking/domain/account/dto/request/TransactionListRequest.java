package com.hanafuture.openbanking.domain.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "거래내역조회 요청")
public class TransactionListRequest {
    
    @Schema(description = "거래고유번호", example = "BANKTRAN123456789012")
    private String bankTranId;
    
    @Schema(description = "표준코드", example = "081")
    private String bankCodeStd;
    
    @Schema(description = "계좌번호", example = "5678901234567890")
    private String accountNum;
    
    @Schema(description = "사용자일련번호", example = "U111111111")
    private String userSeqNo;
    
    @Schema(description = "조회구분코드", example = "A", allowableValues = {"A", "I", "O"})
    private String inquiryType;
    
    @Schema(description = "조회기준코드", example = "D")
    private String inquiryBase;
    
    @Schema(description = "조회시작일자", example = "20250101")
    private String fromDate;
    
    @Schema(description = "조회종료일자", example = "20250701")
    private String toDate;
    
    @Schema(description = "정렬순서", example = "D", allowableValues = {"D", "A"})
    private String sortOrder;
    
    @Schema(description = "요청일시", example = "20250704123045")
    private String tranDtime;
} 