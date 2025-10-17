package com.hanaTI.HanaFuture.domain.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "계좌 요약 정보 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSummaryResponse {
    
    @Schema(description = "총 자산", example = "26260000.00")
    private BigDecimal totalBalance;
    
    @Schema(description = "계좌 개수", example = "3")
    private Long accountCount;
    
    @Schema(description = "계좌 목록")
    private List<AccountResponse> accounts;

}





















