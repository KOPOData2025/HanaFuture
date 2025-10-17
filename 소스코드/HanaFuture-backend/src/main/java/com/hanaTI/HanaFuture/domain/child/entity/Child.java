package com.hanaTI.HanaFuture.domain.child.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.allowancecard.entity.AllowanceCard;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 자녀 정보 엔티티
 * - 아이부자 서비스의 자녀 등록 시스템
 * - 부모가 자녀를 등록하고 관리
 */
@Entity
@Table(name = "children")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Child {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 자녀 이름
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    /**
     * 자녀 생년월일
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
    /**
     * 자녀 성별
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private ChildGender gender;
    
    /**
     * 학교 정보
     */
    @Column(name = "school_name", length = 100)
    private String schoolName;
    
    @Column(name = "grade")
    private Integer grade; // 학년
    
    @Column(name = "class_number")
    private Integer classNumber; // 반
    
    /**
     * 부모 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private User parentUser;
    
    /**
     * 자녀 프로필 정보
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    @Column(name = "nickname", length = 30)
    private String nickname;
    
    /**
     * 용돈 설정
     */
    @Column(name = "weekly_allowance", precision = 8, scale = 2)
    private java.math.BigDecimal weeklyAllowance;
    
    @Column(name = "monthly_allowance", precision = 8, scale = 2)
    private java.math.BigDecimal monthlyAllowance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "allowance_cycle")
    @Builder.Default
    private AllowanceCycle allowanceCycle = AllowanceCycle.WEEKLY;
    
    /**
     * 자녀 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ChildStatus status = ChildStatus.ACTIVE;
    
    /**
     * 연결된 아이카드들
     */
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AllowanceCard> allowanceCards = new ArrayList<>();
    
    /**
     * 금융 교육 레벨
     */
    @Column(name = "financial_education_level")
    @Builder.Default
    private Integer financialEducationLevel = 1;
    
    /**
     * 적립 포인트 (교육 활동 참여 보상)
     */
    @Column(name = "reward_points", precision = 8, scale = 2)
    @Builder.Default
    private java.math.BigDecimal rewardPoints = java.math.BigDecimal.ZERO;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 자녀 나이 계산
     */
    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    /**
     * 학급 정보 문자열
     */
    public String getClassInfo() {
        if (grade != null && classNumber != null) {
            return grade + "학년 " + classNumber + "반";
        } else if (grade != null) {
            return grade + "학년";
        }
        return "";
    }
    
    /**
     * 용돈 설정 업데이트
     */
    public void updateAllowanceSettings(java.math.BigDecimal weeklyAmount, 
                                       java.math.BigDecimal monthlyAmount, 
                                       AllowanceCycle cycle) {
        this.weeklyAllowance = weeklyAmount;
        this.monthlyAllowance = monthlyAmount;
        this.allowanceCycle = cycle;
    }
    
    /**
     * 프로필 정보 업데이트
     */
    public void updateProfile(String nickname, String profileImageUrl, 
                            String schoolName, Integer grade, Integer classNumber) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.schoolName = schoolName;
        this.grade = grade;
        this.classNumber = classNumber;
    }
    
    /**
     * 리워드 포인트 적립
     */
    public void addRewardPoints(java.math.BigDecimal points) {
        this.rewardPoints = this.rewardPoints.add(points);
    }
    
    /**
     * 금융 교육 레벨 상승
     */
    public void levelUp() {
        this.financialEducationLevel++;
    }
}
