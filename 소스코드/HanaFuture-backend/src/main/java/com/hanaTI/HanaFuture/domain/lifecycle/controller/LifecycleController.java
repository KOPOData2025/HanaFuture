package com.hanaTI.HanaFuture.domain.lifecycle.controller;

import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEvent;
import com.hanaTI.HanaFuture.domain.lifecycle.service.LifecycleEventService;
import com.hanaTI.HanaFuture.domain.lifecycle.service.LifecycleWelfareRecommendationService;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lifecycle")
@RequiredArgsConstructor
@Slf4j
public class LifecycleController {
    
    private final LifecycleEventService lifecycleEventService;
    private final LifecycleWelfareRecommendationService recommendationService;
    private final UserRepository userRepository;
    
    /**
     * 사용자의 모든 생애주기 이벤트 조회
     */
    @GetMapping("/events")
    public ResponseEntity<List<LifecycleEvent>> getUserLifecycleEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> events = lifecycleEventService.getUserLifecycleEvents(user);
            
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            log.error("생애주기 이벤트 조회 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 사용자의 미처리 생애주기 이벤트 조회
     */
    @GetMapping("/events/unprocessed")
    public ResponseEntity<List<LifecycleEvent>> getUnprocessedEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> events = lifecycleEventService.getUnprocessedEvents(user);
            
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            log.error("미처리 생애주기 이벤트 조회 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 모든 미처리 이벤트에 대한 통합 추천
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<WelfareBenefitResponse>> getAllRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> unprocessedEvents = lifecycleEventService.getUnprocessedEvents(user);
            
            List<WelfareBenefitResponse> recommendations = recommendationService.getRecommendationsForMultipleEvents(unprocessedEvents);
            
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("통합 생애주기 추천 조회 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 긴급 추천 (즉시 신청해야 하는 혜택)
     */
    @GetMapping("/recommendations/urgent")
    public ResponseEntity<List<WelfareBenefitResponse>> getUrgentRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> unprocessedEvents = lifecycleEventService.getUnprocessedEvents(user);
            
            List<WelfareBenefitResponse> urgentRecommendations = recommendationService.getUrgentRecommendations(unprocessedEvents);
            
            return ResponseEntity.ok(urgentRecommendations);
            
        } catch (Exception e) {
            log.error("긴급 생애주기 추천 조회 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 생애주기 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getLifecycleStatistics(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> userEvents = lifecycleEventService.getUserLifecycleEvents(user);
            
            Map<String, Object> statistics = recommendationService.getLifecycleStatistics(userEvents);
            
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            log.error("생애주기 통계 조회 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 생애주기 이벤트 수동 생성 (사용자 요청)
     */
    @PostMapping("/events/generate")
    public ResponseEntity<List<LifecycleEvent>> generateLifecycleEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> newEvents = lifecycleEventService.generateLifecycleEvents(user);
            
            log.info("사용자 {} - 생애주기 이벤트 {}개 생성", user.getEmail(), newEvents.size());
            
            return ResponseEntity.ok(newEvents);
            
        } catch (Exception e) {
            log.error("생애주기 이벤트 생성 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 생애주기 이벤트 처리 완료 표시
     */
    @PutMapping("/events/{eventId}/complete")
    public ResponseEntity<String> markEventAsCompleted(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            List<LifecycleEvent> userEvents = lifecycleEventService.getUserLifecycleEvents(user);
            
            boolean isUserEvent = userEvents.stream()
                    .anyMatch(event -> event.getId().equals(eventId));
            
            if (!isUserEvent) {
                return ResponseEntity.status(403).build();
            }
            
            lifecycleEventService.markEventAsProcessed(eventId);
            
            return ResponseEntity.ok("이벤트가 처리 완료로 표시되었습니다.");
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            log.error("생애주기 이벤트 완료 처리 중 오류", e);
            return ResponseEntity.status(500).build();
        }
    }
}