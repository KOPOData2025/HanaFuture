package com.hanaTI.HanaFuture.domain.notification.controller;

import com.hanaTI.HanaFuture.domain.notification.dto.GroupInviteRequest;
import com.hanaTI.HanaFuture.domain.notification.service.SmsService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(name = "Invite", description = "모임통장 초대 관련 API")
@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
@Slf4j
public class InviteController {

    private final SmsService smsService;

    @Operation(
        summary = "모임통장 초대 메시지 발송",
        description = "SMS로 모임통장 초대 메시지를 발송합니다. (카카오톡 공유는 프론트엔드에서 처리)"
    )
    @PostMapping("/group-account")
    public ResponseEntity<ApiResponse<Object>> sendGroupAccountInvite(
            @Valid @RequestBody GroupInviteRequest request) {
        
        log.info("모임통장 초대 메시지 발송 요청: 그룹명={}, 초대자={}, 수신자 수={}", 
                request.getGroupName(), request.getInviterName(), request.getMembers().size());
        
        try {
            int successCount = 0;
            int totalCount = request.getMembers().size();

            for (GroupInviteRequest.MemberInfo member : request.getMembers()) {
                boolean sent = smsService.sendGroupInviteSms(
                    member.getPhoneNumber(),
                    request.getGroupName(),
                    request.getInviterName(),
                    generateInviteLink(request.getGroupId(), member.getPhoneNumber())
                );
                
                if (sent) {
                    log.info("SMS 초대 메시지 발송 성공: {}", member.getPhoneNumber());
                    successCount++;
                } else {
                    log.error("SMS 초대 메시지 발송 실패: {}", member.getPhoneNumber());
                }
            }
            
            String message = String.format("초대 메시지 발송 완료: %d/%d 성공", successCount, totalCount);
            
            return ResponseEntity.ok(ApiResponse.success(message, Map.of(
                "totalCount", totalCount,
                "successCount", successCount,
                "failCount", totalCount - successCount
            )));
            
        } catch (Exception e) {
            log.error("모임통장 초대 메시지 발송 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("초대 메시지 발송에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 초대 링크 생성
     */
    private String generateInviteLink(String groupId, String phoneNumber) {
        String token = generateSecureToken(groupId, phoneNumber);
        return String.format("https://hanafuture.com/invite?token=%s", token);
    }

    private String generateSecureToken(String groupId, String phoneNumber) {
        return java.util.Base64.getEncoder()
                .encodeToString((groupId + ":" + phoneNumber + ":" + System.currentTimeMillis()).getBytes());
    }
}
