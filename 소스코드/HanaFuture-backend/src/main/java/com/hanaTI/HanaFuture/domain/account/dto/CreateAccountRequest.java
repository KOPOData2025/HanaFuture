package com.hanaTI.HanaFuture.domain.account.dto;

import com.hanaTI.HanaFuture.domain.account.entity.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "계좌 생성 요청")
@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequest {
    
    @Schema(description = "계좌명", example = "가족 공동계좌", required = true)
    @NotBlank(message = "계좌명은 필수입니다.")
    @Size(min = 2, max = 50, message = "계좌명은 2자 이상 50자 이하여야 합니다.")
    private String accountName;
    
    @Schema(description = "계좌 유형", example = "FAMILY_JOINT", required = true)
    @NotNull(message = "계좌 유형은 필수입니다.")
    private AccountType accountType;
    
    @Schema(description = "은행명", example = "하나은행", required = true)
    @NotBlank(message = "은행명은 필수입니다.")
    @Size(max = 20, message = "은행명은 20자 이하여야 합니다.")
    private String bankName;
    
    @Schema(description = "계좌 설명", example = "가족이 함께 사용하는 계좌입니다")
    @Size(max = 200, message = "계좌 설명은 200자 이하여야 합니다.")
    private String description;
}

























