package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareSearchRequest;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.WelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DB 저장 없이 실시간 API 호출만 하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectWelfareService {
    
    private final WelfareApiService welfareApiService;
    private final WelfareXmlParsingService xmlParsingService;
    
    /**
     * 실시간 복지 혜택 검색 (DB 저장 없음)
     */
    public Page<WelfareBenefitResponse> searchBenefitsDirectly(WelfareSearchRequest request) {
        log.info("실시간 복지 혜택 검색 - 조건: {}", request);
        
        try {
            // 중앙정부 API 호출
            String xmlResponse = welfareApiService.getCentralWelfareServices(
                    request.getPage() + 1, // API는 1부터 시작
                    request.getSize(),
                    mapLifeCycleToCode(request.getLifeCycle()),
                    mapCategoryToCode(request.getCategory()),
                    request.getKeyword()
            ).block();
            
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                return Page.empty();
            }
            
            // XML 파싱
            WelfareApiResponse apiResponse = xmlParsingService.parseXmlResponse(xmlResponse);
            
            if (!xmlParsingService.isSuccessResponse(apiResponse)) {
                log.warn("API 응답 실패: {}", apiResponse.getResultMessage());
                return Page.empty();
            }
            
            // Entity로 변환하지 않고 바로 Response로 변환
            List<WelfareBenefitResponse> benefits = xmlParsingService
                    .convertToEntities(apiResponse, WelfareType.CENTRAL)
                    .stream()
                    .map(WelfareBenefitResponse::from)
                    .collect(Collectors.toList());
            
            // Page 객체 생성
            PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
            long totalElements = apiResponse.getTotalCount() != null ? 
                    apiResponse.getTotalCount().longValue() : benefits.size();
            
            return new PageImpl<>(benefits, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("실시간 복지 혜택 검색 실패", e);
            return Page.empty();
        }
    }
    
    /**
     * 생애주기를 API 코드로 매핑
     */
    private String mapLifeCycleToCode(String lifeCycle) {
        if (lifeCycle == null) return null;
        
        // 생애주기 코드 매핑
        switch (lifeCycle) {
            case "영유아": return "001";
            case "아동": return "002";
            case "청소년": return "003";
            case "청년": return "004";
            case "중장년": return "005";
            case "노년": return "006";
            case "임신출산":
            case "임신·출산": return "007";
            default: return null;
        }
    }
    
    /**
     * 관심주제를 API 코드로 매핑
     */
    private String mapCategoryToCode(String category) {
        if (category == null) return null;
        
        // 관심주제 코드 매핑
        switch (category) {
            case "신체건강": return "010";
            case "정신건강": return "020";
            case "생활지원": return "030";
            case "주거": return "040";
            case "일자리": return "050";
            case "문화여가": return "060";
            case "안전위기": return "070";
            case "임신출산": return "080";
            case "보육": return "090";
            case "교육": return "100";
            default: return null;
        }
    }
}
