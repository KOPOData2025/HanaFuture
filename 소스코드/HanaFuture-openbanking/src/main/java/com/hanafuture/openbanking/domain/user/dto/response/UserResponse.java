package com.hanafuture.openbanking.domain.user.dto.response;

import com.hanafuture.openbanking.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 응답")
public class UserResponse {
    
    @Schema(description = "사용자 고유 식별값", example = "U123456789")
    private String userSeqNo;
    
    @Schema(description = "사용자 CI (연계정보, 64자리 해시값)", example = "abcd1234567890abcd1234567890abcd1234567890abcd1234567890abcd1234")
    private String userCi; // 사용자 CI (해시값)
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName; // 사용자 이름
    
    @Schema(description = "사용자 이메일", example = "hong@example.com")
    private String userEmail; // 사용자 이메일
    
    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt; // 생성일시
    
    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt; // 수정일시
    
    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userSeqNo(user.getUserSeqNo())
                .userCi(user.getUserCi())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    // 보안상 주민등록번호는 응답에서 제외
    // terms_agreed와 sms_verified는 DB에 저장하지 않음 (가입 시 검증용으로만 사용)
} 