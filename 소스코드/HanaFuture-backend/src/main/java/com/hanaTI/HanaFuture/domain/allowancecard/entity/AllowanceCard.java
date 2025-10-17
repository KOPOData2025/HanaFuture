package com.hanaTI.HanaFuture.domain.allowancecard.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 아이카드 엔티티
 * - 아이부자와 유사한 선불식 용돈카드
 * - 계좌 개설 없이 충전식으로 운영
 * - 부모가 모임통장이나 개인계좌에서 충전
 */
@Entity
@Table(name = "allowance_cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AllowanceCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 카드 번호 (가상 카드 번호)
     */
    @Column(name = "card_number", unique = true, nullable = false, length = 16)
    private String cardNumber;
    
    /**
     * 카드 이름 (사용자 지정)
     */
    @Column(name = "card_name", nullable = false, length = 50)
    private String cardName;
    
    /**
     * 자녀 정보 (카드 소유자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;
    
    /**
     * 부모 정보 (카드 관리자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private User parentUser;
    
    /**
     * 현재 잔액 (선불 충전 잔액)
     */
    @Column(name = "current_balance", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    /**
     * 월 용돈 한도
     */
    @Column(name = "monthly_limit", precision = 10, scale = 2)
    private BigDecimal monthlyLimit;
    
    /**
     * 일 사용 한도
     */
    @Column(name = "daily_limit", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal dailyLimit = BigDecimal.valueOf(50000); // 기본 5만원
    
    /**
     * 카드 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AllowanceCardStatus status = AllowanceCardStatus.ACTIVE;
    
    /**
     * 자동 충전 설정
     */
    @Column(name = "auto_charge_enabled")
    @Builder.Default
    private Boolean autoChargeEnabled = false;
    
    @Column(name = "auto_charge_amount", precision = 10, scale = 2)
    private BigDecimal autoChargeAmount;
    
    @Column(name = "auto_charge_threshold", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal autoChargeThreshold = BigDecimal.valueOf(10000); // 1만원 이하 시 자동충전
    
    /**
     * 충전 소스 설정
     */
    @Column(name = "charge_source_type", length = 20)
    @Builder.Default
    private String chargeSourceType = "PERSONAL_ACCOUNT"; // PERSONAL_ACCOUNT, GROUP_ACCOUNT
    
    @Column(name = "charge_source_id", length = 50)
    private String chargeSourceId; // 충전할 계좌 ID
    
    /**
     * 사용 제한 설정
     */
    @Column(name = "usage_restrictions")
    private String usageRestrictions; // JSON 형태로 저장 (편의점, 온라인쇼핑 등)
    
    /**
     * 알림 설정
     */
    @Column(name = "notification_enabled")
    @Builder.Default
    private Boolean notificationEnabled = true;
    
    @Column(name = "low_balance_alert", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal lowBalanceAlert = BigDecimal.valueOf(5000); // 5천원 이하 시 알림
    
    /**
     * 충전 내역
     */
    @OneToMany(mappedBy = "allowanceCard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AllowanceCardCharge> charges = new ArrayList<>();
    
    /**
     * 사용 내역
     */
    @OneToMany(mappedBy = "allowanceCard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AllowanceCardUsage> usages = new ArrayList<>();
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 카드 충전
     */
    public void charge(BigDecimal amount, String sourceType, String sourceId, String description) {
        this.currentBalance = this.currentBalance.add(amount);
        
        AllowanceCardCharge charge = AllowanceCardCharge.builder()
                .allowanceCard(this)
                .amount(amount)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .description(description)
                .balanceAfter(this.currentBalance)
                .build();
                
        this.charges.add(charge);
    }
    
    /**
     * 카드 사용
     */
    public boolean use(BigDecimal amount, String merchantName, String category) {
        if (this.currentBalance.compareTo(amount) < 0) {
            return false; // 잔액 부족
        }
        
        // 일일 한도 체크
        BigDecimal todayUsage = getTodayUsage();
        if (todayUsage.add(amount).compareTo(dailyLimit) > 0) {
            return false; // 일일 한도 초과
        }
        
        this.currentBalance = this.currentBalance.subtract(amount);
        
        AllowanceCardUsage usage = AllowanceCardUsage.builder()
                .allowanceCard(this)
                .amount(amount)
                .merchantName(merchantName)
                .merchantCategory(category)
                .balanceAfter(this.currentBalance)
                .build();
                
        this.usages.add(usage);
        
        // 자동 충전 체크
        if (autoChargeEnabled && currentBalance.compareTo(autoChargeThreshold) <= 0) {
            // TODO: 자동 충전 로직 실행
        }
        
        return true;
    }
    
    /**
     * 오늘 사용 금액 계산
     */
    public BigDecimal getTodayUsage() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        return usages.stream()
                .filter(usage -> usage.getCreatedAt().isAfter(startOfDay))
                .map(AllowanceCardUsage::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 이번 달 사용 금액 계산
     */
    public BigDecimal getMonthlyUsage() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        return usages.stream()
                .filter(usage -> usage.getCreatedAt().isAfter(startOfMonth))
                .map(AllowanceCardUsage::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 카드 설정 업데이트
     */
    public void updateSettings(BigDecimal monthlyLimit, BigDecimal dailyLimit, 
                              Boolean autoChargeEnabled, BigDecimal autoChargeAmount) {
        this.monthlyLimit = monthlyLimit;
        this.dailyLimit = dailyLimit;
        this.autoChargeEnabled = autoChargeEnabled;
        this.autoChargeAmount = autoChargeAmount;
    }
    
    /**
     * 카드 상태 변경
     */
    public void updateStatus(AllowanceCardStatus newStatus) {
        this.status = newStatus;
    }
}
