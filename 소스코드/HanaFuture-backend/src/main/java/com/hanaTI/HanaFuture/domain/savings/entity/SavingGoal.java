package com.hanaTI.HanaFuture.domain.savings.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saving_goals")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SavingGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 적금 목표명
     */
    @Column(name = "goal_name", nullable = false)
    private String goalName;
    
    /**
     * 목표 설명
     */
    @Column(name = "description")
    private String description;
    
    /**
     * 목표 금액
     */
    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;
    
    /**
     * 현재 적립 금액
     */
    @Column(name = "current_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    /**
     * 월 납입 목표 금액
     */
    @Column(name = "monthly_target", precision = 15, scale = 2)
    private BigDecimal monthlyTarget;
    
    /**
     * 적금 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "saving_type", nullable = false)
    private SavingType savingType;
    
    /**
     * 적금 카테고리 (목적별 분류)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SavingCategory category;
    
    /**
     * 시작일
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    /**
     * 목표 만기일
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    /**
     * 적금 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SavingStatus status = SavingStatus.ACTIVE;
    
    /**
     * 연 이자율 (%)
     */
    @Column(name = "interest_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal interestRate = BigDecimal.valueOf(3.5);
    
    /**
     * 적금 생성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    /**
     * 적금 계좌번호 (새로 생성된 하나은행 적금 계좌)
     */
    @Column(name = "savings_account_number", unique = true, length = 50)
    private String savingsAccountNumber;
    
    /**
     * 은행 코드
     */
    @Column(name = "bank_code", length = 10)
    @Builder.Default
    private String bankCode = "081"; // 하나은행
    
    /**
     * 은행명
     */
    @Column(name = "bank_name", length = 50)
    @Builder.Default
    private String bankName = "하나은행";
    
    /**
     * 출금 계좌 유형 (PERSONAL_ACCOUNT, GROUP_ACCOUNT)
     */
    @Column(name = "source_type", length = 20)
    private String sourceType;
    
    /**
     * 출금 계좌 ID (개인계좌번호 또는 모임통장 ID)
     */
    @Column(name = "source_account_id", length = 50)
    private String sourceAccountId;
    
    /**
     * 자동이체일 (매월)
     */
    @Column(name = "auto_transfer_day")
    @Builder.Default
    private Integer autoTransferDay = 25;
    
    /**
     * 적금 참여자들
     */
    @OneToMany(mappedBy = "savingGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SavingParticipant> participants = new ArrayList<>();
    
    /**
     * 납입 내역들
     */
    @OneToMany(mappedBy = "savingGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SavingDeposit> deposits = new ArrayList<>();
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // === 비즈니스 메서드 ===
    
    /**
     * 목표 달성률 계산 (0-100%)
     */
    public double getAchievementRate() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
    
    /**
     * 남은 금액 계산
     */
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }
    
    /**
     * 남은 개월 수 계산
     */
    public long getRemainingMonths() {
        LocalDate now = LocalDate.now();
        if (now.isAfter(endDate)) {
            return 0;
        }
        
        long months = 0;
        LocalDate current = now;
        while (current.isBefore(endDate)) {
            current = current.plusMonths(1);
            months++;
        }
        return months;
    }
    
    /**
     * 월 권장 납입액 계산
     */
    public BigDecimal getRecommendedMonthlyAmount() {
        long remainingMonths = getRemainingMonths();
        if (remainingMonths <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal remaining = getRemainingAmount();
        return remaining.divide(BigDecimal.valueOf(remainingMonths), 0, java.math.RoundingMode.CEILING);
    }
    
    /**
     * 예상 만기 이자 계산
     */
    public BigDecimal getExpectedInterest() {
        // 단리 계산 (실제로는 복리나 더 복잡한 계산 가능)
        BigDecimal principal = currentAmount;
        BigDecimal rate = interestRate.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
        
        long remainingMonths = getRemainingMonths();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 6, java.math.RoundingMode.HALF_UP);
        
        return principal.multiply(monthlyRate).multiply(BigDecimal.valueOf(remainingMonths));
    }
    
    /**
     * 현재 적립 금액 업데이트
     */
    public void updateCurrentAmount(BigDecimal newAmount) {
        this.currentAmount = newAmount;
    }
    
    /**
     * 자동이체 설정 업데이트
     */
    public void updateAutoTransferSettings(String sourceType, String sourceAccountId, Integer transferDay) {
        this.sourceType = sourceType;
        this.sourceAccountId = sourceAccountId;
        this.autoTransferDay = transferDay;
    }
    
    /**
     * 출금 계좌 정보 조회
     */
    public String getSourceAccountDisplay() {
        if ("GROUP_ACCOUNT".equals(sourceType)) {
            return "모임통장 (" + sourceAccountId + ")";
        } else if ("PERSONAL_ACCOUNT".equals(sourceType)) {
            return "개인계좌 (" + sourceAccountId + ")";
        }
        return "출금계좌 미설정";
    }
    
    /**
     * 납입 추가
     */
    public void addDeposit(User user, BigDecimal amount, String description) {
        SavingDeposit deposit = SavingDeposit.builder()
                .savingGoal(this)
                .user(user)
                .amount(amount)
                .description(description)
                .depositDate(LocalDateTime.now())
                .build();
        
        this.deposits.add(deposit);
        this.currentAmount = this.currentAmount.add(amount);
        
        // 목표 달성 확인
        if (currentAmount.compareTo(targetAmount) >= 0) {
            this.status = SavingStatus.COMPLETED;
        }
    }
    
    /**
     * 참여자 추가
     */
    public void addParticipant(User user, ParticipantRole role) {
        SavingParticipant participant = SavingParticipant.builder()
                .savingGoal(this)
                .user(user)
                .role(role)
                .status(ParticipantStatus.ACTIVE)
                .build();
        
        this.participants.add(participant);
    }
    
    /**
     * 적금 만료 처리
     */
    public void expire() {
        this.status = SavingStatus.EXPIRED;
    }
    
    /**
     * 적금 해지
     */
    public void terminate() {
        this.status = SavingStatus.TERMINATED;
    }
}






