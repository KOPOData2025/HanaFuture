package com.hanaTI.HanaFuture.domain.lifecycle.scheduler;

import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEvent;
import com.hanaTI.HanaFuture.domain.lifecycle.service.LifecycleEventService;
import com.hanaTI.HanaFuture.domain.lifecycle.service.LifecycleWelfareRecommendationService;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LifecycleEventScheduler {
    
    private final LifecycleEventService lifecycleEventService;
    private final LifecycleWelfareRecommendationService recommendationService;
    private final UserRepository userRepository;
    
    /**
     * 매일 오전 9시에 생애주기 이벤트 체크 및 알림 발송
     */
    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
    @Transactional
    public void checkDailyLifecycleEvents() {
        log.info("=== 일일 생애주기 이벤트 체크 시작 ===");
        
        try {
            // 오늘 발생하는 이벤트 처리
            List<LifecycleEvent> todayEvents = lifecycleEventService.getTodayEvents();
            log.info("오늘 발생하는 생애주기 이벤트: {}개", todayEvents.size());
            
            for (LifecycleEvent event : todayEvents) {
                processLifecycleEvent(event);
            }
            
            // 다가오는 이벤트 알림 (D-7, D-3, D-1)
            List<LifecycleEvent> upcomingEvents = lifecycleEventService.getUpcomingEvents();
            log.info("다가오는 생애주기 이벤트: {}개", upcomingEvents.size());
            
            for (LifecycleEvent event : upcomingEvents) {
                sendUpcomingEventNotification(event);
            }
            
        } catch (Exception e) {
            log.error("생애주기 이벤트 체크 중 오류 발생", e);
        }
        
        log.info("=== 일일 생애주기 이벤트 체크 완료 ===");
    }
    
    /**
     * 매주 일요일 오전 10시에 모든 사용자의 생애주기 이벤트 재생성
     */
    @Scheduled(cron = "0 0 10 * * SUN") // 매주 일요일 오전 10시
    @Transactional
    public void regenerateLifecycleEvents() {
        log.info("=== 주간 생애주기 이벤트 재생성 시작 ===");
        
        try {
            List<User> allUsers = userRepository.findAll();
            log.info("전체 사용자 수: {}명", allUsers.size());
            
            int processedCount = 0;
            for (User user : allUsers) {
                try {
                    // 각 사용자의 생애주기 이벤트 재생성
                    List<LifecycleEvent> newEvents = lifecycleEventService.generateLifecycleEvents(user);
                    if (!newEvents.isEmpty()) {
                        log.info("사용자 {} - 새로운 생애주기 이벤트 {}개 생성", user.getEmail(), newEvents.size());
                        processedCount++;
                    }
                } catch (Exception e) {
                    log.error("사용자 {} 생애주기 이벤트 생성 중 오류", user.getEmail(), e);
                }
            }
            
            log.info("생애주기 이벤트 재생성 완료: {}명 처리", processedCount);
            
        } catch (Exception e) {
            log.error("생애주기 이벤트 재생성 중 오류 발생", e);
        }
        
        log.info("=== 주간 생애주기 이벤트 재생성 완료 ===");
    }
    
    /**
     * 매월 1일 오전 11시에 생애주기 통계 업데이트
     */
    @Scheduled(cron = "0 0 11 1 * *") // 매월 1일 오전 11시
    @Transactional(readOnly = true)
    public void updateLifecycleStatistics() {
        log.info("=== 월간 생애주기 통계 업데이트 시작 ===");
        
        try {
            List<User> allUsers = userRepository.findAll();
            
            int totalEvents = 0;
            int processedEvents = 0;
            int upcomingEvents = 0;
            
            for (User user : allUsers) {
                List<LifecycleEvent> userEvents = lifecycleEventService.getUserLifecycleEvents(user);
                totalEvents += userEvents.size();
                
                long processed = userEvents.stream().filter(LifecycleEvent::getIsProcessed).count();
                processedEvents += processed;
                
                long upcoming = userEvents.stream().filter(LifecycleEvent::isUpcoming).count();
                upcomingEvents += upcoming;
            }
            
            log.info("=== 생애주기 통계 ===");
            log.info("전체 사용자: {}명", allUsers.size());
            log.info("전체 이벤트: {}개", totalEvents);
            log.info("처리된 이벤트: {}개", processedEvents);
            log.info("다가오는 이벤트: {}개", upcomingEvents);
            log.info("처리율: {:.1f}%", totalEvents > 0 ? (processedEvents * 100.0 / totalEvents) : 0);
            
        } catch (Exception e) {
            log.error("생애주기 통계 업데이트 중 오류 발생", e);
        }
        
        log.info("=== 월간 생애주기 통계 업데이트 완료 ===");
    }
    
    /**
     * 생애주기 이벤트 처리
     */
    private void processLifecycleEvent(LifecycleEvent event) {
        try {
            log.info("생애주기 이벤트 처리: {} - {} ({})", 
                    event.getUser().getEmail(), 
                    event.getEventType().getDisplayName(),
                    event.getChildName() != null ? event.getChildName() : "본인");
            
            // 이벤트에 따른 복지 혜택 추천
            List<WelfareBenefitResponse> recommendations = recommendationService.getRecommendationsForLifecycleEvent(event);
            
            if (!recommendations.isEmpty()) {
                log.info("추천 복지 혜택 {}개 발견", recommendations.size());
                
                // 실제로는 여기서 알림 서비스를 호출하여 사용자에게 알림 발송
                // 예: notificationService.sendLifecycleRecommendation(event.getUser(), event, recommendations);
                
                // 로그로 추천 내용 출력
                for (WelfareBenefitResponse recommendation : recommendations.subList(0, Math.min(3, recommendations.size()))) {
                    log.info("추천 혜택: {} - {}", recommendation.getServiceName(), recommendation.getRecommendationReason());
                }
            }
            
            // 이벤트를 처리 완료로 표시
            lifecycleEventService.markEventAsProcessed(event.getId());
            
        } catch (Exception e) {
            log.error("생애주기 이벤트 처리 중 오류: {}", event.getId(), e);
        }
    }
    
    /**
     * 다가오는 이벤트 알림 발송
     */
    private void sendUpcomingEventNotification(LifecycleEvent event) {
        try {
            long daysUntil = event.getDaysUntilEvent();
            String timeFrame = "";
            
            if (daysUntil == 1) {
                timeFrame = "내일";
            } else if (daysUntil == 3) {
                timeFrame = "3일 후";
            } else if (daysUntil == 7) {
                timeFrame = "일주일 후";
            }
            
            log.info("다가오는 이벤트 알림: {} - {} {} ({})", 
                    event.getUser().getEmail(), 
                    timeFrame,
                    event.getEventType().getDisplayName(),
                    event.getChildName() != null ? event.getChildName() : "본인");
            
            // 미리 준비할 수 있는 복지 혜택 추천
            List<WelfareBenefitResponse> recommendations = recommendationService.getRecommendationsForLifecycleEvent(event);
            
            if (!recommendations.isEmpty()) {
                log.info("미리 준비할 수 있는 혜택 {}개", recommendations.size());
                
                // 실제로는 여기서 알림 서비스를 호출
                // 예: notificationService.sendUpcomingEventNotification(event.getUser(), event, recommendations, daysUntil);
            }
            
        } catch (Exception e) {
            log.error("다가오는 이벤트 알림 발송 중 오류: {}", event.getId(), e);
        }
    }
    
    /**
     * 테스트용 수동 실행 메서드 (개발 환경에서만 사용)
     */
    public void manualTriggerDailyCheck() {
        log.info("수동 트리거: 일일 생애주기 이벤트 체크");
        checkDailyLifecycleEvents();
    }
    
    /**
     * 테스트용 수동 실행 메서드 (개발 환경에서만 사용)
     */
    public void manualTriggerWeeklyRegeneration() {
        log.info("수동 트리거: 주간 생애주기 이벤트 재생성");
        regenerateLifecycleEvents();
    }
}
