package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.dto.external.WelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 복지 데이터 동기화 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleWelfareSyncService {
    
    private final WelfareBenefitRepository welfareBenefitRepository;
    private final WelfareApiService welfareApiService;
    private final WelfareXmlParsingService xmlParsingService;
    
    /**
     * 중앙정부 복지서비스 동기화
     */
    @Transactional
    public void syncCentralWelfareData() {
        log.info("중앙정부 복지서비스 동기화 시작");
        
        try {
            // 육아 관련 생애주기별 동기화
            syncLifeCycleData("001"); // 영유아
            syncLifeCycleData("002"); // 아동  
            syncLifeCycleData("007"); // 임신·출산
            
            log.info("중앙정부 복지서비스 동기화 완료");
            
        } catch (Exception e) {
            log.error("중앙정부 복지서비스 동기화 실패", e);
        }
    }
    
    /**
     * 생애주기별 데이터 동기화
     */
    private void syncLifeCycleData(String lifeCycle) {
        try {
            log.info("생애주기 {} 데이터 동기화 시작", lifeCycle);
            
            String xmlResponse = welfareApiService.getCentralWelfareServices(
                    1, 50, lifeCycle, null, null
            ).block();
            
            if (xmlResponse != null && !xmlResponse.trim().isEmpty()) {
                WelfareApiResponse apiResponse = xmlParsingService.parseXmlResponse(xmlResponse);
                
                if (xmlParsingService.isSuccessResponse(apiResponse)) {
                    List<WelfareBenefit> benefits = xmlParsingService.convertToEntities(apiResponse, WelfareType.CENTRAL);
                    
                    for (WelfareBenefit benefit : benefits) {
                        saveOrUpdateBenefit(benefit);
                    }
                    
                    log.info("생애주기 {} 데이터 {}건 동기화 완료", lifeCycle, benefits.size());
                }
            }
            
        } catch (Exception e) {
            log.error("생애주기 {} 데이터 동기화 실패: {}", lifeCycle, e.getMessage());
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
}
