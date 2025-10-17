package com.hanaTI.HanaFuture.domain.allowancecard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 아이카드 사용 내역
 */
@Entity
@Table(name = "allowance_card_usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AllowanceCardUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 아이카드
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance_card_id", nullable = false)
    private AllowanceCard allowanceCard;
    
    /**
     * 사용 금액
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * 가맹점 정보
     */
    @Column(name = "merchant_name", nullable = false, length = 100)
    private String merchantName;
    
    @Column(name = "merchant_category", length = 50)
    private String merchantCategory; // 편의점, 문구점, 온라인쇼핑 등
    
    @Column(name = "merchant_address", length = 200)
    private String merchantAddress;
    
    /**
     * 사용 후 잔액
     */
    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;
    
    /**
     * 승인 번호
     */
    @Column(name = "approval_number", length = 20)
    private String approvalNumber;
    
    /**
     * 사용 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AllowanceCardUsageStatus status = AllowanceCardUsageStatus.APPROVED;
    
    /**
     * 부모 승인 필요 여부
     */
    @Column(name = "requires_parent_approval")
    @Builder.Default
    private Boolean requiresParentApproval = false;
    
    /**
     * 부모 승인 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "parent_approval_status")
    private ParentApprovalStatus parentApprovalStatus;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 일반 사용 내역 생성
     */
    public static AllowanceCardUsage createUsage(AllowanceCard card, BigDecimal amount,
                                               String merchantName, String category,
                                               BigDecimal balanceAfter) {
        return AllowanceCardUsage.builder()
                .allowanceCard(card)
                .amount(amount)
                .merchantName(merchantName)
                .merchantCategory(category)
                .balanceAfter(balanceAfter)
                .approvalNumber(generateApprovalNumber())
                .build();
    }
    
    /**
     * 승인번호 생성
     */
    private static String generateApprovalNumber() {
        return String.valueOf(System.currentTimeMillis()).substring(6);
    }
    
    /**
     * 부모 승인 처리
     */
    public void approveByParent() {
        this.parentApprovalStatus = ParentApprovalStatus.APPROVED;
    }
    
    /**
     * 부모 거부 처리
     */
    public void rejectByParent() {
        this.parentApprovalStatus = ParentApprovalStatus.REJECTED;
        this.status = AllowanceCardUsageStatus.CANCELLED;
    }
}
