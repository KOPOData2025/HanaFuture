package com.hana.hanabank.domain.savings.controller;

import com.hana.hanabank.domain.savings.dto.SavingsProductResponse;
import com.hana.hanabank.domain.savings.dto.SavingsRecommendationRequest;
import com.hana.hanabank.domain.savings.service.SavingsProductService;
import com.hana.hanabank.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
@Tag(name = "적금 상품", description = "하나은행 적금 상품 관련 API")
public class SavingsProductController {

    private final SavingsProductService savingsProductService;

    @GetMapping("/products")
    @Operation(summary = "전체 적금 상품 조회", description = "활성화된 모든 적금 상품을 금리 높은 순으로 조회합니다.")
    public ResponseEntity<ApiResponse<List<SavingsProductResponse>>> getAllProducts() {
        List<SavingsProductResponse> products = savingsProductService.getAllActiveProducts();
        return ResponseEntity.ok(ApiResponse.success("적금 상품 조회 성공", products));
    }

    @PostMapping("/recommendations")
    @Operation(summary = "맞춤 적금 상품 추천", description = "사용자 조건에 맞는 적금 상품을 추천합니다.")
    public ResponseEntity<ApiResponse<List<SavingsProductResponse>>> getRecommendations(
            @RequestBody SavingsRecommendationRequest request) {
        List<SavingsProductResponse> recommendations = savingsProductService.getRecommendedProducts(request);
        return ResponseEntity.ok(ApiResponse.success("적금 상품 추천 성공", recommendations));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "적금 상품 상세 조회", description = "특정 적금 상품의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<SavingsProductResponse>> getProductDetail(
            @PathVariable Long productId) {
        SavingsProductResponse product = savingsProductService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success("적금 상품 상세 조회 성공", product));
    }

    // 육아 고객 전용 적금 상품 추천 (기존 API 호환성 유지)
    @GetMapping("/childcare-recommendations")
    @Operation(summary = "육아 적금 상품 추천", description = "육아 고객을 위한 적금 상품을 추천합니다.")
    public ResponseEntity<ApiResponse<List<SavingsProductResponse>>> getChildcareRecommendations() {
        SavingsRecommendationRequest request = new SavingsRecommendationRequest();
        request.setTargetCustomer("육아");
        
        List<SavingsProductResponse> recommendations = savingsProductService.getRecommendedProducts(request);
        return ResponseEntity.ok(ApiResponse.success("육아 적금 상품 추천 성공", recommendations));
    }
}
