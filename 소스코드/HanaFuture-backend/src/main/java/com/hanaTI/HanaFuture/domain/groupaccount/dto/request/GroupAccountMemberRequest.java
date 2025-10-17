package com.hanaTI.HanaFuture.domain.groupaccount.dto.request;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountRole;
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
public class GroupAccountMemberRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "역할은 필수입니다")
    private GroupAccountRole role;

    @DecimalMin(value = "0", message = "월 기여금은 0 이상이어야 합니다")
    private BigDecimal monthlyContribution;

    @Builder.Default
    private Boolean autoTransferEnabled = false;

    @Builder.Default
    private Boolean notificationEnabled = true;

    // 초대 시 메시지
    @Size(max = 500, message = "초대 메시지는 500자를 초과할 수 없습니다")
    private String inviteMessage;
}
