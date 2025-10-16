package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalTransactionRequest {
    
    private String userCi;
    
    private String accountNum;
    
    // 옵션값들 (기본값 설정)
    @Builder.Default
    private String inquiryType = "A"; // A: 모두, I: 입금, O: 출금
    
    @Builder.Default
    private String sortOrder = "D"; // D: 내림차순, A: 오름차순 (날짜 기준)
} 