package com.hanaTI.HanaFuture.domain.banking.controller;

import com.hanaTI.HanaFuture.domain.banking.dto.ChildcareSavingsRequest;
import com.hanaTI.HanaFuture.domain.banking.dto.SavingsProductResponse;
import com.hanaTI.HanaFuture.domain.banking.service.SavingsProductService;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import com.hanaTI.HanaFuture.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Savings Products", description = "적금 상품 관리 API")
public class SavingsProductController {

    private final SavingsProductService savingsProductService;

    @Operation(summary = "전체 적금 상품 조회", description = "하나은행의 모든 적금 상품을 조회합니다.")
    @GetMapping("/products")
    public ApiResponse<List<SavingsProductResponse>> getAllSavingsProducts() {
        List<SavingsProductResponse> products = savingsProductService.getAllSavingsProducts();
        return ApiResponse.success(products);
    }

    @Operation(summary = "육아 적금 상품 추천", description = "육아 관련 적금 상품을 추천합니다.")
    @GetMapping("/products/childcare")
    public ApiResponse<List<SavingsProductResponse>> getChildcareProducts(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        List<SavingsProductResponse> products = savingsProductService.getChildcareProductRecommendations(currentUser.getId());
        return ApiResponse.success(products);
    }

    @Operation(summary = "개인 맞춤 적금 상품 추천", description = "사용자 프로필 기반으로 적금 상품을 추천합니다.")
    @GetMapping("/products/recommendations")
    public ApiResponse<List<SavingsProductResponse>> getPersonalizedRecommendations(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        // 사용자 프로필 정보 구성 (실제로는 User 엔티티에서 가져와야 함)
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("age", calculateAge(currentUser));
        userProfile.put("lifeCycle", determineLifeCycle(currentUser));
        
        List<SavingsProductResponse> products = savingsProductService.getPersonalizedRecommendations(
                currentUser.getId(), userProfile);
        return ApiResponse.success(products);
    }

    @Operation(summary = "육아 적금 계좌 생성", description = "육아 적금 계좌를 생성합니다.")
    @PostMapping("/accounts/childcare")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Object>> createChildcareSavings(
            @Valid @RequestBody ChildcareSavingsRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        Map<String, Object> result = savingsProductService.createChildcareSavings(request, currentUser);
        return ApiResponse.success("육아 적금 계좌가 성공적으로 생성되었습니다.", result);
    }

    @Operation(summary = "적금 상품 상세 조회", description = "특정 적금 상품의 상세 정보를 조회합니다.")
    @GetMapping("/products/{productId}")
    public ApiResponse<SavingsProductResponse> getProductDetail(@PathVariable String productId) {
        // 실제로는 하나은행 API에서 상품 상세 정보를 가져와야 함
        List<SavingsProductResponse> allProducts = savingsProductService.getAllSavingsProducts();
        
        SavingsProductResponse product = allProducts.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));
        
        return ApiResponse.success(product);
    }

    @Operation(summary = "적금 상품 비교", description = "여러 적금 상품을 비교합니다.")
    @PostMapping("/products/compare")
    public ApiResponse<List<SavingsProductResponse>> compareProducts(@RequestBody List<String> productIds) {
        List<SavingsProductResponse> allProducts = savingsProductService.getAllSavingsProducts();
        
        List<SavingsProductResponse> compareProducts = allProducts.stream()
                .filter(product -> productIds.contains(product.getProductId()))
                .toList();
        
        return ApiResponse.success(compareProducts);
    }

    // Private helper methods
    
    private Integer calculateAge(User user) {
        return 30;
    }
    
    private String determineLifeCycle(User user) {
        return "YOUNG_ADULT";
    }
}
