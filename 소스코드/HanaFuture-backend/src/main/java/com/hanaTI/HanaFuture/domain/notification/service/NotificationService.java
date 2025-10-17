package com.hanaTI.HanaFuture.domain.notification.service;

import com.hanaTI.HanaFuture.domain.notification.entity.Notification;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationStatus;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationType;
import com.hanaTI.HanaFuture.domain.notification.repository.NotificationRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 알림 생성
     */
    @Transactional
    public Notification createNotification(Long userId, NotificationType type, String title, String content,
                                           Long relatedEntityId, String relatedEntityType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .isRead(false)
                .status(NotificationStatus.UNREAD)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("알림 생성 완료 - 사용자: {}, 타입: {}, 제목: {}", user.getName(), type, title);
        
        return saved;
    }

    /**
     * 사용자의 모든 알림 조회
     */
    public List<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * 사용자의 읽지 않은 알림 조회
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * 읽지 않은 알림 개수
     */
    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notification.markAsRead();
        log.info("알림 읽음 처리 - ID: {}, 사용자: {}", notificationId, userId);
    }

    /**
     * 알림 상태 변경
     */
    @Transactional
    public void updateNotificationStatus(Long notificationId, Long userId, NotificationStatus status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notification.updateStatus(status);
        log.info("알림 상태 변경 - ID: {}, 상태: {}", notificationId, status);
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Notification> unreadNotifications = notificationRepository
                .findByUserAndIsReadFalseOrderByCreatedAtDesc(user);

        unreadNotifications.forEach(Notification::markAsRead);
        log.info("모든 알림 읽음 처리 - 사용자: {}, 개수: {}", user.getName(), unreadNotifications.size());
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notificationRepository.delete(notification);
        log.info("알림 삭제 완료 - ID: {}", notificationId);
    }
}

