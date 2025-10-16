package com.hanafuture.openbanking.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 계좌 등록 응답")
public class UserRegisterResponse {
    
    @Schema(description = "사용자 고유 식별값", example = "U123456789")
    private String userSeqNo;

    @Schema(description = "핀테크이용번호", example = "A1B2C3D4E5F6G7H8")
    private String fintechUseNum;

    @Schema(description = "계좌번호", example = "1234567890123456")
    private String accountNum;
} 