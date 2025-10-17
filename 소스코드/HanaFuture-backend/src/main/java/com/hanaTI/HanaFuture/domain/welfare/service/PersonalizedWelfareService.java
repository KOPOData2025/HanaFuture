package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.entity.IncomeLevel;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareSearchRequest;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;

/**
 * 개인 맞춤 복지 혜택 추천 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalizedWelfareService {
    
    private final UserRepository userRepository;
    private final WelfareBenefitRepository welfareBenefitRepository;
    
    /**
     * 사용자 맞춤 복지 혜택 추천 (스마트 추천)
     */
    public Page<WelfareBenefitResponse> getPersonalizedRecommendations(String userEmail, int page, int size) {
        log.info("사용자 맞춤 복지 혜택 추천 - 이메일: {}", userEmail);
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 다중 추천 전략 적용
        List<WelfareBenefit> recommendedBenefits = new ArrayList<>();
        
        // 1. 우선순위 높은 혜택들 (임신, 자녀 관련)
        recommendedBenefits.addAll(getHighPriorityBenefits(user));
        
        // 2. 소득 수준 기반 혜택
        recommendedBenefits.addAll(getIncomeBasedBenefits(user));
        
        // 3. 거주지 기반 지자체 혜택 (우선순위 높음)
        List<WelfareBenefit> regionalBenefits = getRegionalBenefits(user);
        recommendedBenefits.addAll(regionalBenefits);
        
        // 4. 특수 상황 기반 혜택
        recommendedBenefits.addAll(getSpecialSituationBenefits(user));
        
        // 5. 관심 분야 기반 혜택
        recommendedBenefits.addAll(getInterestBasedBenefits(user));
        
        // 중복 제거 및 정렬 (지역 혜택 우선순위)
        List<WelfareBenefit> uniqueBenefits = recommendedBenefits.stream()
                .distinct()
                .sorted((a, b) -> {
                    // 1. 지역 혜택 우선 (사용자 거주지와 매칭되는 혜택)
                    boolean aIsRegional = isUserRegionalBenefit(a, user);
                    boolean bIsRegional = isUserRegionalBenefit(b, user);
                    if (aIsRegional && !bIsRegional) return -1;
                    if (!aIsRegional && bIsRegional) return 1;
                    
                    // 2. 지원금액이 있는 것 우선
                    if (a.getSupportAmount() != null && b.getSupportAmount() == null) return -1;
                    if (a.getSupportAmount() == null && b.getSupportAmount() != null) return 1;
                    
                    // 3. 지원금액 높은 순
                    if (a.getSupportAmount() != null && b.getSupportAmount() != null) {
                        return b.getSupportAmount().compareTo(a.getSupportAmount());
                    }
                    
                    // 4. 최근 업데이트 순
                    return b.getLastSyncedAt().compareTo(a.getLastSyncedAt());
                })
                .collect(Collectors.toList());
        
        // 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, uniqueBenefits.size());
        List<WelfareBenefit> pagedBenefits = uniqueBenefits.subList(start, end);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefitPage = new PageImpl<>(pagedBenefits, pageable, uniqueBenefits.size());
        
        log.info("사용자 {}에게 {}개 혜택 추천 (총 {}개 중)", userEmail, pagedBenefits.size(), uniqueBenefits.size());
        
        return benefitPage.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 사용자 정보 기반 검색 조건 생성
     */
    private WelfareSearchRequest createPersonalizedSearchRequest(User user, int page, int size) {
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSort("supportAmount");
        request.setDirection("desc");
        
        // 생애주기 기반 필터링
        List<String> lifeCycles = new ArrayList<>();
        
        if (Boolean.TRUE.equals(user.getIsPregnant())) {
            lifeCycles.add("임신");
            lifeCycles.add("출산");
        }
        
        if (user.hasAnyChildren()) {
            Integer age = user.getAge();
            if (age != null) {
                if (age <= 35) {
                    lifeCycles.add("영유아");
                    lifeCycles.add("아동");
                }
                if (age <= 45) {
                    lifeCycles.add("청소년");
                }
            }
        }
        
        if (!lifeCycles.isEmpty()) {
            request.setLifeCycle(String.join(",", lifeCycles));
        }
        
        // 소득 수준 기반 키워드 추가
        List<String> keywords = new ArrayList<>();
        
        if (user.getIncomeLevel() == IncomeLevel.BASIC_LIVELIHOOD) {
            keywords.add("기초생활");
            keywords.add("생계급여");
        }
        
        if (user.getIncomeLevel() == IncomeLevel.LOW_INCOME) {
            keywords.add("차상위");
            keywords.add("저소득");
        }
        
        if (Boolean.TRUE.equals(user.getHasDisability())) {
            keywords.add("장애인");
        }
        
        if (Boolean.TRUE.equals(user.getIsSingleParent())) {
            keywords.add("한부모");
            keywords.add("양육");
        }
        
        if (Boolean.TRUE.equals(user.getIsMulticultural())) {
            keywords.add("다문화");
        }
        
        if (user.getNumberOfChildren() != null && user.getNumberOfChildren() >= 3) {
            keywords.add("다자녀");
        }
        
        if (!keywords.isEmpty()) {
            request.setKeyword(String.join(" ", keywords));
        }
        
        return request;
    }
    
    /**
     * 생애주기별 추천 혜택
     */
    public Page<WelfareBenefitResponse> getLifeCycleRecommendations(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        String lifeCycle = determineUserLifeCycle(user);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findByLifeCycleContaining(lifeCycle, pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 사용자 상황에 맞는 생애주기 결정
     */
    private String determineUserLifeCycle(User user) {
        if (Boolean.TRUE.equals(user.getIsPregnant())) {
            return "임신";
        }
        
        if (user.hasAnyChildren()) {
            Integer age = user.getAge();
            if (age != null && age <= 35) {
                return "영유아";
            }
            return "아동";
        }
        
        Integer age = user.getAge();
        if (age == null) return "청년"; // 기본값
        
        if (age <= 30) return "청년";
        if (age <= 50) return "중장년";
        return "노년";
    }
    
    /**
     * 지역 기반 복지 혜택 추천
     */
    public Page<WelfareBenefitResponse> getRegionalRecommendations(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "supportAmount"));
        
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findByServiceTypeAndSidoNameAndIsActiveTrue(
                com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType.LOCAL,
                user.getResidenceSido(),
                pageable
        );
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 우선순위 높은 혜택들 (임신, 자녀 관련)
     */
    private List<WelfareBenefit> getHighPriorityBenefits(User user) {
        List<WelfareBenefit> benefits = new ArrayList<>();
        
        // 임신 중인 경우 - 최우선
        if (Boolean.TRUE.equals(user.getIsPregnant())) {
            benefits.addAll(welfareBenefitRepository.searchByKeyword("임신", PageRequest.of(0, 20)).getContent());
            benefits.addAll(welfareBenefitRepository.searchByKeyword("출산", PageRequest.of(0, 20)).getContent());
            benefits.addAll(welfareBenefitRepository.searchByKeyword("산모", PageRequest.of(0, 10)).getContent());
        }
        
        // 자녀가 있는 경우
        if (user.hasAnyChildren()) {
            Integer age = user.getAge();
            
            // 영유아 관련 혜택
            if (age != null && age <= 35) {
                benefits.addAll(welfareBenefitRepository.findByLifeCycleContaining("영유아", PageRequest.of(0, 15)).getContent());
            }
            
            // 아동 관련 혜택
            benefits.addAll(welfareBenefitRepository.findByLifeCycleContaining("아동", PageRequest.of(0, 15)).getContent());
            benefits.addAll(welfareBenefitRepository.searchByKeyword("아이돌봄", PageRequest.of(0, 10)).getContent());
            benefits.addAll(welfareBenefitRepository.searchByKeyword("보육", PageRequest.of(0, 10)).getContent());
            
            // 다자녀 가정 혜택
            if (user.getNumberOfChildren() != null && user.getNumberOfChildren() >= 3) {
                benefits.addAll(welfareBenefitRepository.searchByKeyword("다자녀", PageRequest.of(0, 10)).getContent());
            }
        }
        
        return benefits;
    }
    
    /**
     * 소득 수준 기반 혜택
     */
    private List<WelfareBenefit> getIncomeBasedBenefits(User user) {
        List<WelfareBenefit> benefits = new ArrayList<>();
        
        if (user.getIncomeLevel() != null) {
            switch (user.getIncomeLevel()) {
                case BASIC_LIVELIHOOD:
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("기초생활", PageRequest.of(0, 15)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("생계급여", PageRequest.of(0, 10)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("의료급여", PageRequest.of(0, 10)).getContent());
                    break;
                    
                case LOW_INCOME:
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("차상위", PageRequest.of(0, 10)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("저소득", PageRequest.of(0, 15)).getContent());
                    break;
                    
                case MIDDLE_LOW:
                case MIDDLE:
                case HIGH:
                    // 중간 소득층 이상은 보편적 혜택 위주
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("아동수당", PageRequest.of(0, 10)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("부모급여", PageRequest.of(0, 10)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("육아휴직", PageRequest.of(0, 5)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("출산지원", PageRequest.of(0, 5)).getContent());
                    
                    // 저소득층 전용 혜택은 제외
                    benefits = benefits.stream()
                            .filter(benefit -> !isLowIncomeOnlyBenefit(benefit))
                            .collect(Collectors.toList());
                    break;
                    
                default:
                    // 일반 소득층도 받을 수 있는 보편적 혜택
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("아동수당", PageRequest.of(0, 5)).getContent());
                    benefits.addAll(welfareBenefitRepository.searchByKeyword("부모급여", PageRequest.of(0, 5)).getContent());
                    break;
            }
        }
        
        return benefits;
    }
    
    /**
     * 저소득층 전용 혜택인지 확인
     */
    private boolean isLowIncomeOnlyBenefit(WelfareBenefit benefit) {
        String serviceName = benefit.getServiceName().toLowerCase();
        String targetDescription = benefit.getTargetDescription() != null ? 
                benefit.getTargetDescription().toLowerCase() : "";
        
        return serviceName.contains("저소득") || 
               serviceName.contains("기초생활") ||
               serviceName.contains("차상위") ||
               serviceName.contains("생계급여") ||
               serviceName.contains("의료급여") ||
               targetDescription.contains("저소득") ||
               targetDescription.contains("기초생활") ||
               targetDescription.contains("차상위");
    }
    
    /**
     * 거주지 기반 지자체 혜택 (엄격한 지역 필터링)
     */
    private List<WelfareBenefit> getRegionalBenefits(User user) {
        List<WelfareBenefit> benefits = new ArrayList<>();
        
        if (user.getResidenceSido() != null) {
            log.info("사용자 거주지 기반 혜택 검색 - 시도: {}, 시군구: {}", 
                    user.getResidenceSido(), user.getResidenceSigungu());
            
            // 1. 정확한 시/도 매칭 혜택만 가져오기
            Page<WelfareBenefit> sidoBenefits = welfareBenefitRepository.findByServiceTypeAndSidoNameAndIsActiveTrue(
                    com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType.LOCAL,
                    user.getResidenceSido(),
                    PageRequest.of(0, 30)
            );
            
            List<WelfareBenefit> filteredSidoBenefits = sidoBenefits.getContent().stream()
                    .filter(benefit -> isMatchingRegion(benefit, user.getResidenceSido(), user.getResidenceSigungu()))
                    .collect(Collectors.toList());
            
            benefits.addAll(filteredSidoBenefits);
            
            // 2. 구/군 단위 혜택 (정확한 매칭만)
            if (user.getResidenceSigungu() != null) {
                Page<WelfareBenefit> sigunguBenefits = welfareBenefitRepository.findBySigunguNameContainingAndIsActiveTrue(
                        user.getResidenceSigungu(), 
                        PageRequest.of(0, 20)
                );
                
                List<WelfareBenefit> filteredSigunguBenefits = sigunguBenefits.getContent().stream()
                        .filter(benefit -> isMatchingRegion(benefit, user.getResidenceSido(), user.getResidenceSigungu()))
                        .collect(Collectors.toList());
                
                benefits.addAll(filteredSigunguBenefits);
            }
            
            log.info("거주지 기반 필터링 완료 - 총 {}개 혜택", benefits.size());
        }
        
        return benefits;
    }
    
    /**
     * 혜택이 사용자의 거주지와 정확히 매칭되는지 확인
     */
    private boolean isMatchingRegion(WelfareBenefit benefit, String userSido, String userSigungu) {
        // 시/도가 다르면 제외
        if (benefit.getSidoName() != null && !benefit.getSidoName().equals(userSido)) {
            return false;
        }
        
        // 시/군/구가 있는 경우 정확히 매칭되는지 확인
        if (userSigungu != null && benefit.getSigunguName() != null) {
            return benefit.getSigunguName().contains(userSigungu) || userSigungu.contains(benefit.getSigunguName());
        }
        
        // 다른 지역명이 포함되어 있으면 제외
        if (benefit.getServiceName() != null) {
            String serviceName = benefit.getServiceName();
            // 부산, 대구, 인천 등 다른 지역명이 포함된 경우 제외
            if (serviceName.contains("부산") && !"부산광역시".equals(userSido)) return false;
            if (serviceName.contains("대구") && !"대구광역시".equals(userSido)) return false;
            if (serviceName.contains("인천") && !"인천광역시".equals(userSido)) return false;
            if (serviceName.contains("광주") && !"광주광역시".equals(userSido)) return false;
            if (serviceName.contains("대전") && !"대전광역시".equals(userSido)) return false;
            if (serviceName.contains("울산") && !"울산광역시".equals(userSido)) return false;
            if (serviceName.contains("세종") && !"세종특별자치시".equals(userSido)) return false;
        }
        
        return true;
    }
    
    /**
     * 사용자의 거주지와 매칭되는 지역 혜택인지 확인
     */
    private boolean isUserRegionalBenefit(WelfareBenefit benefit, User user) {
        if (user.getResidenceSido() == null) return false;
        
        // 지자체 혜택이고 사용자 거주지와 매칭되는지 확인
        return benefit.getServiceType() == com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType.LOCAL &&
               isMatchingRegion(benefit, user.getResidenceSido(), user.getResidenceSigungu());
    }
    
    /**
     * 관심 분야 기반 혜택
     */
    private List<WelfareBenefit> getInterestBasedBenefits(User user) {
        List<WelfareBenefit> benefits = new ArrayList<>();
        
        if (user.getInterestCategories() != null && !user.getInterestCategories().trim().isEmpty()) {
            String[] interests = user.getInterestCategories().split(",");
            
            for (String interest : interests) {
                String trimmedInterest = interest.trim();
                benefits.addAll(welfareBenefitRepository.searchByKeyword(trimmedInterest, PageRequest.of(0, 8)).getContent());
            }
        }
        
        return benefits;
    }
    
    /**
     * 특수 상황 기반 혜택
     */
    private List<WelfareBenefit> getSpecialSituationBenefits(User user) {
        List<WelfareBenefit> benefits = new ArrayList<>();
        
        // 한부모 가정
        if (Boolean.TRUE.equals(user.getIsSingleParent())) {
            benefits.addAll(welfareBenefitRepository.searchByKeyword("한부모", PageRequest.of(0, 15)).getContent());
            benefits.addAll(welfareBenefitRepository.searchByKeyword("양육", PageRequest.of(0, 10)).getContent());
        }
        
        // 다문화 가정
        if (Boolean.TRUE.equals(user.getIsMulticultural())) {
            benefits.addAll(welfareBenefitRepository.searchByKeyword("다문화", PageRequest.of(0, 10)).getContent());
        }
        
        // 장애인 가정
        if (Boolean.TRUE.equals(user.getHasDisability())) {
            benefits.addAll(welfareBenefitRepository.searchByKeyword("장애인", PageRequest.of(0, 15)).getContent());
        }
        
        return benefits;
    }
}
