package com.hanafuture.openbanking.domain.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "계좌 상세 정보 조회 요청")
public class AccountDetailInfoRequest {
    @Schema(description = "거래고유번호 (20자리)", example = "12345678901234567890", required = true)
    private String bankTranId;

    @Schema(description = "사용자 시퀀스 번호", example = "1", required = true)
    private String userSeqNo;

    @Schema(description = "은행 표준 코드", example = "081", required = true)
    private String bankCodeStd;

    @Schema(description = "계좌번호", example = "1234567890123456", required = true)
    private String accountNum;

    @Schema(description = "서비스 구분", example = "inquiry", required = true)
    private String scope;
} 