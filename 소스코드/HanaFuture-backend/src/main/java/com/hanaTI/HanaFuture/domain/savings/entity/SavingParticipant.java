package com.hanaTI.HanaFuture.domain.savings.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 적금 참여자 엔티티
 * - 공동 적금에 참여하는 사용자 정보
 */
@Entity
@Table(name = "saving_participants")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SavingParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 적금 목표
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_goal_id", nullable = false)
    private SavingGoal savingGoal;
    
    /**
     * 참여자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 참여자 역할
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParticipantRole role;
    
    /**
     * 참여 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.PENDING;
    
    /**
     * 개인별 납입 목표 금액
     */
    @Column(name = "personal_target", precision = 15, scale = 2)
    private BigDecimal personalTarget;
    
    /**
     * 개인별 현재 납입 금액
     */
    @Column(name = "personal_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal personalAmount = BigDecimal.ZERO;
    
    /**
     * 초대 메시지
     */
    @Column(name = "invitation_message")
    private String invitationMessage;
    
    /**
     * 참여 승인/거절 일시
     */
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // === 비즈니스 메서드 ===
    
    /**
     * 참여 승인
     */
    public void approve() {
        this.status = ParticipantStatus.ACTIVE;
        this.respondedAt = LocalDateTime.now();
    }
    
    /**
     * 참여 거절
     */
    public void reject() {
        this.status = ParticipantStatus.INACTIVE;
        this.respondedAt = LocalDateTime.now();
    }
    
    /**
     * 납입 금액 추가
     */
    public void addAmount(BigDecimal amount) {
        this.personalAmount = this.personalAmount.add(amount);
    }
    
    /**
     * 개인 달성률 계산
     */
    public double getPersonalAchievementRate() {
        if (personalTarget == null || personalTarget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return personalAmount.divide(personalTarget, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}


















