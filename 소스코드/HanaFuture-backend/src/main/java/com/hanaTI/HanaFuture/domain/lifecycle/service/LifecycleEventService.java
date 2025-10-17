package com.hanaTI.HanaFuture.domain.lifecycle.service;

import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEvent;
import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEventType;
import com.hanaTI.HanaFuture.domain.lifecycle.repository.LifecycleEventRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LifecycleEventService {
    
    private final LifecycleEventRepository lifecycleEventRepository;
    
    /**
     * 사용자의 생애주기 이벤트를 자동으로 생성합니다.
     */
    public List<LifecycleEvent> generateLifecycleEvents(User user) {
        List<LifecycleEvent> events = new ArrayList<>();
        
        // 임신 관련 이벤트 생성
        if (user.getIsPregnant() && user.getExpectedDueDate() != null) {
            events.addAll(generatePregnancyEvents(user));
        }
        
        // 자녀 관련 이벤트 생성
        if (user.hasAnyChildren()) {
            events.addAll(generateChildEvents(user));
        }
        
        // 기본 생애주기 이벤트 생성
        events.addAll(generateBasicLifecycleEvents(user));
        
        // 중복 제거 및 저장
        List<LifecycleEvent> savedEvents = new ArrayList<>();
        for (LifecycleEvent event : events) {
            if (!isDuplicateEvent(event)) {
                savedEvents.add(lifecycleEventRepository.save(event));
                log.info("생애주기 이벤트 생성: {} - {}", user.getEmail(), event.getEventType().getDisplayName());
            }
        }
        
        return savedEvents;
    }
    
    /**
     * 임신 관련 이벤트 생성
     */
    private List<LifecycleEvent> generatePregnancyEvents(User user) {
        List<LifecycleEvent> events = new ArrayList<>();
        LocalDate dueDate = user.getExpectedDueDate() != null ? user.getExpectedDueDate().toLocalDate() : null;
        
        if (dueDate == null) return events;
        
        // 임신 확인 (현재 날짜)
        events.add(createEvent(user, LifecycleEventType.PREGNANCY_CONFIRMED, LocalDate.now(), null, "임신을 축하드립니다!"));
        
        // 임신 중기 (출산 예정일 - 5개월)
        LocalDate trimester2 = dueDate.minusMonths(5);
        if (trimester2.isAfter(LocalDate.now())) {
            events.add(createEvent(user, LifecycleEventType.PREGNANCY_TRIMESTER_2, trimester2, null, "임신 중기 검진을 받으세요"));
        }
        
        // 임신 후기 (출산 예정일 - 2개월)
        LocalDate trimester3 = dueDate.minusMonths(2);
        if (trimester3.isAfter(LocalDate.now())) {
            events.add(createEvent(user, LifecycleEventType.PREGNANCY_TRIMESTER_3, trimester3, null, "출산 준비를 시작하세요"));
        }
        
        // 출산 예정일
        events.add(createEvent(user, LifecycleEventType.BIRTH_EXPECTED, dueDate, null, "출산 예정일입니다"));
        
        // 출산 완료 (예정일 + 1일, 실제로는 사용자가 수동으로 업데이트)
        events.add(createEvent(user, LifecycleEventType.BIRTH_COMPLETED, dueDate.plusDays(1), null, "출산을 축하드립니다!"));
        
        return events;
    }
    
    /**
     * 자녀 관련 이벤트 생성
     */
    private List<LifecycleEvent> generateChildEvents(User user) {
        List<LifecycleEvent> events = new ArrayList<>();
        
        // 현재는 자녀 수만 알고 있으므로, 출생일을 추정해서 이벤트 생성
        // 실제로는 자녀별 상세 정보가 있어야 정확한 이벤트 생성 가능
        
        int numberOfChildren = user.getNumberOfChildren();
        LocalDate today = LocalDate.now();
        
        for (int i = 1; i <= numberOfChildren; i++) {
            String childName = "자녀 " + i;
            
            // 임시로 자녀 나이를 추정 (첫째: 5세, 둘째: 3세, 셋째: 1세 등)
            int estimatedAge = Math.max(1, 6 - (i * 2));
            LocalDate estimatedBirthDate = today.minusYears(estimatedAge);
            
            // 생일 이벤트
            LocalDate nextBirthday = estimatedBirthDate.withYear(today.getYear());
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1);
            }
            events.add(createEvent(user, LifecycleEventType.BIRTHDAY, nextBirthday, childName, childName + "의 생일입니다"));
            
            // 나이별 이벤트 생성
            events.addAll(generateAgeBasedEvents(user, childName, estimatedBirthDate));
        }
        
        return events;
    }
    
    /**
     * 나이별 이벤트 생성
     */
    private List<LifecycleEvent> generateAgeBasedEvents(User user, String childName, LocalDate birthDate) {
        List<LifecycleEvent> events = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // 백일 (생후 100일)
        LocalDate day100 = birthDate.plusDays(100);
        if (day100.isAfter(today.minusYears(1)) && day100.isBefore(today.plusYears(1))) {
            events.add(createEvent(user, LifecycleEventType.CHILD_100_DAYS, day100, childName, childName + "의 백일을 축하드립니다"));
        }
        
        // 돌 (만 1세)
        LocalDate firstBirthday = birthDate.plusYears(1);
        if (firstBirthday.isAfter(today.minusYears(1)) && firstBirthday.isBefore(today.plusYears(1))) {
            events.add(createEvent(user, LifecycleEventType.CHILD_1_YEAR, firstBirthday, childName, childName + "의 첫 번째 생일을 축하드립니다"));
        }
        
        // 만 2세부터 7세까지
        LifecycleEventType[] ageEvents = {
            LifecycleEventType.CHILD_2_YEARS,
            LifecycleEventType.CHILD_3_YEARS,
            LifecycleEventType.CHILD_4_YEARS,
            LifecycleEventType.CHILD_5_YEARS,
            LifecycleEventType.CHILD_6_YEARS,
            LifecycleEventType.CHILD_7_YEARS
        };
        
        for (int age = 2; age <= 7; age++) {
            LocalDate ageDate = birthDate.plusYears(age);
            if (ageDate.isAfter(today.minusYears(1)) && ageDate.isBefore(today.plusYears(2))) {
                events.add(createEvent(user, ageEvents[age - 2], ageDate, childName, 
                    childName + "이(가) 만 " + age + "세가 되었습니다"));
                
                // 교육 관련 이벤트도 함께 생성
                if (age == 2) {
                    events.add(createEvent(user, LifecycleEventType.DAYCARE_ELIGIBLE, ageDate, childName, 
                        childName + " 어린이집 입소가 가능합니다"));
                } else if (age == 4) {
                    events.add(createEvent(user, LifecycleEventType.KINDERGARTEN_ELIGIBLE, ageDate, childName, 
                        childName + " 유치원 입학이 가능합니다"));
                } else if (age == 7) {
                    events.add(createEvent(user, LifecycleEventType.ELEMENTARY_SCHOOL_ELIGIBLE, ageDate, childName, 
                        childName + " 초등학교 입학이 가능합니다"));
                }
            }
        }
        
        return events;
    }
    
    /**
     * 기본 생애주기 이벤트 생성
     */
    private List<LifecycleEvent> generateBasicLifecycleEvents(User user) {
        List<LifecycleEvent> events = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // 사용자 생일
        if (user.getBirthDate() != null) {
            LocalDate birthDate = user.getBirthDate().toLocalDate();
            LocalDate nextBirthday = birthDate.withYear(today.getYear());
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1);
            }
            events.add(createEvent(user, LifecycleEventType.BIRTHDAY, nextBirthday, null, "생일을 축하드립니다"));
        }
        
        // 정기 건강검진 (매년)
        LocalDate nextHealthCheckup = today.plusMonths(6); // 6개월 후
        events.add(createEvent(user, LifecycleEventType.HEALTH_CHECKUP_DUE, nextHealthCheckup, null, "정기 건강검진을 받으세요"));
        
        return events;
    }
    
    /**
     * 이벤트 생성 헬퍼 메서드
     */
    private LifecycleEvent createEvent(User user, LifecycleEventType eventType, LocalDate eventDate, 
                                     String childName, String description) {
        return LifecycleEvent.builder()
                .user(user)
                .eventType(eventType)
                .eventDate(eventDate)
                .childName(childName)
                .description(description)
                .isProcessed(false)
                .build();
    }
    
    /**
     * 중복 이벤트 체크
     */
    private boolean isDuplicateEvent(LifecycleEvent event) {
        Optional<LifecycleEvent> existing = lifecycleEventRepository.findByUserAndEventTypeAndEventDateAndChildName(
                event.getUser(), 
                event.getEventType(), 
                event.getEventDate(), 
                event.getChildName()
        );
        return existing.isPresent();
    }
    
    /**
     * 사용자의 모든 생애주기 이벤트 조회
     */
    @Transactional(readOnly = true)
    public List<LifecycleEvent> getUserLifecycleEvents(User user) {
        return lifecycleEventRepository.findByUserOrderByEventDateDesc(user);
    }
    
    /**
     * 사용자의 미처리 이벤트 조회
     */
    @Transactional(readOnly = true)
    public List<LifecycleEvent> getUnprocessedEvents(User user) {
        return lifecycleEventRepository.findByUserAndIsProcessedFalseOrderByEventDateAsc(user);
    }
    
    /**
     * 이벤트를 처리 완료로 표시
     */
    public void markEventAsProcessed(Long eventId) {
        LifecycleEvent event = lifecycleEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다: " + eventId));
        
        event.markAsProcessed();
        lifecycleEventRepository.save(event);
        
        log.info("생애주기 이벤트 처리 완료: {} - {}", event.getUser().getEmail(), event.getEventType().getDisplayName());
    }
    
    /**
     * 오늘 발생하는 이벤트 조회
     */
    @Transactional(readOnly = true)
    public List<LifecycleEvent> getTodayEvents() {
        return lifecycleEventRepository.findTodayEvents(LocalDate.now());
    }
    
    /**
     * 다가오는 이벤트 조회 (D-7, D-3, D-1)
     */
    @Transactional(readOnly = true)
    public List<LifecycleEvent> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        List<LocalDate> upcomingDates = List.of(
                today.plusDays(1),  // D-1
                today.plusDays(3),  // D-3
                today.plusDays(7)   // D-7
        );
        return lifecycleEventRepository.findUpcomingEvents(upcomingDates);
    }
}
