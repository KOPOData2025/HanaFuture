package com.hanaTI.HanaFuture.domain.welfare.service;

import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareDataEnrichmentService {
    
    // 카테고리 매핑 (키워드 기반)
    private static final Map<String, String> CATEGORY_KEYWORDS = Map.of(
        "출산", "출산·보육·돌봄",
        "양육", "출산·보육·돌봄", 
        "육아", "출산·보육·돌봄",
        "보육", "출산·보육·돌봄",
        "교육", "교육",
        "학비", "교육",
        "학자금", "교육",
        "주거", "주거",
        "임대", "주거",
        "전세", "주거"
    );
    
    // 신청방법 키워드
    private static final Map<String, String> APPLICATION_METHOD_KEYWORDS = Map.of(
        "온라인", "온라인 신청",
        "인터넷", "온라인 신청",
        "방문", "방문 신청",
        "우편", "우편 신청",
        "전화", "전화 신청"
    );
    
    // 지원금액 패턴들
    private static final Pattern[] AMOUNT_PATTERNS = {
        Pattern.compile("(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*만\\s*원"),
        Pattern.compile("(\\d{1,3}(?:,\\d{3})*)\\s*원"),
        Pattern.compile("월\\s*(\\d{1,3}(?:,\\d{3})*)\\s*원"),
        Pattern.compile("연\\s*(\\d{1,3}(?:,\\d{3})*)\\s*만\\s*원"),
        Pattern.compile("최대\\s*(\\d{1,3}(?:,\\d{3})*)\\s*만\\s*원")
    };
    
    /**
     * 복지 혜택 데이터 보강
     */
    public void enrichWelfareBenefit(WelfareBenefit benefit) {
        try {
            // 1. 카테고리 추론
            enrichCategory(benefit);
            
            // 2. 지원금액 재추출 (더 정교한 방식)
            enrichSupportAmount(benefit);
            
            // 3. 신청방법 추론
            enrichApplicationMethod(benefit);
            
            // 4. 기본값 설정
            setDefaultValues(benefit);
            
            log.debug("데이터 보강 완료: {}", benefit.getServiceName());
            
        } catch (Exception e) {
            log.warn("데이터 보강 실패 - 서비스: {}, 오류: {}", 
                    benefit.getServiceName(), e.getMessage());
        }
    }
    
    /**
     * 카테고리 추론 (서비스명, 내용 기반)
     */
    private void enrichCategory(WelfareBenefit benefit) {
        if (benefit.getCategory() != null && !benefit.getCategory().trim().isEmpty()) {
            return; // 이미 카테고리가 있으면 스킵
        }
        
        String searchText = (benefit.getServiceName() + " " + 
                           (benefit.getServiceContent() != null ? benefit.getServiceContent() : "")).toLowerCase();
        
        for (Map.Entry<String, String> entry : CATEGORY_KEYWORDS.entrySet()) {
            if (searchText.contains(entry.getKey())) {
                benefit.setCategory(entry.getValue());
                log.debug("카테고리 추론: {} -> {}", benefit.getServiceName(), entry.getValue());
                return;
            }
        }
        
        // 기본 카테고리 설정
        benefit.setCategory("기타");
    }
    
    /**
     * 지원금액 재추출 (더 정교한 패턴)
     */
    private void enrichSupportAmount(WelfareBenefit benefit) {
        if (benefit.getSupportAmount() != null && benefit.getSupportAmount() > 0) {
            return; // 이미 금액이 있으면 스킵
        }
        
        String content = benefit.getServiceContent();
        if (content == null) return;
        
        for (Pattern pattern : AMOUNT_PATTERNS) {
            var matcher = pattern.matcher(content);
            if (matcher.find()) {
                try {
                    String amountStr = matcher.group(1).replace(",", "");
                    long amount = Long.parseLong(amountStr);
                    
                    // 만원 단위 처리
                    if (pattern.pattern().contains("만")) {
                        amount *= 10000;
                    }
                    
                    benefit.setSupportAmount(amount);
                    benefit.setSupportAmountDescription(matcher.group(0));
                    
                    log.debug("지원금액 재추출: {} -> {}원", benefit.getServiceName(), amount);
                    return;
                } catch (NumberFormatException e) {
                    log.debug("금액 파싱 실패: {}", matcher.group(1));
                }
            }
        }
        
        // 금액을 찾지 못한 경우 기본 설명 설정
        if (content.contains("지원") || content.contains("혜택")) {
            benefit.setSupportAmountDescription("지원 내용 확인 필요");
        }
    }
    
    /**
     * 신청방법 추론
     */
    private void enrichApplicationMethod(WelfareBenefit benefit) {
        if (benefit.getApplicationMethod() != null && !benefit.getApplicationMethod().trim().isEmpty()) {
            return; // 이미 있으면 스킵
        }
        
        String content = (benefit.getServiceContent() != null ? benefit.getServiceContent() : "").toLowerCase();
        
        for (Map.Entry<String, String> entry : APPLICATION_METHOD_KEYWORDS.entrySet()) {
            if (content.contains(entry.getKey())) {
                benefit.setApplicationMethod(entry.getValue());
                log.debug("신청방법 추론: {} -> {}", benefit.getServiceName(), entry.getValue());
                return;
            }
        }
        
        // 온라인 신청 가능 여부 체크
        if ("Y".equals(benefit.getOnlineApplicationPossible())) {
            benefit.setApplicationMethod("온라인 신청 가능");
        } else {
            benefit.setApplicationMethod("담당기관 문의");
        }
    }
    
    /**
     * 기본값 설정
     */
    private void setDefaultValues(WelfareBenefit benefit) {
        // 지원주기가 없으면 기본값 설정
        if (benefit.getSupportCycle() == null || benefit.getSupportCycle().trim().isEmpty()) {
            benefit.setSupportCycle("상시");
        }
        
        // 서비스 제공 형태가 없으면 기본값 설정
        if (benefit.getServiceProvisionType() == null || benefit.getServiceProvisionType().trim().isEmpty()) {
            benefit.setServiceProvisionType("현금/현물");
        }
    }
    
    /**
     * 데이터 품질 점수 계산 (0-100)
     */
    public int calculateDataQualityScore(WelfareBenefit benefit) {
        int score = 0;
        int totalFields = 10;
        
        // 필수 필드 체크
        if (benefit.getServiceName() != null && !benefit.getServiceName().trim().isEmpty()) score++;
        if (benefit.getServiceContent() != null && !benefit.getServiceContent().trim().isEmpty()) score++;
        if (benefit.getTargetDescription() != null && !benefit.getTargetDescription().trim().isEmpty()) score++;
        if (benefit.getCategory() != null && !benefit.getCategory().trim().isEmpty()) score++;
        if (benefit.getSupportAmount() != null && benefit.getSupportAmount() > 0) score++;
        if (benefit.getApplicationMethod() != null && !benefit.getApplicationMethod().trim().isEmpty()) score++;
        if (benefit.getInquiryUrl() != null && !benefit.getInquiryUrl().trim().isEmpty()) score++;
        if (benefit.getSupportCycle() != null && !benefit.getSupportCycle().trim().isEmpty()) score++;
        if (benefit.getServiceProvisionType() != null && !benefit.getServiceProvisionType().trim().isEmpty()) score++;
        if (benefit.getJurisdictionName() != null && !benefit.getJurisdictionName().trim().isEmpty()) score++;
        
        return (score * 100) / totalFields;
    }
}


















