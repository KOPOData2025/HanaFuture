package com.hanaTI.HanaFuture.domain.welfare.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.WelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.LocalWelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareXmlParsingService {
    
    private final XmlMapper xmlMapper = new XmlMapper();
    
    // 지원금액 추출을 위한 정규표현식
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*(?:만\\s*)?원");
    private static final Pattern WON_PATTERN = Pattern.compile("(\\d{1,3}(?:,\\d{3})*)원");
    
    /**
     * XML 응답을 파싱하여 WelfareApiResponse 객체로 변환
     */
    public WelfareApiResponse parseXmlResponse(String xmlResponse) {
        try {
            log.debug("XML 응답 파싱 시작");
            
            // XML 응답 정리 (BOM 제거, 특수문자 처리)
            String cleanedXml = cleanXmlResponse(xmlResponse);
            
            WelfareApiResponse response = xmlMapper.readValue(cleanedXml, WelfareApiResponse.class);
            
            log.info("XML 파싱 완료 - 총 {}개 항목", 
                    response.getServList() != null ? response.getServList().size() : 0);
            
            return response;
        } catch (Exception e) {
            log.error("XML 파싱 실패: {}", e.getMessage(), e);
            log.debug("파싱 실패한 XML 내용: {}", xmlResponse);
            throw new RuntimeException("복지로 API 응답 파싱에 실패했습니다.", e);
        }
    }
    
    /**
     * WelfareApiResponse를 WelfareBenefit 엔티티 리스트로 변환
     */
    public List<WelfareBenefit> convertToEntities(WelfareApiResponse apiResponse, WelfareType welfareType) {
        if (apiResponse.getServList() == null || apiResponse.getServList().isEmpty()) {
            log.warn("API 응답에 데이터가 없습니다.");
            return Collections.emptyList();
        }
        
        return apiResponse.getServList().stream()
                .map(item -> convertToEntity(item, welfareType))
                .collect(Collectors.toList());
    }
    
    /**
     * 개별 WelfareItem을 WelfareBenefit 엔티티로 변환
     */
    private WelfareBenefit convertToEntity(WelfareApiResponse.WelfareItem item, WelfareType welfareType) {
        WelfareBenefit.WelfareBenefitBuilder builder = WelfareBenefit.builder()
                .serviceId(item.getServiceId())
                .serviceName(cleanText(item.getServiceName()))
                .serviceType(welfareType)
                .lifeCycle(cleanText(item.getLifeCycle()))
                .category(cleanText(item.getInterestTheme())) // intrsThemaArray를 category로 매핑
                .targetDescription(cleanText(item.getTargetDescription()))
                .serviceContent(cleanText(item.getServiceDigest())) // 서비스 요약 사용
                .inquiryUrl(cleanText(item.getServiceDetailLink())) // 서비스 상세 링크 사용
                .supportCycle(cleanText(item.getSupportCycle())) // 지원주기
                .serviceProvisionType(cleanText(item.getServiceProvisionType())) // 서비스 제공 형태
                .onlineApplicationPossible(cleanText(item.getOnlineApplicationPossible())) // 온라인 신청 가능 여부
                .serviceFirstRegistrationDate(cleanText(item.getServiceFirstRegistrationDate())) // 서비스 최초 등록일
                .lastSyncedAt(LocalDateTime.now());
        
        // 중앙부처 서비스 필드
        if (welfareType == WelfareType.CENTRAL) {
            builder.jurisdictionName(cleanText(item.getJurisdictionName()));
        }
        
        // 지자체 서비스 필드
        if (welfareType == WelfareType.LOCAL) {
            builder.jurisdictionName(cleanText(item.getJurisdictionName()))
                   .sidoName(cleanText(item.getSidoName()))
                   .sigunguName(cleanText(item.getSigunguName()))
                   .areaCode(generateAreaCode(item.getSidoCode(), item.getSigunguCode()));
        }
        
        WelfareBenefit benefit = builder.build();
        
        // 지원금액 파싱 및 설정
        extractAndSetSupportAmount(benefit, item.getServiceDigest());
        
        return benefit;
    }
    
    /**
     * 텍스트 정리 (null 체크, HTML 태그 제거, 공백 정리)
     */
    private String cleanText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        return text.replaceAll("<[^>]*>", "") // HTML 태그 제거
                  .replaceAll("&nbsp;", " ") // &nbsp; 처리
                  .replaceAll("\\s+", " ") // 연속된 공백을 하나로
                  .trim();
    }
    
    /**
     * XML 응답 정리
     */
    private String cleanXmlResponse(String xmlResponse) {
        if (xmlResponse == null) {
            return "";
        }
        
        // BOM 제거
        if (xmlResponse.startsWith("\uFEFF")) {
            xmlResponse = xmlResponse.substring(1);
        }
        
        // 잘못된 XML 문자 제거 또는 치환
        xmlResponse = xmlResponse.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
        
        return xmlResponse.trim();
    }
    
    /**
     * 지역코드 생성 (시도코드/시군구코드)
     */
    private String generateAreaCode(String sidoCode, String sigunguCode) {
        if (sidoCode == null) return null;
        if (sigunguCode == null) return sidoCode;
        return sidoCode + "/" + sigunguCode;
    }
    
    /**
     * 서비스 내용에서 지원금액 추출
     */
    private void extractAndSetSupportAmount(WelfareBenefit benefit, String serviceContent) {
        if (serviceContent == null || serviceContent.trim().isEmpty()) {
            return;
        }
        
        try {
            // 다양한 패턴으로 금액 추출 시도
            Long amount = extractAmount(serviceContent);
            if (amount != null) {
                benefit.updateSupportAmount(amount, extractAmountDescription(serviceContent));
                log.debug("지원금액 추출 성공: {} -> {}원", benefit.getServiceName(), amount);
            }
        } catch (Exception e) {
            log.debug("지원금액 추출 실패 - 서비스: {}, 내용: {}", benefit.getServiceName(), serviceContent);
        }
    }
    
    /**
     * 텍스트에서 금액 추출
     */
    private Long extractAmount(String text) {
        // "만원" 단위 금액 찾기
        Matcher matcher = AMOUNT_PATTERN.matcher(text);
        if (matcher.find()) {
            String amountStr = matcher.group(1).replace(",", "");
            try {
                long amount = Long.parseLong(amountStr);
                // "만원" 단위인지 확인
                if (text.substring(matcher.start(), matcher.end()).contains("만")) {
                    amount *= 10000; // 만원을 원으로 변환
                }
                return amount;
            } catch (NumberFormatException e) {
                log.debug("금액 파싱 실패: {}", amountStr);
            }
        }
        
        // "원" 단위 금액 찾기
        matcher = WON_PATTERN.matcher(text);
        if (matcher.find()) {
            String amountStr = matcher.group(1).replace(",", "");
            try {
                return Long.parseLong(amountStr);
            } catch (NumberFormatException e) {
                log.debug("금액 파싱 실패: {}", amountStr);
            }
        }
        
        return null;
    }
    
    /**
     * 금액 관련 설명 추출
     */
    private String extractAmountDescription(String text) {
        // 금액이 포함된 문장 추출
        String[] sentences = text.split("[.!?]");
        for (String sentence : sentences) {
            if (sentence.contains("원") || sentence.contains("지원") || sentence.contains("급여")) {
                return sentence.trim();
            }
        }
        return null;
    }
    
    /**
     * 지자체 API XML 응답 파싱
     */
    public LocalWelfareApiResponse parseLocalXmlResponse(String xmlResponse) {
        try {
            log.debug("지자체 XML 응답 파싱 시작");
            
            String cleanedXml = cleanXmlResponse(xmlResponse);
            LocalWelfareApiResponse response = xmlMapper.readValue(cleanedXml, LocalWelfareApiResponse.class);
            
            log.info("지자체 XML 파싱 완료 - 총 {}개 항목", 
                    response.getServList() != null ? response.getServList().size() : 0);
            
            return response;
        } catch (Exception e) {
            log.error("지자체 XML 파싱 실패: {}", e.getMessage(), e);
            log.debug("파싱 실패한 XML 내용: {}", xmlResponse);
            throw new RuntimeException("지자체 복지로 API 응답 파싱에 실패했습니다.", e);
        }
    }
    
    /**
     * 지자체 API 응답을 WelfareBenefit 엔티티 리스트로 변환
     */
    public List<WelfareBenefit> convertLocalToEntities(LocalWelfareApiResponse apiResponse) {
        if (apiResponse.getServList() == null || apiResponse.getServList().isEmpty()) {
            log.warn("지자체 API 응답에 데이터가 없습니다.");
            return Collections.emptyList();
        }
        
        return apiResponse.getServList().stream()
                .map(this::convertLocalToEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 지자체 WelfareItem을 WelfareBenefit 엔티티로 변환
     */
    private WelfareBenefit convertLocalToEntity(LocalWelfareApiResponse.LocalWelfareItem item) {
        WelfareBenefit benefit = WelfareBenefit.builder()
                .serviceId(item.getServiceId())
                .serviceName(cleanText(item.getServiceName()))
                .serviceType(WelfareType.LOCAL)
                .lifeCycle(cleanText(item.getLifeCycleArray()))
                .targetDescription(cleanText(item.getTargetIndividualArray()))
                .serviceContent(cleanText(item.getServiceDigest()))
                .jurisdictionName(cleanText(item.getBusinessDepartmentName()))
                .sidoName(cleanText(item.getSidoName()))
                .sigunguName(cleanText(item.getSigunguName()))
                .lastSyncedAt(LocalDateTime.now())
                .build();
        
        // 지원금액 파싱 및 설정
        extractAndSetSupportAmount(benefit, item.getServiceDigest());
        
        return benefit;
    }
    
    /**
     * API 응답이 성공인지 확인
     */
    public boolean isSuccessResponse(WelfareApiResponse response) {
        return response != null && response.isSuccess();
    }
    
    /**
     * 지자체 API 응답이 성공인지 확인
     */
    public boolean isLocalSuccessResponse(LocalWelfareApiResponse response) {
        return response != null && response.isSuccess();
    }
}
