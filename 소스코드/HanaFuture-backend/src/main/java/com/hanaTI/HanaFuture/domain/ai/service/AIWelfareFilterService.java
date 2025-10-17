package com.hanaTI.HanaFuture.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaTI.HanaFuture.domain.ai.config.GeminiConfig;
import com.hanaTI.HanaFuture.domain.ai.dto.GeminiRequest;
import com.hanaTI.HanaFuture.domain.ai.dto.GeminiResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gemini AI를 활용한 복지 혜택 스마트 필터링 서비스
 * - 하나퓨처 서비스에 맞는 출산·양육·교육 관련 혜택만 지능적으로 필터링
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIWelfareFilterService {

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private final GeminiConfig geminiConfig;
    private final WelfareBenefitRepository welfareRepository;
    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;

    /**
     * 복지 혜택 목록을 AI로 필터링하여 하나퓨처에 적합한 혜택만 반환
     */
    public List<WelfareBenefitResponse> filterWelfareBenefitsWithAI(List<WelfareBenefitResponse> allBenefits) {
        if (allBenefits.isEmpty()) {
            return new ArrayList<>();
        }

        log.info(" AI 복지 필터링 시작: 전체 {}개 혜택", allBenefits.size());

        try {
            // 배치 처리 (50개씩)
            List<WelfareBenefitResponse> filteredBenefits = new ArrayList<>();
            int batchSize = 50;
            
            for (int i = 0; i < allBenefits.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, allBenefits.size());
                List<WelfareBenefitResponse> batch = allBenefits.subList(i, endIndex);
                
                List<WelfareBenefitResponse> batchFiltered = filterBatchWithAI(batch);
                filteredBenefits.addAll(batchFiltered);
                
                // API 호출 간격 조절 (Rate Limit 방지)
                if (i + batchSize < allBenefits.size()) {
                    Thread.sleep(1000); // 1초 대기
                }
            }

            log.info("AI 복지 필터링 완료: {}개 → {}개 혜택", allBenefits.size(), filteredBenefits.size());
            return filteredBenefits;

        } catch (Exception e) {
            log.error("AI 복지 필터링 실패: {}", e.getMessage(), e);
            // 실패 시 기본 키워드 필터링으로 fallback
            return fallbackKeywordFilter(allBenefits);
        }
    }

    /**
     * 배치 단위로 AI 필터링 수행
     */
    private List<WelfareBenefitResponse> filterBatchWithAI(List<WelfareBenefitResponse> batch) {
        String prompt = buildFilteringPrompt(batch);
        
        try {
            // Gemini API 호출
            GeminiRequest request = GeminiRequest.createTextRequest(prompt);
            GeminiResponse response = callGeminiAPI(request);
            
            if (response == null) {
                return fallbackKeywordFilter(batch);
            }

            String aiResponse = response.getFirstResponseText();
            log.debug(" AI 필터링 응답: {}", aiResponse);

            // AI 응답에서 선택된 혜택 ID 추출
            List<Long> selectedIds = parseSelectedBenefitIds(aiResponse);
            
            // 선택된 ID에 해당하는 혜택만 반환
            return batch.stream()
                    .filter(benefit -> selectedIds.contains(benefit.getId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("배치 AI 필터링 실패, fallback 사용: {}", e.getMessage());
            return fallbackKeywordFilter(batch);
        }
    }

    /**
     * AI 필터링 프롬프트 생성
     */
    private String buildFilteringPrompt(List<WelfareBenefitResponse> benefits) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 하나퓨처 금융 서비스의 복지 혜택 큐레이터입니다.\n");
        prompt.append("하나퓨처는 **가족과 자녀를 위한 금융 서비스**를 제공합니다.\n\n");
        
        prompt.append(" **하나퓨처 서비스 특징:**\n");
        prompt.append("- 모임통장: 가족/부부 공동 계좌 관리\n");
        prompt.append("- 함께 적금: 가족 목표 저축\n");
        prompt.append("- 아이카드: 자녀 용돈 관리\n");
        prompt.append("- 주요 고객: 신혼부부, 육아맘, 자녀 교육에 관심 있는 가정\n\n");
        
        prompt.append(" **엄격한 선별 기준 (모든 조건을 만족해야 선택):**\n");
        prompt.append("1 **핵심 대상**: 0-18세 자녀가 있는 가정, 임신부, 신혼부부만 해당\n");
        prompt.append("2 **직접 혜택**: 출산, 육아, 교육, 보육과 직접 관련된 혜택만\n");
        prompt.append("3 **실질적 도움**: 현금 지원, 바우처, 무료 서비스 등 실질적 혜택\n\n");
        
        prompt.append("**반드시 포함할 키워드 (하나 이상 포함 필수):**\n");
        prompt.append("- 출산, 임신, 산모, 신생아, 영아, 유아, 아동, 자녀\n");
        prompt.append("- 보육, 육아, 양육, 돌봄, 어린이집, 유치원\n");
        prompt.append("- 교육비, 학비, 등록금, 학용품\n");
        prompt.append("- 아동수당, 영아수당, 보육료, 첫만남이용권\n\n");
        
        prompt.append("**반드시 제외 (하나라도 해당하면 제외):**\n");
        prompt.append("- 노인, 장애인, 취업, 창업, 농업, 어업, 중소기업\n");
        prompt.append("- 일반 성인 대상 (자녀/가족 언급 없음)\n");
        prompt.append("- 단순 세금 감면, 대출 지원 (육아 관련 아님)\n");
        prompt.append("- 청년 취업, 노인 일자리, 장애인 지원\n\n");
        
        prompt.append(" **분석할 복지 혜택 목록:**\n");
        for (int i = 0; i < benefits.size(); i++) {
            WelfareBenefitResponse benefit = benefits.get(i);
            prompt.append(String.format("[ID:%d] %s\n", benefit.getId(), benefit.getServiceName()));
            prompt.append(String.format("내용: %s\n", 
                    truncateText(benefit.getServiceContent(), 100)));
            prompt.append(String.format("대상: %s\n", 
                    truncateText(benefit.getTargetDescription(), 50)));
            prompt.append("\n");
        }
        
        prompt.append(" **응답 형식 (정확히 이 형식으로만 응답):**\n");
        prompt.append("SELECTED_IDS: [ID1,ID2,ID3,...]\n");
        prompt.append("REASON: 선택 이유를 간단히 설명\n\n");
        
        prompt.append("**중요**: 매우 엄격하게 선별하세요!\n");
        prompt.append("- 전체의 20-30%만 선택하는 것이 목표입니다\n");
        prompt.append("- 애매한 경우는 제외하세요\n");
        prompt.append("- 진짜 육아/교육/출산과 직접 관련된 것만 선택하세요");
        
        return prompt.toString();
    }

    /**
     * AI 응답에서 선택된 혜택 ID 추출
     */
    private List<Long> parseSelectedBenefitIds(String aiResponse) {
        List<Long> selectedIds = new ArrayList<>();
        
        try {
            String[] lines = aiResponse.split("\n");
            for (String line : lines) {
                if (line.startsWith("SELECTED_IDS:")) {
                    String idsStr = line.substring("SELECTED_IDS:".length()).trim();
                    idsStr = idsStr.replaceAll("[\\[\\]]", ""); // 대괄호 제거
                    
                    if (!idsStr.isEmpty()) {
                        String[] idArray = idsStr.split(",");
                        for (String idStr : idArray) {
                            try {
                                Long id = Long.parseLong(idStr.trim());
                                selectedIds.add(id);
                            } catch (NumberFormatException e) {
                                log.warn("ID 파싱 실패: {}", idStr);
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
        }
        
        log.debug(" AI 선택 혜택 ID: {}", selectedIds);
        return selectedIds;
    }

    /**
     * Gemini API 호출
     */
    private GeminiResponse callGeminiAPI(GeminiRequest request) {
        try {
            // v1 API 직접 사용 (403 오류 해결)
            String v1ApiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";
            String apiKey = "AIzaSyBos48rLWh6F2HkkttqbOA_w3dszF2Szy0"; // 직접 하드코딩
            
            log.info(" Gemini v1 API 호출: {}", v1ApiUrl);
            
            return geminiWebClient.post()
                    .uri(v1ApiUrl)
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * AI 실패 시 기본 키워드 필터링 (fallback)
     */
    private List<WelfareBenefitResponse> fallbackKeywordFilter(List<WelfareBenefitResponse> benefits) {
        log.info(" 기본 키워드 필터링 사용 (fallback)");
        
        // 엄격한 키워드 필터링 - 핵심 육아/교육 키워드만
        List<String> strictKeywords = List.of(
                "출산", "임신", "산모", "신생아", "영아", "유아", "아동", "자녀",
                "보육료", "육아", "양육", "돌봄", "어린이집", "유치원", 
                "아동수당", "영아수당", "첫만남", "교육비", "학비", "등록금"
        );
        
        // 제외할 키워드 - 이런 키워드가 있으면 제외
        List<String> excludeKeywords = List.of(
                "노인", "장애인", "취업", "창업", "농업", "어업", "중소기업", 
                "청년일자리", "소상공인", "벤처", "스타트업", "고령자"
        );
        
        return benefits.stream()
                .filter(benefit -> {
                    try {
                        String serviceName = benefit.getServiceName() != null ? benefit.getServiceName().toLowerCase() : "";
                        String serviceContent = benefit.getServiceContent() != null ? benefit.getServiceContent().toLowerCase() : "";
                        String targetDesc = benefit.getTargetDescription() != null ? benefit.getTargetDescription().toLowerCase() : "";
                        String category = benefit.getCategory() != null ? benefit.getCategory().toLowerCase() : "";
                        String lifeCycle = benefit.getLifeCycle() != null ? benefit.getLifeCycle().toLowerCase() : "";
                        
                        // 전체 텍스트 결합 (null 안전)
                        String allText = serviceName + " " + serviceContent + " " + targetDesc + " " + category + " " + lifeCycle;
                        
                        // 제외 키워드 체크
                        boolean hasExcludeKeyword = excludeKeywords.stream().anyMatch(allText::contains);
                        if (hasExcludeKeyword) {
                            return false;
                        }
                        
                        // 포함 키워드 체크
                        return strictKeywords.stream().anyMatch(allText::contains);
                        
                    } catch (Exception e) {
                        log.warn("혜택 필터링 중 오류 (ID: {}): {}", benefit.getId(), e.getMessage());
                        return false; // 오류 발생 시 제외
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 텍스트 길이 제한
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    /**
     * AI 필터링 결과를 바로 hana_future_welfare_benefits 테이블에 저장
     */
    public void saveFilteredBenefitsDirectly() {
        log.info(" AI 필터링 후 하나퓨처 테이블에 직접 저장 시작...");
        
        try {
            // Step 1: 기존 하나퓨처 테이블 데이터 삭제
            hanaFutureRepository.deleteAll();
            log.info(" 기존 하나퓨처 복지 데이터 삭제 완료");
            
            // Step 2: 전체 복지 데이터 조회
            List<WelfareBenefit> allBenefits = welfareRepository.findAll().stream()
                .filter(WelfareBenefit::getIsActive)
                .collect(Collectors.toList());
            log.info(" 원본 복지 데이터: {}개", allBenefits.size());
            
            // Step 3: WelfareBenefit을 WelfareBenefitResponse로 변환
            List<WelfareBenefitResponse> allBenefitResponses = allBenefits.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            // Step 4: AI 필터링 실행
            List<WelfareBenefitResponse> filteredResponses = filterWelfareBenefitsWithAI(allBenefitResponses);
            log.info(" AI 필터링 완료: {}개 → {}개", allBenefits.size(), filteredResponses.size());
            
            // Step 5: 필터링된 ID로 원본 데이터 조회
            List<Long> filteredIds = filteredResponses.stream()
                .map(WelfareBenefitResponse::getId)
                .collect(Collectors.toList());
            
            List<WelfareBenefit> filteredBenefits = welfareRepository.findAllById(filteredIds);
            
            // Step 6: HanaFutureWelfareBenefit으로 변환 후 저장
            List<HanaFutureWelfareBenefit> hanaFutureBenefits = filteredBenefits.stream()
                .map(benefit -> HanaFutureWelfareBenefit.fromOriginal(benefit, "Gemini AI 필터링"))
                .collect(Collectors.toList());
            
            hanaFutureRepository.saveAll(hanaFutureBenefits);
            
            // Step 7: 결과 통계
            long centralCount = hanaFutureBenefits.stream()
                .filter(b -> "중앙정부".equals(b.getServiceType()))
                .count();
            long localCount = hanaFutureBenefits.stream()
                .filter(b -> "지자체".equals(b.getServiceType()))
                .count();
            
            log.info("하나퓨처 복지 데이터 저장 완료!");
            log.info(" 총 {}개 저장 (중앙정부: {}개, 지자체: {}개)", 
                hanaFutureBenefits.size(), centralCount, localCount);
            
        } catch (Exception e) {
            log.error("AI 필터링 후 저장 실패", e);
            throw new RuntimeException("AI 필터링 후 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * WelfareBenefit을 WelfareBenefitResponse로 변환
     */
    private WelfareBenefitResponse convertToResponse(WelfareBenefit benefit) {
        return WelfareBenefitResponse.builder()
            .id(benefit.getId())
            .serviceName(benefit.getServiceName())
            .serviceContent(benefit.getServiceContent())
            .targetDescription(benefit.getTargetDescription())
            .category(benefit.getCategory())
            .lifeCycle(benefit.getLifeCycle())
            .serviceType(benefit.getServiceType())
            .supportAmount(benefit.getSupportAmount())
            .applicationMethod(benefit.getApplicationMethod())
            .build();
    }
}
