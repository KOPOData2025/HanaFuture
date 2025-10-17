package com.hanaTI.HanaFuture.domain.notification.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq", allocationSize = 1)
    private Long id;

    /**
     * 알림을 받는 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 알림 타입
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    /**
     * 알림 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 알림 내용
     */
    @Lob
    @Column(columnDefinition = "CLOB")
    private String content;

    /**
     * 관련 엔티티 ID (예: 모임통장 ID, 멤버 ID 등)
     */
    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    /**
     * 관련 엔티티 타입
     */
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    /**
     * 읽음 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    /**
     * 읽은 시간
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 알림 상태 (PENDING, ACCEPTED, REJECTED, CANCELLED)
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 알림 상태 변경
     */
    public void updateStatus(NotificationStatus status) {
        this.status = status;
    }
}

