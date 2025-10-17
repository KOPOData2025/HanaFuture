package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.config.WelfareApiProperties;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitDetailResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareSearchRequest;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.WelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.LocalWelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WelfareBenefitService {
    
    private final WelfareBenefitRepository welfareBenefitRepository;
    private final WelfareApiService welfareApiService;
    private final WelfareXmlParsingService xmlParsingService;
    private final WelfareDataEnrichmentService dataEnrichmentService;
    private final WelfareApiProperties welfareApiProperties;
    
    /**
     * 복지 혜택 검색
     */
    public Page<WelfareBenefitResponse> searchBenefits(WelfareSearchRequest request) {
        Pageable pageable = createPageable(request);
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
    
    /**
     * ID로 복지 혜택 조회
     */
    public WelfareBenefitResponse getBenefitById(Long benefitId) {
        WelfareBenefit benefit = welfareBenefitRepository.findById(benefitId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "해당 복지 혜택을 찾을 수 없습니다."));
        
        if (!benefit.getIsActive()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "비활성화된 복지 혜택입니다.");
        }
        
        return WelfareBenefitResponse.from(benefit);
    }
    
    /**
     * 복지 혜택 상세 조회
     */
    public WelfareBenefitDetailResponse getBenefitDetail(Long benefitId) {
        WelfareBenefit benefit = welfareBenefitRepository.findById(benefitId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "해당 복지 혜택을 찾을 수 없습니다."));
        
        if (!benefit.getIsActive()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "비활성화된 복지 혜택입니다.");
        }
        
        return WelfareBenefitDetailResponse.from(benefit);
    }
    
    /**
     * 즐겨찾기 토글
     */
    @Transactional
    public boolean toggleBookmark(Long benefitId) {
        log.info("복지 혜택 {} 즐겨찾기 토글", benefitId);
        return true;
    }
    
    /**
     * 지원금액 순으로 복지 혜택 조회
     */
    public Page<WelfareBenefitResponse> getBenefitsByAmount(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findBySupportAmountOrderByAmountDesc(pageable);
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 최근 업데이트된 복지 혜택 조회
     */
    public Page<WelfareBenefitResponse> getRecentBenefits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WelfareBenefit> benefits = welfareBenefitRepository.findRecentlyUpdated(pageable);
        return benefits.map(WelfareBenefitResponse::from);
    }
    
    /**
     * 중앙정부 복지서비스 동기화
     */
    @Transactional
    public void syncCentralWelfareServices() {
        // API가 비활성화된 경우 동기화 건너뛰기
        if (!welfareApiProperties.getEnabled()) {
            log.warn("복지 API가 비활성화되어 있어 중앙정부 복지서비스 동기화를 건너뜁니다. (정부 전산망 오류로 인한 임시 조치)");
            return;
        }
        
        log.info("중앙정부 복지서비스 동기화 시작");
        
        try {
            // 전체 데이터 동기화 (첫 3페이지)
            for (int page = 1; page <= 3; page++) {
                syncCentralPage(page, 100, null, null, null);
                Thread.sleep(1000); // API 호출 간격
            }
            
            // 육아 관련 특별 동기화
            syncCentralPage(1, 50, "001", null, null); // 영유아
            syncCentralPage(1, 50, "002", null, null); // 아동
            syncCentralPage(1, 50, "007", null, null); // 임신·출산
            
        } catch (Exception e) {
            log.error("중앙정부 복지서비스 동기화 실패", e);
        }
    }
    
    /**
     * 지자체 복지서비스 동기화
     */
    @Transactional
    public void syncLocalWelfareServices(String sidoName, String sigunguName) {
        // API가 비활성화된 경우 동기화 건너뛰기
        if (!welfareApiProperties.getEnabled()) {
            log.warn("복지 API가 비활성화되어 있어 지자체 복지서비스 동기화를 건너뜁니다. (정부 전산망 오류로 인한 임시 조치)");
            return;
        }
        
        log.info("지자체 복지서비스 동기화 시작 - 지역: {}", sidoName);
        
        try {
            // 지자체 데이터 동기화 (첫 2페이지)
            for (int page = 1; page <= 2; page++) {
                syncLocalPage(page, 50, sidoName, sigunguName);
                Thread.sleep(1000); // API 호출 간격
            }
            
        } catch (Exception e) {
            log.error("지자체 복지서비스 동기화 실패", e);
        }
    }
    
    /**
     * 중앙정부 페이지별 동기화
     */
    private void syncCentralPage(int pageNo, int numOfRows, String lifeArray, String intrsThemaArray, String searchWrd) {
        try {
            String xmlResponse = welfareApiService.getCentralWelfareServices(
                    pageNo, numOfRows, lifeArray, intrsThemaArray, searchWrd
            ).block();
            
            if (xmlResponse != null && !xmlResponse.trim().isEmpty()) {
                WelfareApiResponse apiResponse = xmlParsingService.parseXmlResponse(xmlResponse);
                
                if (xmlParsingService.isSuccessResponse(apiResponse)) {
                    List<WelfareBenefit> benefits = xmlParsingService.convertToEntities(apiResponse, WelfareType.CENTRAL);
                    
                    for (WelfareBenefit benefit : benefits) {
                        // 데이터 보강 후 저장
                        dataEnrichmentService.enrichWelfareBenefit(benefit);
                        saveOrUpdateBenefit(benefit);
                    }
                    
                    log.info("중앙정부 페이지 {} 동기화 완료: {}건", pageNo, benefits.size());
                }
            }
        } catch (Exception e) {
            log.error("중앙정부 페이지 {} 동기화 실패: {}", pageNo, e.getMessage());
        }
    }
    
    /**
     * 지자체 페이지별 동기화
     */
    private void syncLocalPage(int pageNo, int numOfRows, String sidoName, String sigunguName) {
        try {
            String xmlResponse = welfareApiService.getLocalWelfareServices(
                    sidoName, sigunguName, pageNo, numOfRows, null, null
            ).block();
            
            if (xmlResponse != null && !xmlResponse.trim().isEmpty()) {
                LocalWelfareApiResponse apiResponse = xmlParsingService.parseLocalXmlResponse(xmlResponse);
                
                if (xmlParsingService.isLocalSuccessResponse(apiResponse)) {
                    List<WelfareBenefit> benefits = xmlParsingService.convertLocalToEntities(apiResponse);
                    
                    for (WelfareBenefit benefit : benefits) {
                        // 데이터 보강 후 저장
                        dataEnrichmentService.enrichWelfareBenefit(benefit);
                        saveOrUpdateBenefit(benefit);
                    }
                    
                    log.info("지자체 {} 페이지 {} 동기화 완료: {}건", sidoName, pageNo, benefits.size());
                }
            }
        } catch (Exception e) {
            log.error("지자체 {} 페이지 {} 동기화 실패: {}", sidoName, pageNo, e.getMessage());
        }
    }
    
    /**
     * 복지 혜택 저장 또는 업데이트
     */
    @Transactional
    public void saveOrUpdateBenefit(WelfareBenefit benefit) {
        try {
            Optional<WelfareBenefit> existingBenefit = welfareBenefitRepository.findByServiceId(benefit.getServiceId());
            
            if (existingBenefit.isPresent()) {
                // 기존 데이터 업데이트
                WelfareBenefit existing = existingBenefit.get();
                existing.updateFromApi(
                        benefit.getServiceName(),
                        benefit.getLifeCycle(),
                        benefit.getCategory(),
                        benefit.getJurisdictionName(),
                        benefit.getTargetDescription(),
                        benefit.getServiceContent(),
                        benefit.getApplicationMethod(),
                        benefit.getInquiryUrl(),
                        benefit.getSupportCycle(),
                        benefit.getServiceProvisionType(),
                        benefit.getOnlineApplicationPossible(),
                        benefit.getServiceFirstRegistrationDate()
                );
                
                if (benefit.getSupportAmount() != null) {
                    existing.updateSupportAmount(benefit.getSupportAmount(), benefit.getSupportAmountDescription());
                }
                
                log.debug("복지 혜택 업데이트: {}", existing.getServiceName());
            } else {
                // 새로운 데이터 저장
                welfareBenefitRepository.save(benefit);
                log.debug("복지 혜택 신규 저장: {}", benefit.getServiceName());
            }
        } catch (Exception e) {
            log.error("복지 혜택 저장 실패: {}", e.getMessage());
        }
    }
    
    /**
     * 기존 데이터의 NULL 값 보강 (배치 작업)
     */
    @Transactional
    public void enrichExistingData() {
        log.info("기존 복지 혜택 데이터 보강 시작");
        
        try {
            // NULL 값이 많은 데이터들을 배치로 처리
            int pageSize = 100;
            int page = 0;
            
            while (true) {
                Pageable pageable = PageRequest.of(page, pageSize);
                Page<WelfareBenefit> benefitsPage = welfareBenefitRepository.findAll(pageable);
                
                if (benefitsPage.isEmpty()) {
                    break;
                }
                
                for (WelfareBenefit benefit : benefitsPage.getContent()) {
                    // 데이터 품질 점수 확인
                    int qualityScore = dataEnrichmentService.calculateDataQualityScore(benefit);
                    
                    // 품질 점수가 70% 미만인 경우 보강
                    if (qualityScore < 70) {
                        log.debug("데이터 보강 대상: {} (품질점수: {}%)", 
                                benefit.getServiceName(), qualityScore);
                        
                        dataEnrichmentService.enrichWelfareBenefit(benefit);
                        welfareBenefitRepository.save(benefit);
                    }
                }
                
                log.info("페이지 {} 보강 완료 ({}/{})", page + 1, 
                        benefitsPage.getNumberOfElements(), benefitsPage.getTotalElements());
                
                if (benefitsPage.isLast()) {
                    break;
                }
                
                page++;
                
                // CPU 부하 방지를 위한 잠시 대기
                Thread.sleep(100);
            }
            
            log.info("전체 복지 혜택 데이터 보강 완료");
            
        } catch (Exception e) {
            log.error("데이터 보강 배치 작업 실패", e);
        }
    }
    
    /**
     * Pageable 객체 생성
     */
    private Pageable createPageable(WelfareSearchRequest request) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                request.getSort()
        );
        
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
}