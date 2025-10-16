package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExternalAccountRequest {
    
    private String userNum; // 사용자 주민등록번호 (999999-9999999 형식)
} 