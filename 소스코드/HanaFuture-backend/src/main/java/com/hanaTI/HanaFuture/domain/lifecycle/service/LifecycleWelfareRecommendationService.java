package com.hanaTI.HanaFuture.domain.lifecycle.service;

import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEvent;
import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEventType;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LifecycleWelfareRecommendationService {
    
    private final WelfareBenefitRepository welfareBenefitRepository;
    
    /**
     * 생애주기 이벤트에 따른 복지 혜택 추천
     */
    public List<WelfareBenefitResponse> getRecommendationsForLifecycleEvent(LifecycleEvent event) {
        List<String> keywords = getKeywordsForEventType(event.getEventType());
        List<WelfareBenefit> recommendations = new ArrayList<>();
        
        // 키워드 기반 검색
        for (String keyword : keywords) {
            List<WelfareBenefit> benefits = welfareBenefitRepository.findByServiceNameContainingOrTargetDescriptionContaining(keyword, keyword);
            recommendations.addAll(benefits);
        }
        
        // 중복 제거 및 정렬
        List<WelfareBenefit> uniqueRecommendations = recommendations.stream()
                .distinct()
                .sorted((a, b) -> {
                    // 지원금액 높은 순으로 정렬
                    if (a.getSupportAmount() != null && b.getSupportAmount() != null) {
                        return b.getSupportAmount().compareTo(a.getSupportAmount());
                    }
                    return a.getServiceName().compareTo(b.getServiceName());
                })
                .limit(10) // 상위 10개만
                .collect(Collectors.toList());
        
        return uniqueRecommendations.stream()
                .map(benefit -> WelfareBenefitResponse.builder()
                        .id(benefit.getId())
                        .serviceName(benefit.getServiceName())
                        .serviceType(benefit.getServiceType())
                        .lifeCycle(benefit.getLifeCycle())
                        .targetDescription(benefit.getTargetDescription())
                        .supportAmount(benefit.getSupportAmount())
                        .inquiryUrl(benefit.getInquiryUrl())
                        .jurisdictionName(benefit.getJurisdictionName())
                        .sidoName(benefit.getSidoName())
                        .sigunguName(benefit.getSigunguName())
                        .recommendationReason(generateRecommendationReason(event, benefit))
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 생애주기 이벤트 타입별 검색 키워드 매핑
     */
    private List<String> getKeywordsForEventType(LifecycleEventType eventType) {
        Map<LifecycleEventType, List<String>> keywordMap = new HashMap<>();
        
        // 임신 관련
        keywordMap.put(LifecycleEventType.PREGNANCY_CONFIRMED, Arrays.asList("임신", "산모", "태교", "산전", "임산부"));
        keywordMap.put(LifecycleEventType.PREGNANCY_TRIMESTER_2, Arrays.asList("임신", "산모", "산전검사", "임산부"));
        keywordMap.put(LifecycleEventType.PREGNANCY_TRIMESTER_3, Arrays.asList("임신", "산모", "출산준비", "분만", "임산부"));
        keywordMap.put(LifecycleEventType.BIRTH_EXPECTED, Arrays.asList("출산", "분만", "산후", "신생아", "출산지원"));
        keywordMap.put(LifecycleEventType.BIRTH_COMPLETED, Arrays.asList("출산", "신생아", "산후조리", "첫만남", "출산지원"));
        
        // 자녀 성장 관련
        keywordMap.put(LifecycleEventType.CHILD_100_DAYS, Arrays.asList("신생아", "영아", "예방접종", "건강검진"));
        keywordMap.put(LifecycleEventType.CHILD_1_YEAR, Arrays.asList("영아", "돌", "예방접종", "건강검진", "아동수당"));
        keywordMap.put(LifecycleEventType.CHILD_2_YEARS, Arrays.asList("영유아", "보육", "어린이집", "양육수당", "아동수당"));
        keywordMap.put(LifecycleEventType.CHILD_3_YEARS, Arrays.asList("유아", "보육", "어린이집", "보육료", "아동수당"));
        keywordMap.put(LifecycleEventType.CHILD_4_YEARS, Arrays.asList("유아", "보육", "어린이집", "유치원", "보육료"));
        keywordMap.put(LifecycleEventType.CHILD_5_YEARS, Arrays.asList("유아", "유치원", "누리과정", "교육비"));
        keywordMap.put(LifecycleEventType.CHILD_6_YEARS, Arrays.asList("아동", "유치원", "누리과정", "교육비", "초등학교"));
        keywordMap.put(LifecycleEventType.CHILD_7_YEARS, Arrays.asList("아동", "초등학교", "교육비", "방과후", "돌봄"));
        
        // 교육 관련
        keywordMap.put(LifecycleEventType.DAYCARE_ELIGIBLE, Arrays.asList("어린이집", "보육", "보육료", "양육수당"));
        keywordMap.put(LifecycleEventType.KINDERGARTEN_ELIGIBLE, Arrays.asList("유치원", "누리과정", "교육비"));
        keywordMap.put(LifecycleEventType.ELEMENTARY_SCHOOL_ELIGIBLE, Arrays.asList("초등학교", "교육비", "방과후", "돌봄"));
        
        // 건강 관련
        keywordMap.put(LifecycleEventType.HEALTH_CHECKUP_DUE, Arrays.asList("건강검진", "의료비", "치료비"));
        keywordMap.put(LifecycleEventType.VACCINATION_DUE, Arrays.asList("예방접종", "백신", "의료비"));
        
        return keywordMap.getOrDefault(eventType, Arrays.asList("아동", "육아", "양육"));
    }
    
    /**
     * 추천 이유 생성
     */
    private String generateRecommendationReason(LifecycleEvent event, WelfareBenefit benefit) {
        String eventDescription = event.getEventType().getDescription();
        String childInfo = event.getChildName() != null ? event.getChildName() + "이(가) " : "";
        
        Map<LifecycleEventType, String> reasonTemplates = new HashMap<>();
        reasonTemplates.put(LifecycleEventType.PREGNANCY_CONFIRMED, "임신을 축하드립니다! 산모와 태아를 위한 지원 혜택입니다.");
        reasonTemplates.put(LifecycleEventType.BIRTH_COMPLETED, "출산을 축하드립니다! 신생아와 산모를 위한 지원 혜택입니다.");
        reasonTemplates.put(LifecycleEventType.CHILD_2_YEARS, childInfo + "만 2세가 되어 어린이집 입소가 가능합니다.");
        reasonTemplates.put(LifecycleEventType.CHILD_3_YEARS, childInfo + "만 3세가 되어 보육료 지원을 받을 수 있습니다.");
        reasonTemplates.put(LifecycleEventType.CHILD_5_YEARS, childInfo + "만 5세가 되어 누리과정 교육비 지원을 받을 수 있습니다.");
        reasonTemplates.put(LifecycleEventType.DAYCARE_ELIGIBLE, childInfo + "어린이집 입소 연령이 되었습니다.");
        reasonTemplates.put(LifecycleEventType.KINDERGARTEN_ELIGIBLE, childInfo + "유치원 입학 연령이 되었습니다.");
        reasonTemplates.put(LifecycleEventType.ELEMENTARY_SCHOOL_ELIGIBLE, childInfo + "초등학교 입학 연령이 되었습니다.");
        
        String template = reasonTemplates.get(event.getEventType());
        if (template != null) {
            return template;
        }
        
        // 기본 템플릿
        return eventDescription + " 관련 지원 혜택입니다.";
    }
    
    /**
     * 여러 생애주기 이벤트에 대한 통합 추천
     */
    public List<WelfareBenefitResponse> getRecommendationsForMultipleEvents(List<LifecycleEvent> events) {
        Set<WelfareBenefit> allRecommendations = new HashSet<>();
        Map<Long, String> reasonMap = new HashMap<>();
        
        for (LifecycleEvent event : events) {
            List<WelfareBenefitResponse> eventRecommendations = getRecommendationsForLifecycleEvent(event);
            for (WelfareBenefitResponse response : eventRecommendations) {
                // 혜택 ID로 중복 체크
                if (!reasonMap.containsKey(response.getId())) {
                    reasonMap.put(response.getId(), response.getRecommendationReason());
                    
                    // WelfareBenefit 객체 찾기
                    welfareBenefitRepository.findById(response.getId()).ifPresent(allRecommendations::add);
                }
            }
        }
        
        return allRecommendations.stream()
                .sorted((a, b) -> {
                    if (a.getSupportAmount() != null && b.getSupportAmount() != null) {
                        return b.getSupportAmount().compareTo(a.getSupportAmount());
                    }
                    return a.getServiceName().compareTo(b.getServiceName());
                })
                .limit(15) // 상위 15개
                .map(benefit -> WelfareBenefitResponse.builder()
                        .id(benefit.getId())
                        .serviceName(benefit.getServiceName())
                        .serviceType(benefit.getServiceType())
                        .lifeCycle(benefit.getLifeCycle())
                        .targetDescription(benefit.getTargetDescription())
                        .supportAmount(benefit.getSupportAmount())
                        .inquiryUrl(benefit.getInquiryUrl())
                        .jurisdictionName(benefit.getJurisdictionName())
                        .sidoName(benefit.getSidoName())
                        .sigunguName(benefit.getSigunguName())
                        .recommendationReason(reasonMap.get(benefit.getId()))
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 긴급도별 추천 (즉시 신청해야 하는 혜택 우선)
     */
    public List<WelfareBenefitResponse> getUrgentRecommendations(List<LifecycleEvent> events) {
        List<LifecycleEvent> urgentEvents = events.stream()
                .filter(event -> event.getEventType().requiresImmediateAction())
                .sorted((a, b) -> a.getEventDate().compareTo(b.getEventDate()))
                .collect(Collectors.toList());
        
        if (urgentEvents.isEmpty()) {
            return Collections.emptyList();
        }
        
        return getRecommendationsForMultipleEvents(urgentEvents);
    }
    
    /**
     * 생애주기 단계별 통계 정보
     */
    public Map<String, Object> getLifecycleStatistics(List<LifecycleEvent> events) {
        Map<String, Object> stats = new HashMap<>();
        
        // 이벤트 타입별 개수
        Map<LifecycleEventType, Long> eventTypeCounts = events.stream()
                .collect(Collectors.groupingBy(LifecycleEvent::getEventType, Collectors.counting()));
        
        // 카테고리별 개수
        long pregnancyEvents = events.stream().filter(e -> e.getEventType().isPregnancyRelated()).count();
        long childEvents = events.stream().filter(e -> e.getEventType().isChildRelated()).count();
        long educationEvents = events.stream().filter(e -> e.getEventType().isEducationRelated()).count();
        long healthEvents = events.stream().filter(e -> e.getEventType().isHealthRelated()).count();
        
        stats.put("totalEvents", events.size());
        stats.put("pregnancyEvents", pregnancyEvents);
        stats.put("childEvents", childEvents);
        stats.put("educationEvents", educationEvents);
        stats.put("healthEvents", healthEvents);
        stats.put("eventTypeCounts", eventTypeCounts);
        
        // 다가오는 이벤트 개수
        long upcomingEvents = events.stream().filter(LifecycleEvent::isUpcoming).count();
        stats.put("upcomingEvents", upcomingEvents);
        
        return stats;
    }
}
