package com.hanaTI.HanaFuture.domain.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "멤버 초대 요청")
@Getter
@Setter
@NoArgsConstructor
public class InviteMemberRequest {
    
    @Schema(description = "초대할 사용자 이메일", example = "spouse@example.com", required = true)
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @Schema(description = "초대 메시지", example = "우리 가족 생활비 계좌에 함께 참여해요!")
    private String message;
}


















