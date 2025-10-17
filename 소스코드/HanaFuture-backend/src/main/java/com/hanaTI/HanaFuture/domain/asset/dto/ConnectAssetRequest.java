package com.hanaTI.HanaFuture.domain.asset.dto;

import com.hanaTI.HanaFuture.domain.asset.entity.AssetAccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectAssetRequest {

    @NotBlank(message = "계좌번호는 필수입니다")
    @Size(max = 50, message = "계좌번호는 50자를 초과할 수 없습니다")
    private String accountNumber;

    @NotBlank(message = "은행 코드는 필수입니다")
    @Size(max = 10, message = "은행 코드는 10자를 초과할 수 없습니다")
    private String bankCode;

    @NotBlank(message = "은행명은 필수입니다")
    @Size(max = 50, message = "은행명은 50자를 초과할 수 없습니다")
    private String bankName;

    @Size(max = 100, message = "계좌명은 100자를 초과할 수 없습니다")
    private String accountName;

    @NotNull(message = "계좌 유형은 필수입니다")
    private AssetAccountType accountType;

    @Size(max = 3, message = "통화 코드는 3자를 초과할 수 없습니다")
    @Builder.Default
    private String currency = "KRW";

    @Size(max = 100, message = "별칭은 100자를 초과할 수 없습니다")
    private String alias;

    @Builder.Default
    private Boolean syncEnabled = true;

    @Builder.Default
    private Boolean notificationEnabled = true;

    @Builder.Default
    private Boolean isPrimary = false;

    // 계좌 인증 정보
    @NotBlank(message = "계좌 비밀번호는 필수입니다")
    private String accountPassword;

    // 추가 인증 정보 (필요시)
    private String additionalAuth;

    // 동의 관련
    @Builder.Default
    private Boolean consentToDataCollection = true;
    @Builder.Default
    private Boolean consentToThirdPartySharing = false;
}
