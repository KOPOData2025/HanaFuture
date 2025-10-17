package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.service.PersonalizedWelfareService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Personalized Welfare", description = "개인 맞춤 복지 혜택 추천 API")
@RestController
@RequestMapping("/api/personalized-welfare")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PersonalizedWelfareController {
    
    private final PersonalizedWelfareService personalizedWelfareService;
    
    @Operation(
            summary = "개인 맞춤 복지 혜택 추천",
            description = "사용자의 개인정보를 기반으로 가장 적합한 복지 혜택을 추천합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "맞춤 추천 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getPersonalizedRecommendations(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        Page<WelfareBenefitResponse> recommendations = personalizedWelfareService
                .getPersonalizedRecommendations(userDetails.getUsername(), page, size);
        
        return ResponseEntity.ok(ApiResponse.success(
                "맞춤형 복지 혜택을 추천했습니다.", recommendations));
    }
    
    @Operation(
            summary = "생애주기별 맞춤 혜택",
            description = "사용자의 생애주기에 맞는 복지 혜택을 추천합니다."
    )
    @GetMapping("/lifecycle-recommendations")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getLifeCycleRecommendations(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        Page<WelfareBenefitResponse> recommendations = personalizedWelfareService
                .getLifeCycleRecommendations(userDetails.getUsername(), page, size);
        
        return ResponseEntity.ok(ApiResponse.success(
                "생애주기별 복지 혜택을 추천했습니다.", recommendations));
    }
    
    @Operation(
            summary = "지역 기반 맞춤 혜택",
            description = "사용자의 거주지역에 맞는 지자체 복지 혜택을 추천합니다."
    )
    @GetMapping("/regional-recommendations")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getRegionalRecommendations(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        Page<WelfareBenefitResponse> recommendations = personalizedWelfareService
                .getRegionalRecommendations(userDetails.getUsername(), page, size);
        
        return ResponseEntity.ok(ApiResponse.success(
                "지역 맞춤 복지 혜택을 추천했습니다.", recommendations));
    }
    
    @Operation(
            summary = "추천 이유 설명",
            description = "특정 복지 혜택이 추천된 이유를 설명합니다."
    )
    @GetMapping("/recommendation-reason/{benefitId}")
    public ResponseEntity<ApiResponse<String>> getRecommendationReason(
            @Parameter(description = "복지 혜택 ID", example = "1") @PathVariable Long benefitId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }

        String reason = generateRecommendationReason(userDetails.getUsername(), benefitId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "추천 이유를 조회했습니다.", reason));
    }

    private String generateRecommendationReason(String userEmail, Long benefitId) {
        return "귀하의 가족 구성과 거주지역을 고려하여 추천된 혜택입니다.";
    }
}
