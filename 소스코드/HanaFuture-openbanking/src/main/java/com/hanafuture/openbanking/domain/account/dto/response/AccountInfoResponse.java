package com.hanafuture.openbanking.domain.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "계좌통합조회 응답")
public class AccountInfoResponse {
    
    @Schema(description = "계좌 목록")
    private List<AccountInfo> resList;
    
    @Getter
    @Builder
    @Schema(description = "계좌 정보")
    public static class AccountInfo {
        
        @Schema(description = "목록 일련번호", example = "1")
        private String listNum; // N(6)
        
        @Schema(description = "은행 표준 코드", example = "097")
        private String bankCodeStd; // AN(3)
        
        @Schema(description = "활동구분", example = "1")
        private String activityType; // A(1)
        
        @Schema(description = "계좌종류", example = "1")
        private String accountType; // A(1)
        
        @Schema(description = "계좌번호", example = "12345678901234567890")
        private String accountNum; // AN(20)
        
        @Schema(description = "계좌번호(마스킹)", example = "1234************7890")
        private String accountNumMasked; // AN(20)
        
        @Schema(description = "계좌순번", example = "001")
        private String accountSeq; // AN(3)
        
        @Schema(description = "예금주명", example = "홍길동")
        private String accountHolderName; // AH(20)
        
        @Schema(description = "계좌개설일자", example = "20230101")
        private String accountIssueDate; // N(8)
        
        @Schema(description = "최종거래일자", example = "20250701")
        private String lastTranDate; // N(8)
        
        @Schema(description = "상품명", example = "입출금통장")
        private String productName; // AH(100)
        
        @Schema(description = "부기명", example = "주거래")
        private String productSubName; // AH(10)
        
        @Schema(description = "휴면계좌 여부", example = "N")
        private String dormancyYn; // A(1)
        
        @Schema(description = "계좌잔액", example = "5000000")
        private String balanceAmt; // SN(15)
        
        @Schema(description = "예금잔고", example = "5000000")
        private String depositAmt; // SN(15)
        
        @Schema(description = "잔고 산출기준1", example = "1")
        private String balanceCalcBasis1; // A(1)
        
        @Schema(description = "잔고 산출기준2", example = "A")
        private String balanceCalcBasis2; // A(1)
        
        @Schema(description = "투자연계계좌 여부", example = "N")
        private String investmentLinkedYn; // A(1)
        
        @Schema(description = "은행계좌 여부", example = "Y")
        private String bankLinkedYn; // A(1)
        
        @Schema(description = "잔액발생취소여부", example = "N")
        private String balanceAfterCancelYn; // A(1)
        
        @Schema(description = "저축은행 코드", example = "")
        private String savingsBankCode; // AN(3)
    }
} 