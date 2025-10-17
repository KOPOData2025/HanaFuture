package com.hanaTI.HanaFuture.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = true)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String phoneNumber;
    
    /**
     * 고객식별정보 (CI) - 주민번호 SHA-256 해시값
     */
    @Column(name = "user_ci", length = 88)
    private String userCi;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;
    
    @Column
    private String refreshToken;
    
    // === 소셜 로그인 관련 필드 ===
    
    /**
     * 소셜 로그인 제공자 (GOOGLE, KAKAO, LOCAL)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    @Builder.Default
    private SocialProvider provider = SocialProvider.LOCAL;
    
    /**
     * 소셜 로그인 제공자의 사용자 ID
     */
    @Column(name = "provider_id")
    private String providerId;
    
    // === 복지 혜택 맞춤 추천을 위한 개인정보 ===
    
    /**
     * 생년월일
     */
    @Column(name = "birth_date")
    private LocalDateTime birthDate;
    
    /**
     * 성별
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    
    /**
     * 거주지 (시도)
     */
    @Column(name = "residence_sido")
    private String residenceSido;
    
    /**
     * 거주지 (시군구)
     */
    @Column(name = "residence_sigungu")
    private String residenceSigungu;
    
    /**
     * 결혼 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;
    
    /**
     * 자녀 유무
     */
    @Column(name = "has_children")
    @Builder.Default
    private Boolean hasChildren = false;
    
    /**
     * 자녀 수
     */
    @Column(name = "number_of_children")
    private Integer numberOfChildren;
    
    /**
     * 자녀 나이들 (쉼표로 구분된 문자열)
     */
    @Column(name = "children_ages")
    private String childrenAgesStr;
    
    /**
     * 관심사 목록 (쉼표로 구분된 문자열)
     */
    @Column(name = "interests")
    private String interestsStr;
    
    /**
     * 임신 여부
     */
    @Column(name = "is_pregnant")
    @Builder.Default
    private Boolean isPregnant = false;
    
    /**
     * 예상 출산일 (임신 중인 경우)
     */
    @Column(name = "expected_due_date")
    private LocalDateTime expectedDueDate;
    
    /**
     * 소득 (실제 소득 금액)
     */
    @Column(name = "income")
    private Long income;
    
    /**
     * 소득 구간 (복지 혜택 자격 판정용)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "income_level")
    private IncomeLevel incomeLevel;
    
    /**
     * 취업 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;
    
    /**
     * 장애인 여부
     */
    @Column(name = "has_disability")
    @Builder.Default
    private Boolean hasDisability = false;
    
    /**
     * 한부모 가정 여부
     */
    @Column(name = "is_single_parent")
    @Builder.Default
    private Boolean isSingleParent = false;
    
    /**
     * 다문화 가정 여부
     */
    @Column(name = "is_multicultural")
    @Builder.Default
    private Boolean isMulticultural = false;
    
    /**
     * 관심 복지 분야 (쉼표로 구분)
     */
    @Column(name = "interest_categories")
    private String interestCategories;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public void clearRefreshToken() {
        this.refreshToken = null;
    }
    
    public void updatePassword(String password) {
        this.password = password;
    }
    
    public void updateSocialInfo(SocialProvider provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }
    
    public void updateName(String name) {
        this.name = name;
    }
    
    public void updateProfile(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    
    public void updateUserCi(String userCi) {
        this.userCi = userCi;
    }
    
    /**
     * 개인 정보 업데이트 (복지 혜택 추천용)
     */
    public void updatePersonalInfo(LocalDateTime birthDate, Gender gender, String residenceSido, 
                                  String residenceSigungu, MaritalStatus maritalStatus, 
                                  Integer numberOfChildren, Boolean isPregnant, 
                                  LocalDateTime expectedDueDate, IncomeLevel incomeLevel,
                                  Boolean hasDisability, Boolean isSingleParent, 
                                  Boolean isMulticultural, String interestCategories) {
        this.birthDate = birthDate;
        this.gender = gender;
        this.residenceSido = residenceSido;
        this.residenceSigungu = residenceSigungu;
        this.maritalStatus = maritalStatus;
        this.numberOfChildren = numberOfChildren;
        this.isPregnant = isPregnant;
        this.expectedDueDate = expectedDueDate;
        this.incomeLevel = incomeLevel;
        this.hasDisability = hasDisability;
        this.isSingleParent = isSingleParent;
        this.isMulticultural = isMulticultural;
        this.interestCategories = interestCategories;
    }
    
    /**
     * 나이 계산
     */
    public Integer getAge() {
        if (birthDate == null) return null;
        return LocalDateTime.now().getYear() - birthDate.getYear();
    }
    
    /**
     * 자녀 유무 확인
     */
    public Boolean hasAnyChildren() {
        return hasChildren != null ? hasChildren : (numberOfChildren != null && numberOfChildren > 0);
    }
    
    /**
     * 복지 혜택 대상 여부 확인
     */
    public Boolean isWelfareEligible() {
        return incomeLevel == IncomeLevel.BASIC_LIVELIHOOD || 
               incomeLevel == IncomeLevel.LOW_INCOME ||
               hasDisability ||
               isSingleParent ||
               isMulticultural ||
               (numberOfChildren != null && numberOfChildren >= 3); // 다자녀
    }
    
    /**
     * 자녀 나이 목록 조회 (문자열을 List로 변환)
     */
    public List<Integer> getChildrenAges() {
        if (childrenAgesStr == null || childrenAgesStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(childrenAgesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
    
    /**
     * 관심사 목록 조회 (문자열을 List로 변환)
     */
    public List<String> getInterests() {
        if (interestsStr == null || interestsStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(interestsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 자녀 나이 목록 설정 (List를 문자열로 변환)
     */
    public void setChildrenAges(List<Integer> childrenAges) {
        if (childrenAges == null || childrenAges.isEmpty()) {
            this.childrenAgesStr = "";
        } else {
            this.childrenAgesStr = childrenAges.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }
    
    /**
     * 관심사 목록 설정 (List를 문자열로 변환)
     */
    public void setInterests(List<String> interests) {
        if (interests == null || interests.isEmpty()) {
            this.interestsStr = "";
        } else {
            this.interestsStr = String.join(",", interests);
        }
    }

    /**
     * 개인정보 업데이트
     */
    public void updatePersonalInfo(String name, String phoneNumber, LocalDateTime birthDate, 
                                 Gender gender, String residenceSido, String residenceSigungu,
                                 Long income, EmploymentStatus employmentStatus, MaritalStatus maritalStatus,
                                 Boolean hasChildren, Integer numberOfChildren, List<Integer> childrenAges,
                                 List<String> interests) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (birthDate != null) this.birthDate = birthDate;
        if (gender != null) this.gender = gender;
        if (residenceSido != null) this.residenceSido = residenceSido;
        if (residenceSigungu != null) this.residenceSigungu = residenceSigungu;
        if (income != null) this.income = income;
        if (employmentStatus != null) this.employmentStatus = employmentStatus;
        if (maritalStatus != null) this.maritalStatus = maritalStatus;
        if (hasChildren != null) this.hasChildren = hasChildren;
        if (numberOfChildren != null) this.numberOfChildren = numberOfChildren;
        if (childrenAges != null) this.setChildrenAges(childrenAges);
        if (interests != null) this.setInterests(interests);
    }
}
