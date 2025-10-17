package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 모임통장 멤버 엔티티
 */
@Entity
@Table(name = "group_account_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GroupAccountMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_account_member_seq")
    @SequenceGenerator(name = "group_account_member_seq", sequenceName = "group_account_member_seq", allocationSize = 1)
    private Long id;

    /**
     * 모임통장
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_account_id", nullable = false)
    @Setter
    private GroupAccount groupAccount;

    /**
     * 멤버 (가입된 사용자인 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 멤버 이름 (미가입 사용자인 경우)
     */
    @Column(name = "user_name", length = 50)
    private String userName;

    /**
     * 멤버 이메일
     */
    @Column(name = "user_email", length = 100)
    private String userEmail;

    /**
     * 멤버 전화번호
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    /**
     * 초대받은 사람 이름 (PENDING 상태에서 사용)
     */
    @Column(name = "name", length = 50)
    private String name;
    
    /**
     * 초대받은 사람 전화번호 (PENDING 상태에서 사용)
     */
    @Column(name = "phone", length = 20)
    private String phone;
    
    /**
     * 초대한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;
    
    /**
     * 초대 토큰 (개선된 버전)
     */
    @Column(name = "invite_token", length = 200)
    private String inviteToken;

    /**
     * 멤버 역할
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupAccountRole role;

    /**
     * 멤버 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GroupAccountMemberStatus status = GroupAccountMemberStatus.PENDING;

    /**
     * 기여 금액 (총 입금액)
     */
    @Column(name = "contribution_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal contributionAmount = BigDecimal.ZERO;

    /**
     * 월 기여 금액
     */
    @Column(name = "monthly_contribution", precision = 15, scale = 2)
    private BigDecimal monthlyContribution;

    /**
     * 자동이체 설정 여부
     */
    @Column(name = "auto_transfer_enabled", nullable = false)
    @Builder.Default
    private Boolean autoTransferEnabled = false;

    /**
     * 초대 토큰 (초대 링크용)
     */
    @Column(name = "invitation_token", unique = true, length = 100)
    private String invitationToken;

    /**
     * 초대 만료일시
     */
    @Column(name = "invitation_expires_at")
    private LocalDateTime invitationExpiresAt;

    /**
     * 가입일시
     */
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 초대 수락
     */
    public void acceptInvitation(User user) {
        this.user = user;
        this.status = GroupAccountMemberStatus.ACTIVE;
        this.joinedAt = LocalDateTime.now();
    }

    /**
     * 초대 거절
     */
    public void rejectInvitation() {
        this.status = GroupAccountMemberStatus.REJECTED;
    }

    /**
     * 초대 토큰 설정
     */
    public void setInvitationToken(String token, LocalDateTime expiresAt) {
        this.invitationToken = token;
        this.invitationExpiresAt = expiresAt;
    }

    /**
     * 기여 금액 추가
     */
    public void addContribution(BigDecimal amount) {
        this.contributionAmount = this.contributionAmount.add(amount);
    }

    /**
     * 멤버 상태 변경
     */
    public void updateStatus(GroupAccountMemberStatus status) {
        this.status = status;
    }

    /**
     * 권한 확인
     */
    public boolean hasPermission(GroupAccountPermission permission) {
        return this.role.hasPermission(permission);
    }
    
    /**
     * 초대 토큰 업데이트
     */
    public void updateInviteToken(String token) {
        this.inviteToken = token;
    }
    
    /**
     * PENDING → ACTIVE 전환 시 사용자 정보 연결
     */
    public void updateUserInfo(User user) {
        this.user = user;
        this.userName = user.getName();
        this.userEmail = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.joinedAt = LocalDateTime.now();
    }
}




