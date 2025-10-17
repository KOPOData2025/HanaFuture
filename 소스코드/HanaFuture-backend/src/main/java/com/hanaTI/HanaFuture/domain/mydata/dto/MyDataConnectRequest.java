package com.hanaTI.HanaFuture.domain.mydata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "마이데이터 계좌 연동 요청")
public class MyDataConnectRequest {
    
    @NotEmpty(message = "연동할 은행을 선택해주세요")
    @Schema(description = "연동할 은행 코드 목록", example = "[\"hana\", \"kb\", \"shinhan\"]")
    private List<String> bankCodes;
    
    @NotNull(message = "인증 방법을 선택해주세요")
    @Schema(description = "본인인증 방법", example = "certificate", allowableValues = {"certificate", "mobile", "card"})
    private String authMethod;
    
    @Schema(description = "동의 항목", example = "true")
    private Boolean consentToDataCollection = true;
    
    @Schema(description = "제3자 제공 동의", example = "true")
    private Boolean consentToThirdPartySharing = true;
    
    @Schema(description = "마케팅 수신 동의", example = "false")
    private Boolean consentToMarketing = false;
}
