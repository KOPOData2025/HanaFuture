package com.hanaTI.HanaFuture.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "은행 계좌 연동 요청")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkBankAccountRequest {

    @Schema(description = "계좌번호", example = "81700123456789", required = true)
    @NotBlank(message = "계좌번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "올바른 계좌번호 형식이 아닙니다.")
    private String accountNumber;

    @Schema(description = "계좌 별칭 (선택사항)", example = "주거래 통장")
    @Size(max = 50, message = "계좌 별칭은 50자를 초과할 수 없습니다.")
    private String accountAlias;

    @Schema(description = "주계좌로 설정 여부", example = "true")
    @Builder.Default
    private Boolean setAsPrimary = false;

    @Schema(description = "자동 동기화 활성화 여부", example = "true")
    @Builder.Default
    private Boolean autoSyncEnabled = true;

    @Schema(description = "계좌 인증 방법", example = "SMS", allowableValues = {"SMS", "CERTIFICATE", "BIOMETRIC"})
    @Builder.Default
    private String authMethod = "SMS";

    @Schema(description = "인증 코드 (SMS 인증 시)", example = "123456")
    private String authCode;
}
