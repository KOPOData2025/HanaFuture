package com.hanaTI.HanaFuture.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityVerificationResponse {
    
    private String verificationId; // 인증 ID
    private String userCi; // 사용자 CI (주민등록번호 해시)
    private boolean verificationSuccess; // 인증 성공 여부
    private String message; // 결과 메시지
}

