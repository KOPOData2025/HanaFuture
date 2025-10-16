package com.hanafuture.openbanking.domain.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "계좌 잔액 조회 응답")
public class BalanceResponse {
    @Schema(description = "은행명", example = "국민은행")
    private String bankName;

    @Schema(description = "계좌번호", example = "1234567890123456")
    private String accountNum;

    @Schema(description = "계좌잔액", example = "1000000")
    private String balanceAmt;

    @Schema(description = "출금가능액", example = "800000")
    private String availableAmt;

    @Schema(description = "계좌종류", example = "1")
    private String accountType;

    @Schema(description = "상품명", example = "자유입출금통장")
    private String productName;

    @Schema(description = "계좌개설일", example = "20200101")
    private String accountIssueDate;

    @Schema(description = "만기일", example = "20250101")
    private String maturityDate;

    @Schema(description = "최종거래일", example = "20250701")
    private String lastTranDate;
} 