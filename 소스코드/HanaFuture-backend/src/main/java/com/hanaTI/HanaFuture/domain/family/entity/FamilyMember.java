package com.hanaTI.HanaFuture.domain.family.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 가족 멤버 엔티티
 */
@Entity
@Table(name = "family_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "family_member_seq")
    @SequenceGenerator(name = "family_member_seq", sequenceName = "family_member_seq", allocationSize = 1)
    private Long id;

    /**
     * 가족 그룹의 주인 (나)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * 가족 멤버 (실제 사용자 - 연결된 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_user_id")
    private User memberUser;

    /**
     * 가족 멤버 이름
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 전화번호
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * 관계 (부모, 자녀, 배우자 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FamilyRelationType relationType;

    /**
     * 생년월일
     */
    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    /**
     * 초대 상태 (PENDING, ACCEPTED, REJECTED)
     */
    @Column(length = 20)
    @Builder.Default
    private String inviteStatus = "NOT_INVITED";

    /**
     * 메모
     */
    @Column(length = 500)
    private String memo;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 사용자 연결 (초대 수락 시)
     */
    public void connectUser(User user) {
        this.memberUser = user;
        this.inviteStatus = "ACCEPTED";
    }

    /**
     * 초대 상태 업데이트
     */
    public void updateInviteStatus(String status) {
        this.inviteStatus = status;
    }

    /**
     * 정보 업데이트
     */
    public void updateInfo(String name, String phoneNumber, FamilyRelationType relationType) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (relationType != null) this.relationType = relationType;
    }
}

