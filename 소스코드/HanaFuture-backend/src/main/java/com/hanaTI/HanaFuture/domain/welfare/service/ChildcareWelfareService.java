package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareSearchRequest;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * HanaFuture 서비스에 특화된 육아 관련 복지서비스 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildcareWelfareService {
    
    private final WelfareBenefitRepository welfareBenefitRepository;
    
    // 육아 관련 키워드들
    private static final List<String> CHILDCARE_KEYWORDS = Arrays.asList(
        "임신", "출산", "육아", "보육", "영유아", "아동", "유치원", "어린이집", 
        "부모급여", "양육", "자녀", "신생아", "산모", "모성", "교육", "돌봄"
    );
    
    /**
     * 육아 관련 복지서비스만 조회
     */
    public Page<WelfareBenefitResponse> getChildcareWelfareBenefits(int page, int size) {
        log.info("육아 관련 복지서비스 조회 - 페이지: {}, 크기: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // 생애주기가 영유아, 아동, 임신·출산인 서비스들
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findByLifeCycleContaining("영유아", pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 육아 키워드로 복지서비스 검색
     */
    public Page<WelfareBenefitResponse> searchChildcareWelfare(String keyword, int page, int size) {
        log.info("육아 관련 키워드 검색 - 키워드: {}, 페이지: {}, 크기: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "supportAmount"));
        
        // 키워드 검색
        Page<WelfareBenefit> benefits = welfareBenefitRepository.searchByKeyword(keyword, pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 임신·출산 관련 복지서비스
     */
    public Page<WelfareBenefitResponse> getPregnancyBenefits(int page, int size) {
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setLifeCycle("임신");
        request.setPage(page);
        request.setSize(size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findByLifeCycleContaining("임신", pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 영유아 관련 복지서비스
     */
    public Page<WelfareBenefitResponse> getInfantBenefits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findByLifeCycleContaining("영유아", pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 보육 관련 복지서비스
     */
    public Page<WelfareBenefitResponse> getChildcareBenefits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.searchByKeyword("보육", pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 교육 관련 복지서비스
     */
    public Page<WelfareBenefitResponse> getEducationBenefits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.searchByKeyword("교육", pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 지원금액이 높은 육아 복지서비스 (Top 10)
     */
    public Page<WelfareBenefitResponse> getTopSupportAmountBenefits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findBySupportAmountOrderByAmountDesc(pageable);
        
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 사용자 맞춤 복지서비스 추천
     * 
     * @param userAge 사용자 나이
     * @param hasChild 자녀 유무
     * @param isPregnant 임신 여부
     */
    public Page<WelfareBenefitResponse> getRecommendedBenefits(Integer userAge, Boolean hasChild, 
                                                              Boolean isPregnant, int page, int size) {
        log.info("사용자 맞춤 복지서비스 추천 - 나이: {}, 자녀유무: {}, 임신여부: {}", userAge, hasChild, isPregnant);
        
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSort("supportAmount");
        request.setDirection("desc");
        
        // 임신 중이면 임신·출산 관련 서비스
        if (Boolean.TRUE.equals(isPregnant)) {
            request.setLifeCycle("임신");
        }
        // 자녀가 있으면 영유아/아동 관련 서비스
        else if (Boolean.TRUE.equals(hasChild)) {
            request.setLifeCycle("영유아");
        }
        // 나이대별 추천
        else if (userAge != null && userAge >= 20 && userAge <= 40) {
            request.setLifeCycle("청년");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "supportAmount"));
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findByComplexConditions(
                request.getServiceType(),
                request.getSidoName(),
                request.getSigunguName(),
                request.getLifeCycle(),
                request.getCategory(),
                request.getKeyword(),
                pageable
        );
        
        return benefits.map(WelfareBenefitResponse::from);
    }
}
