package com.hanafuture.openbanking.domain.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "계좌통합조회 요청")
public class AccountInfoRequest {
    
    @Schema(description = "사용자명 (최대 20자)", example = "홍길동", required = true, maxLength = 20)
    private String userName; // Y - 사용자명 (AH 20자)
    
    @Schema(description = "사용자 주민등록번호 (999999-9999999 형식)", example = "901201-1234567", required = true, minLength = 14, maxLength = 14)
    private String userNum; // Y - 사용자 주민등록번호 (하이픈 포함 14자)
    
    @Schema(description = "사용자 이메일주소 (최대 100자)", example = "hong@example.com", required = true, maxLength = 100)
    private String userEmail; // Y - 사용자 이메일주소 (E 100자)
    
    @Schema(description = "어카운트인포 서비스 제3자정보제공동의 여부 (Y 고정)", example = "Y", required = true, allowableValues = {"Y"})
    private String ainfoAgreeYn; // Y - 어카운트인포 서비스 제 3 자정보제공동의 여부(Y) (A 1자)
    
    @Schema(description = "금융기관 업권 구분", example = "1", required = true, allowableValues = {"1", "2", "4"})
    private String inquiryBankType; // Y - 금융기관 업권 구분 (AN 1자)
    // 1: 은행, 2: 상호금융기관, 4: 금융투자회사
    
    @Schema(description = "추적번호 (6자리 숫자)", example = "123456", required = true, minLength = 6, maxLength = 6)
    private String traceNo; // Y - 추적번호 (N 6자)
    
    @Schema(description = "조회 건수 (최대 30건)", example = "10", required = true, minimum = "1", maximum = "30")
    private String inquiryRecordCnt; // Y - 조회 건수 (N 6자, 최대 30건)
} 