package com.hanafuture.openbanking.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVerifyRequest {
    
    private String userName; // 사용자 이름
    
    private String userPhone; // 휴대전화번호
    
    private String userEmail; // 이메일
    
    private String residentNumber; // 주민등록번호
} 