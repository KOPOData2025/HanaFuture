package com.hanaTI.HanaFuture.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${sms.api.key:}")
    private String smsApiKey;
    
    @Value("${sms.api.secret:}")
    private String smsApiSecret;
    
    @Value("${sms.sender.number:}")
    private String senderNumber;
    
    private final RestTemplate restTemplate;

    /**
     * 모임통장 초대 SMS 발송
     */
    public boolean sendGroupInviteSms(String phoneNumber, String groupName, String inviterName, String inviteLink) {
        try {
            // SMS 내용 구성
            String message = String.format(
                "[하나Future] %s님이 '%s' 모임통장에 초대했습니다! " +
                "투명한 가계관리와 수수료 면제 혜택을 누리세요. " +
                "참여하기: %s",
                inviterName, groupName, inviteLink
            );

            return sendSmsViaNhnCloud(phoneNumber, message);
            
        } catch (Exception e) {
            log.error("SMS 발송 실패: {}", phoneNumber, e);
            return false;
        }
    }

    private boolean sendSmsViaNhnCloud(String phoneNumber, String message) {
        try {
            String url = "https://api-sms.cloud.toast.com/sms/v3.0/appKeys/{appKey}/sender/sms";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Secret-Key", smsApiSecret);
            
            Map<String, Object> body = new HashMap<>();
            body.put("title", "하나Future 모임통장 초대");
            body.put("body", message);
            body.put("sendNo", senderNumber);
            body.put("recipientList", new Object[]{
                Map.of("recipientNo", phoneNumber.replaceAll("-", ""))
            });
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                url.replace("{appKey}", smsApiKey), request, String.class);
            
            log.info("SMS 발송 결과: {}", response.getBody());
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            log.error("NHN Cloud SMS 발송 실패", e);
            return false;
        }
    }

    private boolean sendSmsViaAwsSns(String phoneNumber, String message) {
        log.info("AWS SNS SMS 발송 시뮬레이션: {} -> {}", phoneNumber, message);
        return true;
    }
}





