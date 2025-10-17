package com.hanaTI.HanaFuture.domain.groupaccount.dto.request;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountTransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAccountTransactionRequest {

    @NotNull(message = "거래 유형은 필수입니다")
    private GroupAccountTransactionType type;

    @NotNull(message = "금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "금액은 0.01 이상이어야 합니다")
    private BigDecimal amount;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;

    @Size(max = 100, message = "참조 ID는 100자를 초과할 수 없습니다")
    private String referenceId;

    // 이체 관련 필드들
    private String fromAccountNumber;
    private String toAccountNumber;
    private String fromBankCode;
    private String toBankCode;
    
    // 비밀번호 (출금 시 필요)
    private String accountPassword;
}
