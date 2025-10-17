package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
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
 * 모임통장 엔티티
 */
@Entity
@Table(name = "group_accounts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GroupAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_account_seq")
    @SequenceGenerator(name = "group_account_seq", sequenceName = "group_account_seq", allocationSize = 1)
    private Long id;

    /**
     * 모임통장 이름
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 모임통장 계좌명
     */
    @Column(name = "group_account_name", length = 100)
    private String groupAccountName;

    /**
     * 모임통장 계좌번호
     */
    @Column(name = "group_account_number", unique = true, nullable = false, length = 20)
    private String groupAccountNumber;

    /**
     * 모임통장 설명
     */
    @Column(length = 500)
    private String description;

    /**
     * 모임통장 비밀번호 (4자리)
     */
    @Column(length = 4)
    private String password;

    /**
     * 모임 목적
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupAccountPurpose purpose;

    /**
     * 현재 잔액
     */
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;

    /**
     * 은행 코드
     */
    @Column(name = "bank_code", length = 10)
    private String bankCode;

    /**
     * 은행명
     */
    @Column(name = "bank_name", length = 50)
    private String bankName;

    /**
     * 계좌 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GroupAccountStatus status = GroupAccountStatus.ACTIVE;

    /**
     * 자동이체 설정 여부
     */
    @Column(name = "auto_transfer_enabled", nullable = false)
    @Builder.Default
    private Boolean autoTransferEnabled = false;

    @Column(name = "last_balance_update")
    private LocalDateTime lastBalanceUpdate;

    /**
     * 자동이체 일자 (매월 몇 일)
     */
    @Column(name = "auto_transfer_day")
    private Integer autoTransferDay;

    /**
     * 알림 설정 여부
     */
    @Column(name = "notification_enabled", nullable = false)
    @Builder.Default
    private Boolean notificationEnabled = true;

    /**
     * 생성자 (모임장)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * 주 사용자 ID
     */
    @Column(name = "primary_user_id")
    private Long primaryUserId;

    /**
     * 주 사용자 이름
     */
    @Column(name = "primary_user_name", length = 50)
    private String primaryUserName;

    /**
     * 출금 계좌 번호 (자동이체용)
     */
    @Column(name = "source_account_number", length = 50)
    private String sourceAccountNumber;

    /**
     * 모임통장 멤버들
     */
    @OneToMany(mappedBy = "groupAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupAccountMember> members = new ArrayList<>();

    /**
     * 모임통장 거래내역
     */
    @OneToMany(mappedBy = "groupAccount", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GroupAccountTransaction> transactions = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 잔액 업데이트
     */
    public void updateBalance(BigDecimal amount) {
        this.currentBalance = this.currentBalance.add(amount);
    }

    /**
     * 자동이체 설정
     */
    public void setAutoTransfer(boolean enabled, Integer day) {
        this.autoTransferEnabled = enabled;
        this.autoTransferDay = day;
    }

    /**
     * 계좌 상태 변경
     */
    public void updateStatus(GroupAccountStatus status) {
        this.status = status;
    }

    /**
     * 멤버 추가
     */
    public void addMember(GroupAccountMember member) {
        this.members.add(member);
        member.setGroupAccount(this);
    }

    /**
     * 거래내역 추가
     */
    public void addTransaction(GroupAccountTransaction transaction) {
        this.transactions.add(transaction);
        // transaction.setGroupAccount(this); // Immutable entity이므로 제거
    }

    // === 계산된 필드들 ===

    /**
     * 활성 멤버 수
     */
    public Long getActiveMembersCount() {
        return members.stream()
                .filter(member -> member.getStatus() == GroupAccountMemberStatus.ACTIVE)
                .count();
    }
}



