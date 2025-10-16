package com.hanafuture.openbanking.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hanafuture.openbanking.domain.user.entity.User;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 계좌 등록 요청")
public class UserRegisterRequest {
    
    @Schema(description = "거래고유번호 (20자리)", example = "12345678901234567890", required = true, minLength = 20, maxLength = 20)
    private String bankTranId; // Y - 거래고유번호 (20자리)
    
    @Schema(description = "등록계좌개설기관 표준코드 (3자리)", example = "004", required = true, minLength = 3, maxLength = 3)
    private String bankCodeStd; // Y - 등록계좌개설기관 표준코드 (3자리)
    
    @Schema(description = "등록계좌번호 (16자리)", example = "1234567890123456", required = true, minLength = 16, maxLength = 16)
    private String registerAccountNum; // Y - 등록계좌번호 (16자리)
    
    @Schema(description = "성별 생년월일 (8자리, YYYYMMDD)", example = "19901201", required = true, minLength = 8, maxLength = 8)
    private String userInfo; // Y - 성별 생년월일 (8자리)
    
    @Schema(description = "사용자명 (최대 20자리)", example = "홍길동", required = true, maxLength = 20)
    private String userName; // Y - 사용자명 (20자리)
    
    @Schema(description = "사용자 이메일", example = "hong@example.com", required = true)
    private String userEmail; // Y - 사용자 이메일
    
    @Schema(description = "CI 값 (64자리)", example = "abcd1234567890abcd1234567890abcd1234567890abcd1234567890abcd1234", required = true, minLength = 64, maxLength = 64)
    private String userCi; // Y - CI (64자리)
    
    @Schema(description = "서비스구분", example = "inquiry", allowableValues = {"inquiry", "transfer"}, required = true)
    private String scope; // Y - 서비스구분 (inquiry | transfer)

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .userEmail(this.userEmail)
                .userCi(this.userCi)
                .build();
    }
} 