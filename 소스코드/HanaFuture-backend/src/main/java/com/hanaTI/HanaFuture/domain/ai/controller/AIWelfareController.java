package com.hanaTI.HanaFuture.domain.ai.controller;

import com.hanaTI.HanaFuture.domain.ai.service.GeminiWelfareRecommendationService;
import com.hanaTI.HanaFuture.domain.ai.service.BackgroundAIRecommendationService;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "AI Welfare Recommendation", description = "AI 기반 맞춤형 복지 혜택 추천 API")
@RestController
@RequestMapping("/api/ai/welfare")
@RequiredArgsConstructor
public class AIWelfareController {
    
    private final GeminiWelfareRecommendationService geminiWelfareRecommendationService;
    private final BackgroundAIRecommendationService backgroundAIService;
    
    @Operation(
            summary = "AI 맞춤형 복지 혜택 추천",
            description = "Gemini AI를 활용하여 사용자 프로필에 맞는 복지 혜택을 추천합니다."
    )
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getAIRecommendations(
            @Parameter(description = "사용자 ID (선택사항, 미입력시 로그인 사용자)", required = false)
            @RequestParam(required = false) Long userId,
            @Parameter(description = "페이지 번호", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") 
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            // 사용자 ID가 없으면 현재 로그인 사용자 사용
            if (userId == null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    userId = getCurrentUserId(auth);
                } else {
                    userId = 1L; // 기본 사용자 (테스트용)
                }
            }
            
            log.info("사용자 {}에 대한 AI 복지 추천 요청", userId);
            
            // 실시간 AI 추천 실행 (hana_future_welfare_benefits 기반)
            Page<WelfareBenefitResponse> recommendations = 
                    geminiWelfareRecommendationService.getPersonalizedWelfareRecommendations(
                            userId, page, size);
            
            log.info("AI 복지 추천 완료: {}개 결과", recommendations.getNumberOfElements());
            
            return ResponseEntity.ok(ApiResponse.success(
                    "AI 맞춤형 복지 혜택을 성공적으로 추천했습니다.", 
                    recommendations));
            
        } catch (Exception e) {
            log.error("AI 복지 추천 실패: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(
                    "AI 추천 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }
    
    @Operation(
            summary = "AI 복지 추천 테스트",
            description = "간단한 사용자 정보로 AI 추천을 테스트합니다."
    )
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> testAIRecommendation(
            @Parameter(description = "나이") @RequestParam Integer age,
            @Parameter(description = "자녀 수") @RequestParam(defaultValue = "0") Integer childrenCount,
            @Parameter(description = "지역") @RequestParam(defaultValue = "서울특별시") String region,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        try {
            log.info("AI 복지 추천 테스트 - 나이: {}, 자녀수: {}, 지역: {}", age, childrenCount, region);
            
            // 테스트용 사용자 ID (1번)로 추천 실행
            Page<WelfareBenefitResponse> recommendations = 
                    geminiWelfareRecommendationService.getPersonalizedWelfareRecommendations(
                            1L, page, size);
            
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("테스트 완료: %d세, 자녀 %d명, %s 지역 기준", age, childrenCount, region), 
                    recommendations));
            
        } catch (Exception e) {
            log.error("AI 복지 추천 테스트 실패: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(
                    "AI 추천 테스트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(Authentication auth) {
        // JWT에서 사용자 ID 추출
        try {
            return Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            return 1L; // 기본값
        }
    }
}
