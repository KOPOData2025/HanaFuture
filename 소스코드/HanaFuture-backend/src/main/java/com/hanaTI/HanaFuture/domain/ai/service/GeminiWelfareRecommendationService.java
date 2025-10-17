package com.hanaTI.HanaFuture.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaTI.HanaFuture.domain.ai.config.GeminiConfig;
import com.hanaTI.HanaFuture.domain.ai.dto.*;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.child.repository.ChildRepository;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountRepository;
import com.hanaTI.HanaFuture.domain.savings.repository.SavingDepositRepository;
import com.hanaTI.HanaFuture.domain.account.repository.AccountRepository;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.service.WelfareBenefitService;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareSearchRequest;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiWelfareRecommendationService {
    
    private final GeminiConfig geminiConfig;
    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final GroupAccountRepository groupAccountRepository;
    private final SavingDepositRepository savingDepositRepository;
    private final AccountRepository accountRepository;
    private final WelfareBenefitService welfareBenefitService;
    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;
    
    /**
     * 사용자 맞춤 복지 혜택 추천
     */
    public Page<WelfareBenefitResponse> getPersonalizedWelfareRecommendations(
            Long userId, int page, int size) {
        
        try {
            log.info("사용자 {}에 대한 AI 복지 추천 시작", userId);
            
            // 1. 사용자 프로필 수집
            UserProfileDto userProfile = buildUserProfile(userId);
            log.info("사용자 프로필 수집 완료: {}", userProfile.getName());
            
            // 2. Gemini AI에게 추천 요청
            WelfareRecommendationDto recommendation = getAIRecommendation(userProfile);
            log.info("AI 추천 완료: {} 키워드", recommendation.getRecommendedKeywords().size());
            
            // 3. AI 추천을 바탕으로 실제 복지 데이터 필터링
            Page<WelfareBenefitResponse> recommendedBenefits = filterWelfareBenefits(
                    recommendation, userProfile, page, size);
            
            log.info("총 {}개의 맞춤 복지 혜택 추천 완료 (사용자 거주지: {} {})", 
                recommendedBenefits.getTotalElements(), userProfile.getSidoName(), userProfile.getSigunguName());
            return recommendedBenefits;
            
        } catch (Exception e) {
            log.error("AI 복지 추천 실패: {}", e.getMessage(), e);
            // 실패 시 기본 추천 로직으로 fallback
            return getFallbackRecommendations(userId, page, size);
        }
    }
    
    /**
     * 사용자 프로필 구성 - 실제 데이터 기반
     */
    private UserProfileDto buildUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        log.info("사용자 정보 수집: {} ({})", user.getName(), user.getEmail());
        
        // 실제 자녀 정보 조회
        List<UserProfileDto.ChildInfo> children = childRepository.findByParentUserAndStatusOrderByCreatedAtDesc(
                user, com.hanaTI.HanaFuture.domain.child.entity.ChildStatus.ACTIVE).stream()
                .map(child -> UserProfileDto.ChildInfo.builder()
                        .name(child.getName())
                        .age(child.getAge())
                        .schoolType(determineSchoolType(child.getAge()))
                        .hasSpecialNeeds(false) // 향후 확장 가능
                        .build())
                .collect(Collectors.toList());
        
        log.info("자녀 정보: {}명 ({})", children.size(), 
                children.stream().map(c -> c.getName() + "(" + c.getAge() + "세)").collect(Collectors.joining(", ")));
        
        // 실제 나이 계산
        Integer userAge = user.getAge();
        if (userAge == null) {
            userAge = 30; // 기본값
            log.warn("사용자 생년월일 정보 없음, 기본값 30세 사용");
        }
        
        // 실제 결혼 상태
        Boolean isMarried = user.getMaritalStatus() != null && 
                (user.getMaritalStatus() == com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus.MARRIED);
        
        // 실제 거주지 정보
        String sidoName = user.getResidenceSido() != null ? user.getResidenceSido() : "서울특별시";
        String sigunguName = user.getResidenceSigungu() != null ? user.getResidenceSigungu() : "강남구";
        
        // 실제 소득 정보
        Long monthlyIncome = user.getIncome() != null ? user.getIncome() : 5000000L;
        
        // 실제 자녀 관련 정보
        Boolean hasChildren = user.hasAnyChildren() || !children.isEmpty();
        Integer childrenCount = user.getNumberOfChildren() != null ? user.getNumberOfChildren() : children.size();
        
        // 실제 임신 여부
        Boolean isPregnant = user.getIsPregnant() != null ? user.getIsPregnant() : false;
        
        log.info("프로필 요약: {}세, 결혼={}, 자녀={}명, 임신={}, 거주지={} {}, 소득={}원", 
                userAge, isMarried, childrenCount, isPregnant, sidoName, sigunguName, monthlyIncome);
        
        return UserProfileDto.builder()
                .userId(userId)
                .name(user.getName())
                .age(userAge)
                .email(user.getEmail())
                .hasChildren(hasChildren)
                .childrenCount(childrenCount)
                .children(children)
                .isPregnant(isPregnant)
                .isMarried(isMarried)
                .sidoName(sidoName)
                .sigunguName(sigunguName)
                .monthlyIncome(monthlyIncome)
                .totalAssets(calculateTotalAssets(userId))
                .hasGroupAccount(hasGroupAccount(userId))
                .hasSavingsAccount(hasSavingsAccount(userId))
                .build();
    }
    
    /**
     * Gemini AI에게 추천 요청
     */
    private WelfareRecommendationDto getAIRecommendation(UserProfileDto userProfile) {
        
        String prompt = buildRecommendationPrompt(userProfile);
        log.debug("Gemini 요청 프롬프트: {}", prompt);
        
        // Gemini API 호출
        GeminiRequest request = GeminiRequest.createTextRequest(prompt);
        GeminiResponse response = callGeminiAPI(request);
        
        // AI 응답 파싱
        String aiResponse = response.getFirstResponseText();
        log.debug("Gemini 응답: {}", aiResponse);
        
        return parseAIResponse(aiResponse, userProfile);
    }
    
    /**
     * AI 추천 프롬프트 생성
     */
    private String buildRecommendationPrompt(UserProfileDto profile) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 대한민국 정부 복지 혜택 전문 상담사입니다.\n");
        prompt.append("다음 사용자 정보를 바탕으로 맞춤형 복지 혜택을 추천해주세요.\n\n");
        
        prompt.append(" 사용자 정보:\n");
        prompt.append("- 이름: ").append(profile.getName()).append("\n");
        prompt.append("- 나이: ").append(profile.getAge()).append("세\n");
        prompt.append("- 결혼 여부: ").append(profile.getIsMarried() ? "기혼" : "미혼").append("\n");
        prompt.append("- 자녀 수: ").append(profile.getChildrenCount()).append("명\n");
        
        if (profile.getHasChildren()) {
            prompt.append("- 자녀 정보:\n");
            for (UserProfileDto.ChildInfo child : profile.getChildren()) {
                prompt.append("  * ").append(child.getName()).append("(").append(child.getAge())
                        .append("세, ").append(child.getSchoolType()).append(")\n");
            }
        }
        
        prompt.append("- 거주지: ").append(profile.getSidoName()).append(" ").append(profile.getSigunguName()).append("\n");
        prompt.append("- 월 소득: ").append(profile.getMonthlyIncome()).append("원\n");
        prompt.append("- 임신 여부: ").append(profile.getIsPregnant() ? "예" : "아니오").append("\n\n");
        
        prompt.append(" 다음 형식으로 정확히 응답해주세요:\n");
        prompt.append("KEYWORDS: [키워드1,키워드2,키워드3] (최대 10개)\n");
        prompt.append("CATEGORIES: [카테고리1,카테고리2] (최대 5개)\n");
        prompt.append("LIFECYCLE: [생애주기1,생애주기2] (해당하는 것들)\n");
        prompt.append("SUMMARY: 추천 이유를 1-2문장으로 설명\n");
        prompt.append("CONFIDENCE: 신뢰도 점수 (1-100)\n\n");
        
        prompt.append(" 추천 기준:\n");
        prompt.append("- 출산, 육아, 교육 관련 혜택 우선\n");
        prompt.append("- 사용자 나이와 자녀 연령에 적합한 혜택\n");
        prompt.append("- 거주 지역에서 신청 가능한 혜택\n");
        prompt.append("- 소득 수준에 맞는 혜택\n");
        
        return prompt.toString();
    }
    
    /**
     * Gemini API 호출
     */
    private GeminiResponse callGeminiAPI(GeminiRequest request) {
        String apiKey = geminiConfig.getGeminiApiKey();
        
        log.info("Gemini API 호출 시작 - API Key: {}...", apiKey.substring(0, 10));
        
        try {
            // v1 API 직접 사용 (403 오류 해결)
            String v1ApiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;
            
            log.info("Gemini v1 API 호출: {}", v1ApiUrl);
            
            Mono<GeminiResponse> responseMono = geminiWebClient
                    .post()
                    .uri(v1ApiUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class);
            
            GeminiResponse response = responseMono.block(); // 동기식 처리
            log.info("Gemini API 응답 성공");
            
            return response;
            
        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini API 호출 실패", e);
        }
    }
    
    /**
     * AI 응답 파싱
     */
    private WelfareRecommendationDto parseAIResponse(String aiResponse, UserProfileDto profile) {
        // 간단한 파싱
        List<String> keywords = extractSection(aiResponse, "KEYWORDS");
        List<String> categories = extractSection(aiResponse, "CATEGORIES");
        List<String> lifeCycles = extractSection(aiResponse, "LIFECYCLE");
        String summary = extractSummary(aiResponse, "SUMMARY");
        int confidence = extractConfidence(aiResponse, "CONFIDENCE");
        
        return WelfareRecommendationDto.builder()
                .userId(profile.getUserId())
                .recommendedKeywords(keywords)
                .priorityCategories(categories)
                .recommendationSummary(summary)
                .reasoningExplanation("Gemini AI가 사용자 프로필을 분석하여 생성한 맞춤 추천")
                .confidenceScore(confidence)
                .filterCriteria(WelfareRecommendationDto.FilterCriteria.builder()
                        .includeKeywords(keywords)
                        .lifeCycles(lifeCycles)
                        .region(profile.getSidoName())
                        .requiresChildren(profile.getHasChildren())
                        .build())
                .build();
    }
    
    /**
     * AI 추천을 바탕으로 하나퓨처 복지 데이터 필터링 (hana_future_welfare_benefits 기반)
     *
     * 1. 중앙정부 혜택: AI 키워드로 검색 + AI 관련도 상위 혜택도 추가 (필터링 완화)
     * 2. 지자체 혜택: 사용자 거주지(시군구)로 직접 검색
     */
    private Page<WelfareBenefitResponse> filterWelfareBenefits(
            WelfareRecommendationDto recommendation, UserProfileDto userProfile, int page, int size) {
        
        String userSigungu = userProfile.getSigunguName(); // "금천구"
        
        log.info("복지 혜택 검색 시작 - 사용자: {} {}", userProfile.getSidoName(), userSigungu);
        
        java.util.Set<WelfareBenefitResponse> allResults = new java.util.LinkedHashSet<>();
        
        // 1-1. 중앙정부 혜택: AI 추천 키워드로 검색
        if (!recommendation.getRecommendedKeywords().isEmpty()) {
            for (String keyword : recommendation.getRecommendedKeywords()) {
                log.debug("키워드 '{}' 로 중앙정부 혜택 검색", keyword);
                
                Page<HanaFutureWelfareBenefit> results = hanaFutureRepository
                    .findByKeywordAndIsActiveTrue(keyword.trim(), 
                        org.springframework.data.domain.PageRequest.of(0, 100)); // 더 많이 가져오기
                
                results.getContent().stream()
                    .filter(benefit -> benefit.getSidoName() == null && benefit.getSigunguName() == null)
                    .forEach(benefit -> allResults.add(convertToResponse(benefit)));
            }
        }
        
        log.info("키워드로 중앙정부 혜택 {}개 수집", allResults.size());
        
        // 1-2. 중앙정부 혜택: AI 관련도 순으로 상위 30개 추가
        Page<HanaFutureWelfareBenefit> topCentralBenefits = hanaFutureRepository
            .findBySidoNameIsNullAndSigunguNameIsNullAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
                org.springframework.data.domain.PageRequest.of(0, 30));
        
        topCentralBenefits.getContent().forEach(benefit -> allResults.add(convertToResponse(benefit)));
        
        log.info("AI 관련도 순으로 중앙정부 혜택 {}개 추가, 총 {}개", 
            topCentralBenefits.getTotalElements(), allResults.size());
        
        // 2. 지자체 혜택: 사용자 거주지(시군구)로 직접 검색
        log.info("지자체 혜택 검색 - 시군구: {}", userSigungu);
        Page<HanaFutureWelfareBenefit> localResults = hanaFutureRepository
            .findBySigunguNameContainingAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
                userSigungu, org.springframework.data.domain.PageRequest.of(0, 50));
        
        localResults.getContent().forEach(benefit -> allResults.add(convertToResponse(benefit)));
        
        log.info("지자체 혜택 {}개 추가, 총 {}개", localResults.getTotalElements(), allResults.size());
        
        // 중앙정부/지자체 분포 확인
        long centralCount = allResults.stream()
            .filter(r -> r.getSidoName() == null && r.getSigunguName() == null)
            .count();
        long localCount = allResults.size() - centralCount;
        
        log.info("최종 결과 분포 - 중앙정부: {}개, 지자체: {}개", centralCount, localCount);
        
        // List로 변환 후 페이징 적용
        java.util.List<WelfareBenefitResponse> resultList = new java.util.ArrayList<>(allResults);
        int start = page * size;
        int end = Math.min(start + size, resultList.size());
        java.util.List<WelfareBenefitResponse> pagedResults = resultList.subList(start, end);
        
        return new PageImpl<>(pagedResults, 
            org.springframework.data.domain.PageRequest.of(page, size), 
            resultList.size());
    }
    
    /**
     * 사용자 거주지 추출 (구/시/군 단위까지 상세하게)
     * 예: sidoName="서울특별시", sigunguName="금천구" -> "금천구"
     * 예: sidoName="경기도", sigunguName="성남시" -> "성남시"
     */
    private String extractUserRegion(UserProfileDto userProfile) {
        String sido = userProfile.getSidoName();
        String sigungu = userProfile.getSigunguName();
        
        // 시군구가 있으면 가장 상세한 단위 우선 (구/시/군)
        if (sigungu != null && !sigungu.isEmpty()) {
            log.debug(" 사용자 거주지 (시군구): {}", sigungu);
            return sigungu;
        }
        
        // 시군구가 없으면 시도 반환
        if (sido != null && !sido.isEmpty()) {
            log.debug(" 사용자 거주지 (시도): {}", sido);
            return sido;
        }
        
        // 둘 다 없으면 null
        log.warn("사용자 거주지 정보 없음");
        return null;
    }
    
    /**
     * 거주지 기반 필터링
     * - 중앙정부 혜택: 모두 표시
     * - 지자체 혜택: 사용자 거주지와 정확히 일치하는 것만 표시
     */
    private Page<WelfareBenefitResponse> filterByUserRegion(
            Page<HanaFutureWelfareBenefit> benefits, UserProfileDto userProfile, int page, int size) {
        
        String userSido = userProfile.getSidoName();       // "서울특별시"
        String userSigungu = userProfile.getSigunguName(); // "금천구"
        
        log.info("필터링 시작 - 전체 혜택: {}개, 사용자 거주지: {} {}", 
            benefits.getTotalElements(), userSido, userSigungu);
        
        // 모든 혜택을 순회하면서 필터링
        List<WelfareBenefitResponse> filteredList = benefits.getContent().stream()
            .map(benefit -> {
                WelfareBenefitResponse response = convertToResponse(benefit);
                
                // 지역 기반 필터링
                String benefitSido = benefit.getSidoName();
                String benefitSigungu = benefit.getSigunguName();
                
                log.debug(" 혜택: {} - 시도: {}, 시군구: {}", benefit.getServiceName(), benefitSido, benefitSigungu);
                
                // sido_name과 sigungu_name이 둘 다 null이면 중앙정부 혜택으로 간주
                if (benefitSido == null && benefitSigungu == null) {
                    log.debug("중앙정부 혜택 (지역 정보 없음): {}", benefit.getServiceName());
                    return response;
                }
                
                // 지자체 혜택인 경우: 사용자 주소와 비교
                // 1. sigungu_name이 있으면: 시군구만 비교하면 됨
                if (benefitSigungu != null && !benefitSigungu.isEmpty()) {
                    // 시군구 일치 확인
                    if (benefitSigungu.contains(userSigungu)) {
                        log.debug("시군구 일치: {} (사용자: {}, 혜택: {})", 
                            benefit.getServiceName(), userSigungu, benefitSigungu);
                        return response;
                    } else {
                        log.debug(" 시군구 불일치: {} (사용자: {}, 혜택: {})", 
                            benefit.getServiceName(), userSigungu, benefitSigungu);
                        return null;
                    }
                }
                
                // 2. sido_name만 있고 sigungu_name이 없으면: 시도 전체 혜택
                if (benefitSido != null && !benefitSido.isEmpty()) {
                    // 사용자 시도와 혜택 시도 비교
                    String userSidoShort = userSido.replace("특별시", "").replace("광역시", "").replace("특별자치시", "");
                    String benefitSidoShort = benefitSido.replace("특별시", "").replace("광역시", "").replace("특별자치시", "");
                    
                    if (benefitSidoShort.equals(userSidoShort)) {
                        log.debug("시도 일치 (전체 혜택): {} (사용자: {}, 혜택: {})", 
                            benefit.getServiceName(), userSido, benefitSido);
                        return response;
                    } else {
                        log.debug(" 시도 불일치: {} (사용자: {}, 혜택: {})", 
                            benefit.getServiceName(), userSido, benefitSido);
                        return null;
                    }
                }
                
                // sido, sigungu 모두 있지만 조건 불일치
                log.debug(" 지역 불일치: {}", benefit.getServiceName());
                return null;
            })
            .filter(response -> response != null)
            .collect(Collectors.toList());
        
        log.info("필터링 완료 - 필터링 후: {}개 (중앙정부+지자체)", filteredList.size());
        
        // 중앙정부/지자체 분포 확인
        long centralCount = filteredList.stream()
            .filter(r -> r.getServiceType() == WelfareType.CENTRAL)
            .count();
        long localCount = filteredList.stream()
            .filter(r -> r.getServiceType() == WelfareType.LOCAL)
            .count();
        
        log.info(" 필터링 결과 분포 - 중앙정부: {}개, 지자체: {}개", centralCount, localCount);
        
        // PageImpl로 변환하여 반환
        return new PageImpl<>(filteredList, benefits.getPageable(), filteredList.size());
    }
    
    /**
     * 광역시/도 단위인지 확인
     */
    private boolean isProvincialLevel(String region) {
        return region.endsWith("특별시") || region.endsWith("광역시") || 
               region.endsWith("특별자치시") || region.endsWith("도") || 
               region.endsWith("특별자치도");
    }
    
    /**
     * 서울시 구인지 확인
     */
    private boolean isSeoulDistrict(String district) {
        String[] seoulDistricts = {
            "종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구",
            "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구",
            "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구",
            "서초구", "강남구", "송파구", "강동구"
        };
        return java.util.Arrays.asList(seoulDistricts).contains(district);
    }
    
    /**
     * 시군구에서 시도 추출 (간단 버전)
     */
    private String extractProvinceFromDistrict(String district) {
        if (isSeoulDistrict(district)) {
            return "서울특별시";
        }
        return null;
    }
    
    
    
    /**
     * 실패 시 기본 추천 (fallback) - 하나퓨처 데이터 기반
     */
    private Page<WelfareBenefitResponse> getFallbackRecommendations(Long userId, int page, int size) {
        log.warn("AI 추천 실패, 기본 하나퓨처 복지 데이터로 fallback");
        
        // 하나퓨처 복지 데이터에서 AI 관련도 순으로 반환
        Page<HanaFutureWelfareBenefit> fallbackResults = hanaFutureRepository
            .findByIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
                org.springframework.data.domain.PageRequest.of(page, size));
        
        return fallbackResults.map(this::convertToResponse);
    }
    
    /**
     * HanaFutureWelfareBenefit을 WelfareBenefitResponse로 변환
     */
    private WelfareBenefitResponse convertToResponse(HanaFutureWelfareBenefit benefit) {
        return WelfareBenefitResponse.builder()
                .id(benefit.getId())
                .serviceId(benefit.getServiceId())
                .serviceName(benefit.getServiceName())
                .serviceType(parseServiceType(benefit.getServiceType()))
                .serviceContent(benefit.getServiceContent())
                .applicationMethod(benefit.getApplicationMethod())
                .targetDescription(benefit.getTargetDescription())
                .supportAmount(benefit.getSupportAmount())
                .category(benefit.getCategory())
                .lifeCycle(benefit.getLifeCycle())
                .areaCode(benefit.getRegionCode())
                .sidoName(benefit.getSidoName())
                .sigunguName(benefit.getSigunguName())
                .inquiryUrl(benefit.getRelatedUrl())
                .jurisdictionName(benefit.getReceptionAgency())
                .build();
    }
    
    /**
     * String을 WelfareType enum으로 안전하게 변환
     */
    private com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType parseServiceType(String serviceTypeStr) {
        if (serviceTypeStr == null || serviceTypeStr.trim().isEmpty()) {
            log.warn("serviceType이 null 또는 비어있음");
            return null;
        }
        
        try {
            WelfareType type = com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType.valueOf(serviceTypeStr.trim().toUpperCase());
            log.debug("serviceType 변환 성공: '{}' -> {}", serviceTypeStr, type);
            return type;
        } catch (IllegalArgumentException e) {
            log.warn("serviceType 변환 실패: '{}' - {}", serviceTypeStr, e.getMessage());
            log.warn("알 수 없는 serviceType: '{}' - null로 처리", serviceTypeStr);
            return null;
        }
    }

    private List<String> extractSection(String response, String section) {
        // 간단한 파싱 구현
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.startsWith(section + ":")) {
                String content = line.substring(section.length() + 1).trim();
                content = content.replaceAll("[\\[\\]]", "");
                return Arrays.asList(content.split(",")).stream()
                        .map(String::trim)
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
    
    private String extractSummary(String response, String section) {
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.startsWith(section + ":")) {
                return line.substring(section.length() + 1).trim();
            }
        }
        return "Gemini AI가 분석한 맞춤형 복지 혜택입니다.";
    }
    
    private int extractConfidence(String response, String section) {
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.startsWith(section + ":")) {
                String content = line.substring(section.length() + 1).trim();
                try {
                    return Integer.parseInt(content);
                } catch (NumberFormatException e) {
                    return 80;
                }
            }
        }
        return 80;
    }
    
    /**
     * 나이에 따른 학교 유형 결정
     */
    private String determineSchoolType(int age) {
        if (age <= 7) {
            return "미취학";
        } else if (age <= 13) {
            return "초등학교";
        } else if (age <= 16) {
            return "중학교";
        } else if (age <= 19) {
            return "고등학교";
        } else {
            return "성인";
        }
    }
    
    /**
     * 사용자의 총 자산 계산 (연결된 계좌 잔액 합계)
     */
    private Long calculateTotalAssets(Long userId) {
        try {
            return accountRepository.findByUserIdAndIsActiveTrue(userId)
                    .stream()
                    .map(account -> account.getBalance() != null ? account.getBalance().longValue() : 0L)
                    .reduce(0L, Long::sum);
        } catch (Exception e) {
            log.warn("자산 계산 실패: {}", e.getMessage());
            return 0L;
        }
    }
    
    /**
     * 모임통장 보유 여부 확인
     */
    private Boolean hasGroupAccount(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return false;
            return !groupAccountRepository.findByCreatorOrderByCreatedAtDesc(user).isEmpty();
        } catch (Exception e) {
            log.warn("모임통장 보유 여부 확인 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 적금 보유 여부 확인
     */
    private Boolean hasSavingsAccount(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return false;
            return !savingDepositRepository.findByUserOrderByDepositDateDesc(user).isEmpty();
        } catch (Exception e) {
            log.warn("적금 보유 여부 확인 실패: {}", e.getMessage());
            return false;
        }
    }
}
