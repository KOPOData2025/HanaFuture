package com.hanaTI.HanaFuture.domain.allowancecard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 아이카드 충전 내역
 */
@Entity
@Table(name = "allowance_card_charges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AllowanceCardCharge {
    
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
     * 충전 금액
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * 충전 소스 유형 (PERSONAL_ACCOUNT, GROUP_ACCOUNT)
     */
    @Column(name = "source_type", nullable = false, length = 20)
    private String sourceType;
    
    /**
     * 충전 소스 ID (계좌번호 또는 모임통장 ID)
     */
    @Column(name = "source_id", nullable = false, length = 50)
    private String sourceId;
    
    /**
     * 충전 설명
     */
    @Column(name = "description", length = 200)
    private String description;
    
    /**
     * 충전 후 잔액
     */
    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;
    
    /**
     * 충전 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type", nullable = false)
    @Builder.Default
    private AllowanceCardChargeType chargeType = AllowanceCardChargeType.MANUAL;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수동 충전 생성
     */
    public static AllowanceCardCharge createManualCharge(AllowanceCard card, BigDecimal amount, 
                                                        String sourceType, String sourceId, 
                                                        String description, BigDecimal balanceAfter) {
        return AllowanceCardCharge.builder()
                .allowanceCard(card)
                .amount(amount)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .description(description)
                .balanceAfter(balanceAfter)
                .chargeType(AllowanceCardChargeType.MANUAL)
                .build();
    }
    
    /**
     * 자동 충전 생성
     */
    public static AllowanceCardCharge createAutoCharge(AllowanceCard card, BigDecimal amount,
                                                      String sourceType, String sourceId,
                                                      BigDecimal balanceAfter) {
        return AllowanceCardCharge.builder()
                .allowanceCard(card)
                .amount(amount)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .description("자동 충전")
                .balanceAfter(balanceAfter)
                .chargeType(AllowanceCardChargeType.AUTO)
                .build();
    }
}
