package com.hana.hanabank.domain.savings.service;

import com.hana.hanabank.domain.savings.dto.SavingsProductResponse;
import com.hana.hanabank.domain.savings.dto.SavingsRecommendationRequest;
import com.hana.hanabank.domain.savings.entity.SavingsProduct;
import com.hana.hanabank.domain.savings.repository.SavingsProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SavingsProductService {

    private final SavingsProductRepository savingsProductRepository;

    public List<SavingsProductResponse> getAllActiveProducts() {
        List<SavingsProduct> products = savingsProductRepository.findByIsActiveTrueOrderByMaxInterestRateDesc();
        return products.stream()
                .map(SavingsProductResponse::from)
                .collect(Collectors.toList());
    }

    public List<SavingsProductResponse> getRecommendedProducts(SavingsRecommendationRequest request) {
        log.info("적금 상품 추천 요청: userId={}, monthlyAmount={}, targetCustomer={}", 
                request.getUserId(), request.getMonthlyAmount(), request.getTargetCustomer());

        List<SavingsProduct> products;

        // 타겟 고객군이 있으면 해당 상품 우선 추천
        if (request.getTargetCustomer() != null && !request.getTargetCustomer().isEmpty()) {
            products = savingsProductRepository.findByTargetCustomerContaining(request.getTargetCustomer());
            if (!products.isEmpty()) {
                return products.stream()
                        .limit(3) // 최대 3개까지 추천
                        .map(SavingsProductResponse::from)
                        .collect(Collectors.toList());
            }
        }

        // 월 납입금액 기준으로 추천
        if (request.getMonthlyAmount() != null) {
            products = savingsProductRepository.findByMonthlyAmountRange(request.getMonthlyAmount());
            if (!products.isEmpty()) {
                return products.stream()
                        .limit(3)
                        .map(SavingsProductResponse::from)
                        .collect(Collectors.toList());
            }
        }

        // 기본 추천 (금리 높은 순)
        products = savingsProductRepository.findByIsActiveTrueOrderByMaxInterestRateDesc();
        return products.stream()
                .limit(3)
                .map(SavingsProductResponse::from)
                .collect(Collectors.toList());
    }

    public SavingsProductResponse getProductById(Long productId) {
        SavingsProduct product = savingsProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("적금 상품을 찾을 수 없습니다: " + productId));
        return SavingsProductResponse.from(product);
    }
}
