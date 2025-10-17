package com.hanaTI.HanaFuture.domain.user.dto;

import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "사용자 연동 계좌 정보")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankAccountResponse {

    @Schema(description = "계좌 ID", example = "1")
    private Long id;

    @Schema(description = "은행 코드", example = "081")
    private String bankCode;

    @Schema(description = "은행명", example = "하나은행")
    private String bankName;

    @Schema(description = "계좌번호", example = "81700123456789")
    private String accountNumber;

    @Schema(description = "계좌명 (상품명)", example = "하나 입출금통장")
    private String accountName;

    @Schema(description = "계좌 별칭", example = "주거래 통장")
    private String accountAlias;

    @Schema(description = "표시명 (별칭 또는 계좌명)", example = "주거래 통장")
    private String displayName;

    @Schema(description = "계좌 유형", example = "1")
    private String accountType;

    @Schema(description = "잔액", example = "2500000.00")
    private BigDecimal balance;

    @Schema(description = "주계좌 여부", example = "true")
    private Boolean isPrimary;

    @Schema(description = "계좌 상태", example = "ACTIVE")
    private UserBankAccountStatus status;

    @Schema(description = "자동 동기화 활성화 여부", example = "true")
    private Boolean autoSyncEnabled;

    @Schema(description = "출금 가능 여부", example = "true")
    private Boolean isWithdrawable;

    @Schema(description = "마지막 동기화 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime lastSyncedAt;

    @Schema(description = "계좌 연동 시간", example = "2024-01-01T09:00:00")
    private LocalDateTime linkedAt;

    /**
     * 마스킹된 계좌번호 반환
     */
    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 8) {
            return accountNumber;
        }
        
        String prefix = accountNumber.substring(0, 3);
        String suffix = accountNumber.substring(accountNumber.length() - 4);
        String masked = "*".repeat(accountNumber.length() - 7);
        
        return prefix + masked + suffix;
    }

    /**
     * 포맷된 잔액 반환 (천 단위 콤마)
     */
    public String getFormattedBalance() {
        if (balance == null) {
            return "0";
        }
        return String.format("%,d", balance.longValue());
    }
}
