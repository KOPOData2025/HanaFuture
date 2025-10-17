package com.hanaTI.HanaFuture.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 사용자 연동 은행 계좌 정보
 */
@Entity
@Table(name = "user_bank_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 계좌 소유자 (사용자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 은행 코드 (하나은행: 081)
     */
    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    /**
     * 은행명
     */
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    /**
     * 계좌번호
     */
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    /**
     * 계좌명 (상품명)
     */
    @Column(name = "account_name", nullable = false)
    private String accountName;

    /**
     * 계좌 별칭 (사용자가 설정)
     */
    @Column(name = "account_alias")
    private String accountAlias;

    /**
     * 계좌 유형 (1: 입출금, 2: 적금, 3: 대출 등)
     */
    @Column(name = "account_type", nullable = false)
    private String accountType;

    /**
     * 잔액 (마지막 동기화 시점)
     */
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    /**
     * 주계좌 여부
     */
    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    /**
     * 계좌 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserBankAccountStatus status = UserBankAccountStatus.ACTIVE;

    /**
     * 자동 동기화 여부
     */
    @Column(name = "auto_sync_enabled")
    @Builder.Default
    private Boolean autoSyncEnabled = true;

    /**
     * 마지막 동기화 시간
     */
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    /**
     * 계좌 연동 시간
     */
    @CreatedDate
    @Column(name = "linked_at", nullable = false, updatable = false)
    private LocalDateTime linkedAt;

    /**
     * 정보 수정 시간
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 잔액 업데이트
     */
    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
        this.lastSyncedAt = LocalDateTime.now();
    }

    /**
     * 주계좌로 설정
     */
    public void setPrimary() {
        this.isPrimary = true;
    }

    /**
     * 주계좌 해제
     */
    public void unsetPrimary() {
        this.isPrimary = false;
    }

    /**
     * 계좌 별칭 변경
     */
    public void updateAlias(String alias) {
        this.accountAlias = alias;
    }

    /**
     * 계좌 상태 변경
     */
    public void updateStatus(UserBankAccountStatus status) {
        this.status = status;
    }

    /**
     * 자동 동기화 설정 변경
     */
    public void updateAutoSync(Boolean enabled) {
        this.autoSyncEnabled = enabled;
    }

    /**
     * 출금 가능 계좌인지 확인
     */
    public boolean isWithdrawable() {
        return status == UserBankAccountStatus.ACTIVE && 
               ("1".equals(accountType) || "입출금".equals(accountType));
    }

    /**
     * 계좌 표시명 반환 (별칭이 있으면 별칭, 없으면 계좌명)
     */
    public String getDisplayName() {
        return accountAlias != null && !accountAlias.trim().isEmpty() 
               ? accountAlias 
               : accountName;
    }
}
