package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.service.ChildcareWelfareService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Childcare Welfare", description = "육아 특화 복지 혜택 API")
@RestController
@RequestMapping("/api/childcare-welfare")
@RequiredArgsConstructor
public class ChildcareWelfareController {
    
    private final ChildcareWelfareService childcareWelfareService;
    
    @Operation(
            summary = "육아 관련 복지서비스 조회",
            description = "HanaFuture 서비스에 맞는 육아 관련 복지서비스를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getChildcareWelfare(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getChildcareWelfareBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("육아 관련 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "임신·출산 복지서비스",
            description = "임신·출산 관련 복지서비스를 조회합니다."
    )
    @GetMapping("/pregnancy")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getPregnancyBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getPregnancyBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("임신·출산 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "영유아 복지서비스",
            description = "영유아 관련 복지서비스를 조회합니다."
    )
    @GetMapping("/infant")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getInfantBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getInfantBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("영유아 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "보육 복지서비스",
            description = "보육 관련 복지서비스를 조회합니다."
    )
    @GetMapping("/childcare")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getChildcareBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getChildcareBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("보육 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "교육 복지서비스",
            description = "교육 관련 복지서비스를 조회합니다."
    )
    @GetMapping("/education")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getEducationBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getEducationBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("교육 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "높은 지원금 복지서비스",
            description = "지원금액이 높은 순으로 육아 복지서비스를 조회합니다."
    )
    @GetMapping("/top-support")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getTopSupportBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getTopSupportAmountBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("높은 지원금 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "맞춤 복지서비스 추천",
            description = "사용자 정보에 따른 맞춤 복지서비스를 추천합니다."
    )
    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getRecommendedBenefits(
            @Parameter(description = "나이", example = "30") @RequestParam(required = false) Integer age,
            @Parameter(description = "자녀 유무", example = "true") @RequestParam(required = false) Boolean hasChild,
            @Parameter(description = "임신 여부", example = "false") @RequestParam(required = false) Boolean isPregnant,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.getRecommendedBenefits(
                age, hasChild, isPregnant, page, size);
        return ResponseEntity.ok(ApiResponse.success("맞춤 복지 혜택을 추천했습니다.", benefits));
    }
    
    @Operation(
            summary = "육아 키워드 검색",
            description = "육아 관련 키워드로 복지서비스를 검색합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> searchChildcareKeyword(
            @Parameter(description = "검색 키워드", example = "부모급여") @RequestParam String keyword,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = childcareWelfareService.searchChildcareWelfare(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("'" + keyword + "' 검색 결과입니다.", benefits));
    }
}
