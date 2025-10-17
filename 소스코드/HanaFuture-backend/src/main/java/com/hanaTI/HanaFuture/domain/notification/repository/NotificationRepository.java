package com.hanaTI.HanaFuture.domain.notification.repository;

import com.hanaTI.HanaFuture.domain.notification.entity.Notification;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationStatus;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationType;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 사용자의 모든 알림 조회 (최신순)
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자의 읽지 않은 알림 조회
     */
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    /**
     * 사용자의 특정 타입 알림 조회
     */
    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, NotificationType type);

    /**
     * 사용자의 특정 상태 알림 조회
     */
    List<Notification> findByUserAndStatusOrderByCreatedAtDesc(User user, NotificationStatus status);

    /**
     * 읽지 않은 알림 개수
     */
    long countByUserAndIsReadFalse(User user);
}

