package com.hanaTI.HanaFuture.domain.user.dto;

import com.hanaTI.HanaFuture.domain.user.entity.Gender;
import com.hanaTI.HanaFuture.domain.user.entity.IncomeLevel;
import com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 개인정보 업데이트 요청")
public class UpdateUserInfoRequest {
    
    @Schema(description = "거주지 시도", example = "서울특별시")
    @NotBlank(message = "거주지 시도는 필수입니다")
    private String residenceSido;
    
    @Schema(description = "거주지 시군구", example = "구로구")
    @NotBlank(message = "거주지 시군구는 필수입니다")
    private String residenceSigungu;
    
    @Schema(description = "생년월일", example = "1990-01-01T00:00:00")
    private LocalDateTime birthDate;
    
    @Schema(description = "성별")
    private Gender gender;
    
    @Schema(description = "결혼 상태")
    private MaritalStatus maritalStatus;
    
    @Schema(description = "자녀 수", example = "2")
    private Integer numberOfChildren;
    
    @Schema(description = "임신 여부", example = "false")
    @Builder.Default
    private Boolean isPregnant = false;
    
    @Schema(description = "출산 예정일", example = "2024-06-01T00:00:00")
    private LocalDateTime expectedDueDate;
    
    @Schema(description = "소득 수준")
    private IncomeLevel incomeLevel;
    
    @Schema(description = "장애인 여부", example = "false")
    @Builder.Default
    private Boolean hasDisability = false;
    
    @Schema(description = "한부모 가정 여부", example = "false")
    @Builder.Default
    private Boolean isSingleParent = false;
    
    @Schema(description = "다문화 가정 여부", example = "false")
    @Builder.Default
    private Boolean isMulticultural = false;
    
    @Schema(description = "관심 복지 분야 (쉼표로 구분)", example = "육아,교육,의료")
    private String interestCategories;
}
