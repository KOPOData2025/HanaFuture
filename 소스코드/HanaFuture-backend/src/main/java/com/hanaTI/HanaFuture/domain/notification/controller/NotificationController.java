package com.hanaTI.HanaFuture.domain.notification.controller;

import com.hanaTI.HanaFuture.domain.notification.dto.NotificationResponse;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationStatus;
import com.hanaTI.HanaFuture.domain.notification.service.NotificationService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Notification", description = "알림 관련 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "사용자 알림 목록 조회", description = "로그인한 사용자의 모든 알림을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications(
            @PathVariable Long userId) {
        
        log.info(" 알림 목록 조회 요청 - 사용자 ID: {}", userId);
        
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "읽지 않은 알림 조회", description = "로그인한 사용자의 읽지 않은 알림을 조회합니다.")
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @PathVariable Long userId) {
        
        log.info(" 읽지 않은 알림 조회 요청 - 사용자 ID: {}", userId);
        
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "읽지 않은 알림 개수", description = "로그인한 사용자의 읽지 않은 알림 개수를 조회합니다.")
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long userId) {
        
        long count = notificationService.getUnreadCount(userId);
        
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음으로 표시합니다.")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        
        log.info("알림 읽음 처리 요청 - 알림 ID: {}, 사용자 ID: {}", notificationId, userId);
        
        notificationService.markAsRead(notificationId, userId);
        
        return ResponseEntity.ok(ApiResponse.success("알림을 읽음으로 처리했습니다."));
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음으로 표시합니다.")
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable Long userId) {
        
        log.info("모든 알림 읽음 처리 요청 - 사용자 ID: {}", userId);
        
        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok(ApiResponse.success("모든 알림을 읽음으로 처리했습니다."));
    }

    @Operation(summary = "알림 상태 변경", description = "알림 상태를 변경합니다 (수락/거절 등)")
    @PutMapping("/{notificationId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long notificationId,
            @RequestBody Map<String, String> request,
            @RequestParam Long userId) {
        
        NotificationStatus status = NotificationStatus.valueOf(request.get("status"));
        
        log.info(" 알림 상태 변경 요청 - 알림 ID: {}, 상태: {}", notificationId, status);
        
        notificationService.updateNotificationStatus(notificationId, userId, status);
        
        return ResponseEntity.ok(ApiResponse.success("알림 상태를 변경했습니다."));
    }

    @Operation(summary = "알림 삭제", description = "알림을 삭제합니다")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        
        log.info(" 알림 삭제 요청 - 알림 ID: {}, 사용자 ID: {}", notificationId, userId);
        
        notificationService.deleteNotification(notificationId, userId);
        
        return ResponseEntity.ok(ApiResponse.success("알림을 삭제했습니다."));
    }
}

