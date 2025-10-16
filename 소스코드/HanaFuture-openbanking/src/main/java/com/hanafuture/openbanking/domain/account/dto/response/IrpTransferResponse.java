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
@Schema(description = "IRP 실물이전 응답")
public class IrpTransferResponse {
    
    @Schema(description = "출금된 계좌번호", example = "110555888999")
    private String wdAccountNum;
    
    @Schema(description = "입금된 계좌번호", example = "2345678901234567")
    private String rsvAccountNum;
    
    @Schema(description = "마스킹된 입금된 계좌번호", example = "234-567***-****567")
    private String maskedAccountNum;
    
    @Schema(description = "입금받을 은행타입", example = "081")
    private String rsvBankCodeStd;
    
    @Schema(description = "계좌종류", example = "I")
    private String accountType;
    
    @Schema(description = "IRP 타입", example = "원금보장형")
    private String irpType;
    
    @Schema(description = "IRP 투자 상품", example = "농협 정기 예금 10%")
    private String irpProductName;
    
    @Schema(description = "만기일", example = "20251231")
    private String maturityDate;
    
    @Schema(description = "입금 금액", example = "5000000")
    private String depositAmt;
    
    @Schema(description = "계좌잔액", example = "5000000")
    private String balanceAmt;
    
    @Schema(description = "납입기간", example = "12")
    private String paymentPrd;
    
    @Schema(description = "수정일자", example = "20250704")
    private String updatedAt;
} 