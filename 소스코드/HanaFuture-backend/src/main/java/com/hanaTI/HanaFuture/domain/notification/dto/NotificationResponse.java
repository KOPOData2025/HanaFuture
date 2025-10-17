package com.hanaTI.HanaFuture.domain.notification.dto;

import com.hanaTI.HanaFuture.domain.notification.entity.Notification;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationStatus;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String title;
    private String content;
    private Long relatedEntityId;
    private String relatedEntityType;
    private Boolean isRead;
    private LocalDateTime readAt;
    private NotificationStatus status;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

