package com.hanafuture.openbanking.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsVerifyRequest {
    
    private String userPhone; // 휴대전화번호
} 