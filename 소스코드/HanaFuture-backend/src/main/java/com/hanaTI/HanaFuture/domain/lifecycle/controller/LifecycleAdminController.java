package com.hanaTI.HanaFuture.domain.lifecycle.controller;

import com.hanaTI.HanaFuture.domain.lifecycle.scheduler.LifecycleEventScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lifecycle")
@RequiredArgsConstructor
@Slf4j
public class LifecycleAdminController {
    
    private final LifecycleEventScheduler lifecycleEventScheduler;
    
    /**
     * 수동으로 일일 생애주기 이벤트 체크 실행
     */
    @PostMapping("/trigger/daily-check")
    public ResponseEntity<String> triggerDailyCheck() {
        try {
            log.info("관리자 요청: 일일 생애주기 이벤트 체크 수동 실행");
            lifecycleEventScheduler.manualTriggerDailyCheck();
            
            return ResponseEntity.ok("일일 생애주기 이벤트 체크가 실행되었습니다.");
            
        } catch (Exception e) {
            log.error("일일 생애주기 이벤트 체크 수동 실행 중 오류", e);
            return ResponseEntity.status(500).body("일일 체크 실행 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 수동으로 주간 생애주기 이벤트 재생성 실행
     */
    @PostMapping("/trigger/weekly-regeneration")
    public ResponseEntity<String> triggerWeeklyRegeneration() {
        try {
            log.info("관리자 요청: 주간 생애주기 이벤트 재생성 수동 실행");
            lifecycleEventScheduler.manualTriggerWeeklyRegeneration();
            
            return ResponseEntity.ok("주간 생애주기 이벤트 재생성이 실행되었습니다.");
            
        } catch (Exception e) {
            log.error("주간 생애주기 이벤트 재생성 수동 실행 중 오류", e);
            return ResponseEntity.status(500).body("주간 재생성 실행 중 오류가 발생했습니다.");
        }
    }
}