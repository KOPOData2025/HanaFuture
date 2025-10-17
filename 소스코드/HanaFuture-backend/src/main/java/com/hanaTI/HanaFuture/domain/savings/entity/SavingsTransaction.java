package com.hanaTI.HanaFuture.domain.savings.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 적금 거래 내역 엔티티
 */
@Entity
@Table(name = "savings_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SavingsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 적금 목표 정보 (기존 시스템의 SavingGoal 사용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_goal_id", nullable = false)
    private SavingGoal savingGoal;

    /**
     * 거래 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsTransactionType transactionType;

    /**
     * 거래 금액
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * 거래 후 잔액
     */
    @Column(nullable = false)
    private Long balanceAfter;

    /**
     * 거래 설명
     */
    @Column(length = 200)
    private String description;

    /**
     * 거래일시
     */
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    /**
     * 자동이체 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAutoTransfer = false;

    /**
     * 거래 참조번호 (외부 시스템 연동용)
     */
    @Column(length = 50)
    private String referenceNumber;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 적금 목표 설정 (양방향 연관관계 설정용)
     */
    public void setSavingGoal(SavingGoal savingGoal) {
        this.savingGoal = savingGoal;
    }

    /**
     * 자동이체 거래로 설정
     */
    public void markAsAutoTransfer() {
        this.isAutoTransfer = true;
    }

    /**
     * 거래 참조번호 설정
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
