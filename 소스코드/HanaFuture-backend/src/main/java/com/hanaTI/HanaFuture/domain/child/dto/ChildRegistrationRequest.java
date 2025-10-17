package com.hanaTI.HanaFuture.domain.child.dto;

import com.hanaTI.HanaFuture.domain.child.entity.AllowanceCycle;
import com.hanaTI.HanaFuture.domain.child.entity.ChildGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "자녀 등록 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildRegistrationRequest {
    
    @Schema(description = "자녀 이름", example = "김철수", required = true)
    @NotBlank(message = "자녀 이름은 필수입니다.")
    private String name;
    
    @Schema(description = "자녀 생년월일", example = "2015-03-15", required = true)
    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;
    
    @Schema(description = "자녀 성별", example = "MALE", allowableValues = {"MALE", "FEMALE"})
    private ChildGender gender;
    
    @Schema(description = "학교명", example = "서울초등학교")
    private String schoolName;
    
    @Schema(description = "학년", example = "3")
    @Positive(message = "학년은 양수여야 합니다.")
    private Integer grade;
    
    @Schema(description = "반", example = "2")
    @Positive(message = "반은 양수여야 합니다.")
    private Integer classNumber;
    
    @Schema(description = "부모 사용자 ID", example = "1", required = true)
    @NotNull(message = "부모 사용자 ID는 필수입니다.")
    private Long parentUserId;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
    
    @Schema(description = "닉네임", example = "철수")
    private String nickname;
    
    @Schema(description = "주간 용돈", example = "10000")
    private BigDecimal weeklyAllowance;
    
    @Schema(description = "월간 용돈", example = "50000")
    private BigDecimal monthlyAllowance;
    
    @Schema(description = "용돈 지급 주기", example = "WEEKLY", allowableValues = {"WEEKLY", "MONTHLY"})
    @Builder.Default
    private AllowanceCycle allowanceCycle = AllowanceCycle.WEEKLY;
}










