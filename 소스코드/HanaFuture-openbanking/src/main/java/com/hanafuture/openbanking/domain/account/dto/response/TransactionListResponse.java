package com.hanafuture.openbanking.domain.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "거래내역조회 응답")
public class TransactionListResponse {
    
    @Schema(description = "은행명", example = "국민은행")
    private String bankName;
    
    @Schema(description = "계좌번호", example = "1234567890123456")
    private String accountNum;
    
    @Schema(description = "잔액", example = "1500000")
    private String balanceAmt;
    
    @Schema(description = "페이지당 레코드 건수", example = "2")
    private Integer pageRecordCnt;
    
    @Schema(description = "다음페이지 여부", example = "N")
    private String nextPageYn;
    
    @Schema(description = "이전 조회 추적 정보", example = "TRACEINFO123456789012")
    private String beforInquiryTraceInfo;
    
    @Schema(description = "거래내역 목록")
    private List<TransactionInfo> resList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "거래내역 정보")
    public static class TransactionInfo {
        
        @Schema(description = "거래일자", example = "20250701")
        private String tranDate;
        
        @Schema(description = "거래시간", example = "090000")
        private String tranTime;
        
        @Schema(description = "입출금구분", example = "입금")
        private String inoutType;
        
        @Schema(description = "거래구분", example = "현금")
        private String tranType;
        
        @Schema(description = "적요", example = "통장입금")
        private String printedContent;
        
        @Schema(description = "거래금액", example = "1000000")
        private String tranAmt;
        
        @Schema(description = "거래후잔액", example = "2000000")
        private String afterBalanceAmt;
        
        @Schema(description = "취급점명", example = "서울지점")
        private String branchName;
    }
} 