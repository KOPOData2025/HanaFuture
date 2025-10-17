package com.hanaTI.HanaFuture.domain.groupaccount.dto.request;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountPurpose;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAccountRequest {

    @NotBlank(message = "모임통장 이름은 필수입니다")
    @Size(max = 100, message = "모임통장 이름은 100자를 초과할 수 없습니다")
    private String name;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;

    @NotNull(message = "목적은 필수입니다")
    private GroupAccountPurpose purpose;

    @DecimalMin(value = "0", message = "목표 금액은 0 이상이어야 합니다")
    private BigDecimal targetAmount;

    @Future(message = "목표 날짜는 미래 날짜여야 합니다")
    private LocalDate targetDate;

    @DecimalMin(value = "0", message = "월 목표 금액은 0 이상이어야 합니다")
    private BigDecimal monthlyTarget;

    @Builder.Default
    private Boolean autoTransferEnabled = false;

    @Min(value = 1, message = "자동이체 날짜는 1일 이상이어야 합니다")
    @Max(value = 31, message = "자동이체 날짜는 31일 이하여야 합니다")
    private Integer autoTransferDay;

    @Builder.Default
    private Boolean notificationEnabled = true;

    @Size(max = 10, message = "은행 코드는 10자를 초과할 수 없습니다")
    private String bankCode;

    @Size(max = 50, message = "은행명은 50자를 초과할 수 없습니다")
    private String bankName;
    
    /**
     * 계좌 비밀번호 (4자리)
     */
    @Size(min = 4, max = 4, message = "비밀번호는 4자리여야 합니다")
    private String accountPassword;

    // === 사용자 정보 ===
    
    @NotNull(message = "주사용자 ID는 필수입니다")
    private Long primaryUserId;
    
    @NotBlank(message = "모임통장 계좌명은 필수입니다")
    @Size(max = 100, message = "모임통장 계좌명은 100자를 초과할 수 없습니다")
    private String groupAccountName;
    
    @NotBlank(message = "핀테크이용번호는 필수입니다")
    @Size(max = 50, message = "핀테크이용번호는 50자를 초과할 수 없습니다")
    private String primaryFintechUseNum;

    // === 출금계좌 정보 ===
    
    @NotNull(message = "출금계좌 ID는 필수입니다")
    private Long withdrawAccountId;
    
    @DecimalMin(value = "0", message = "초기 입금 금액은 0 이상이어야 합니다")
    private BigDecimal initialDepositAmount;
}
