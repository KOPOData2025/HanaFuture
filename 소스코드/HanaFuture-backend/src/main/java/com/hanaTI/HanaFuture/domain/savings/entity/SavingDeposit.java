package com.hanaTI.HanaFuture.domain.savings.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 적금 납입 내역 엔티티
 * - 각 참여자의 납입 기록
 */
@Entity
@Table(name = "saving_deposits")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SavingDeposit {
    
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
     * 납입자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 납입 금액
     */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    /**
     * 납입 후 총 적립액
     */
    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;
    
    /**
     * 납입 설명/메모
     */
    @Column(name = "description")
    private String description;
    
    /**
     * 납입 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "deposit_type", nullable = false)
    @Builder.Default
    private DepositType depositType = DepositType.MANUAL;
    
    /**
     * 납입 일시
     */
    @Column(name = "deposit_date", nullable = false)
    private LocalDateTime depositDate;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 정기 납입 생성
     */
    public static SavingDeposit createRegularDeposit(SavingGoal goal, User user, BigDecimal amount, BigDecimal balanceAfter) {
        return SavingDeposit.builder()
                .savingGoal(goal)
                .user(user)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description("정기 납입")
                .depositType(DepositType.REGULAR)
                .depositDate(LocalDateTime.now())
                .build();
    }
    
    /**
     * 자유 납입 생성
     */
    public static SavingDeposit createFreeDeposit(SavingGoal goal, User user, BigDecimal amount, 
                                                BigDecimal balanceAfter, String description) {
        return SavingDeposit.builder()
                .savingGoal(goal)
                .user(user)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .depositType(DepositType.MANUAL)
                .depositDate(LocalDateTime.now())
                .build();
    }
}


















