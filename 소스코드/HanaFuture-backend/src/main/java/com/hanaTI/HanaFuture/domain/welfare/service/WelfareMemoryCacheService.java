package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 하나퓨처 복지 혜택 메모리 캐싱 서비스
 * 하나퓨처 맞춤 복지 혜택을 메모리에 캐싱
 * - DB 조회 없이 빠른 응답 제공 (부하 테스트용)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareMemoryCacheService {
    
    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;
    
    // 메모리에 캐싱된 하나퓨처 복지 혜택 데이터
    private List<HanaFutureWelfareBenefit> cachedBenefits;
    
    /**
     * 애플리케이션 시작 시 하나퓨처 복지 혜택 데이터를 메모리에 로드
     */
    @PostConstruct
    public void loadCache() {
        log.info("하나퓨처 복지 혜택 데이터를 메모리에 로드 중...");
        
        // AI 관련도 점수 순으로 정렬된 전체 데이터 로드
        this.cachedBenefits = hanaFutureRepository
                .findByIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(PageRequest.of(0, 200))
                .getContent();
        
        log.info("하나퓨처 복지 혜택 {}개를 메모리에 로드했습니다.", cachedBenefits.size());
    }
    
    /**
     * 빠른 페이징 조회 (DB 조회 없음, 메모리에서만 처리)
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 복지 혜택 목록
     */
    public Page<WelfareBenefitResponse> getFastPaging(int page, int size) {
        log.debug("빠른 페이징 조회 - page: {}, size: {}", page, size);
        
        // 페이징 계산
        int start = page * size;
        int end = Math.min(start + size, cachedBenefits.size());
        
        // 범위 체크
        if (start >= cachedBenefits.size()) {
            return Page.empty(PageRequest.of(page, size));
        }
        
        // 메모리에서 서브리스트 추출
        List<HanaFutureWelfareBenefit> pagedBenefits = cachedBenefits.subList(start, end);
        
        // DTO 변환
        List<WelfareBenefitResponse> responses = pagedBenefits.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Page 객체 생성
        PageRequest pageable = PageRequest.of(page, size);
        return new PageImpl<>(responses, pageable, cachedBenefits.size());
    }
    
    /**
     * HanaFutureWelfareBenefit를 WelfareBenefitResponse로 변환
     */
    private WelfareBenefitResponse convertToResponse(HanaFutureWelfareBenefit benefit) {
        // serviceType 문자열을 WelfareType enum으로 변환
        com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType welfareType = null;
        if (benefit.getServiceType() != null) {
            try {
                welfareType = com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType.valueOf(benefit.getServiceType());
            } catch (IllegalArgumentException e) {
                log.warn("유효하지 않은 serviceType: {}", benefit.getServiceType());
            }
        }
        
        return WelfareBenefitResponse.builder()
                .id(benefit.getId())
                .serviceId(benefit.getServiceId())
                .serviceName(benefit.getServiceName())
                .serviceType(welfareType)
                .serviceTypeDisplayName(welfareType != null ? welfareType.getDisplayName() : null)
                .lifeCycle(benefit.getLifeCycle())
                .category(benefit.getCategory())
                .supportAmount(benefit.getSupportAmount())
                .targetDescription(benefit.getTargetDescription())
                .serviceContent(benefit.getServiceContent())
                .applicationMethod(benefit.getApplicationMethod())
                .sidoName(benefit.getSidoName())
                .sigunguName(benefit.getSigunguName())
                .inquiryUrl(benefit.getRelatedUrl())
                .build();
    }
    
    /**
     * 캐시된 데이터 수 조회
     */
    public int getCachedCount() {
        return cachedBenefits != null ? cachedBenefits.size() : 0;
    }
    
    /**
     * 캐시 새로고침 (필요 시 수동 호출)
     */
    public void refreshCache() {
        log.info("캐시 새로고침 시작...");
        loadCache();
    }
}

