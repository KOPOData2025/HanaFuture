package com.hanaTI.HanaFuture.domain.mydata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "마이데이터 연동 계좌 정보")
public class MyDataAccountResponse {
    
    @Schema(description = "계좌 ID", example = "1")
    private String accountId;
    
    @Schema(description = "은행명", example = "하나은행")
    private String bankName;
    
    @Schema(description = "은행 코드", example = "hana")
    private String bankCode;
    
    @Schema(description = "계좌번호", example = "81700123456789")
    private String accountNumber;
    
    @Schema(description = "상품명", example = "하나 입출금통장")
    private String productName;
    
    @Schema(description = "계좌 유형", example = "입출금")
    private String accountType;
    
    @Schema(description = "현재 잔액", example = "1500000")
    private Long balance;
    
    @Schema(description = "가용 잔액", example = "1500000")
    private Long availableBalance;
    
    @Schema(description = "계좌 활성 상태", example = "true")
    private Boolean isActive;
    
    @Schema(description = "마지막 동기화 시간")
    private LocalDateTime lastSyncedAt;
    
    @Schema(description = "연동 상태", example = "CONNECTED")
    private String connectionStatus;
    
    @Schema(description = "마이데이터 제공기관", example = "하나은행")
    private String dataProvider;
}
