package com.hanaTI.HanaFuture.domain.auth.dto;

import com.hanaTI.HanaFuture.domain.user.entity.Gender;
import com.hanaTI.HanaFuture.domain.user.entity.IncomeLevel;
import com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedSignUpRequest {
    
    // 기본 정보
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String phoneNumber;
    
    // === 복지 혜택 맞춤 추천을 위한 추가 정보 ===
    
    /**
     * 생년월일 (필수)
     */
    @NotNull(message = "생년월일은 필수입니다")
    private LocalDateTime birthDate;
    
    /**
     * 성별 (필수)
     */
    @NotNull(message = "성별은 필수입니다")
    private Gender gender;
    
    /**
     * 거주지 시도 (필수)
     */
    @NotBlank(message = "거주지(시도)는 필수입니다")
    private String residenceSido;
    
    /**
     * 거주지 시군구 (선택)
     */
    private String residenceSigungu;
    
    /**
     * 결혼 상태 (필수)
     */
    @NotNull(message = "결혼 상태는 필수입니다")
    private MaritalStatus maritalStatus;
    
    /**
     * 자녀 수 (기본값 0)
     */
    @Min(value = 0, message = "자녀 수는 0 이상이어야 합니다")
    @Max(value = 10, message = "자녀 수는 10 이하여야 합니다")
    private Integer numberOfChildren = 0;
    
    /**
     * 임신 여부 (기본값 false)
     */
    private Boolean isPregnant = false;
    
    /**
     * 예상 출산일 (임신 중인 경우)
     */
    private LocalDateTime expectedDueDate;
    
    /**
     * 소득 구간 (필수)
     */
    @NotNull(message = "소득 구간은 필수입니다")
    private IncomeLevel incomeLevel;
    
    /**
     * 장애인 여부 (기본값 false)
     */
    private Boolean hasDisability = false;
    
    /**
     * 한부모 가정 여부 (기본값 false)
     */
    private Boolean isSingleParent = false;
    
    /**
     * 다문화 가정 여부 (기본값 false)
     */
    private Boolean isMulticultural = false;
    
    /**
     * 관심 복지 분야 (선택)
     * 예: "임신출산,보육,교육,주거"
     */
    private String interestCategories;
    
    /**
     * 개인정보 수집 동의 (필수)
     */
    @NotNull(message = "개인정보 수집 동의는 필수입니다")
    @AssertTrue(message = "개인정보 수집에 동의해야 합니다")
    private Boolean agreeToPersonalInfo;
    
    /**
     * 복지 혜택 정보 수신 동의 (선택)
     */
    private Boolean agreeToWelfareInfo = false;
}
