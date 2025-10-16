package com.hanafuture.openbanking.domain.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "계좌 상세 정보 조회 응답")
public class AccountDetailInfoResponse {
    @Schema(description = "은행명")
    private String bankName;

    @Schema(description = "저축은행명")
    private String savingsBankName;

    @Schema(description = "사용자 시퀀스 번호")
    private String userSeqNo;

    @Schema(description = "계좌번호")
    private String accountNum;

    @Schema(description = "계좌순번")
    private String accountSeq;

    @Schema(description = "계좌종류")
    private String accountType;

    @Schema(description = "서비스 구분")
    private String scope;

    @Schema(description = "핀테크이용번호")
    private String fintechUseNum;
} 