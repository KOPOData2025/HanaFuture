package com.hanaTI.HanaFuture.domain.banking.service;

import com.hanaTI.HanaFuture.domain.banking.dto.ChildcareSavingsRequest;
import com.hanaTI.HanaFuture.domain.banking.dto.SavingsProductResponse;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.service.UserBankAccountService;
import com.hanaTI.HanaFuture.global.exception.BusinessException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsProductService {

    private final HanaBankApiClient hanaBankApiClient;
    private final UserBankAccountService userBankAccountService;

    /**
     * 육아 적금 상품 추천 조회
     */
    public List<SavingsProductResponse> getChildcareProductRecommendations(Long userId) {
        try {
            Mono<Map<String, Object>> responseMono = hanaBankApiClient.getChildcareProductRecommendations(userId.toString());
            Map<String, Object> response = responseMono.block();
            
            if (response != null && (Boolean) response.get("success")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("data");
                
                return products.stream()
                        .map(this::mapToSavingsProductResponse)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("육아 상품 추천 조회 실패: userId={}", userId, e);
        }
        
        // 실패 시 기본 상품 반환
        return getDefaultChildcareProducts();
    }

    /**
     * 육아 적금 계좌 생성
     */
    public Map<String, Object> createChildcareSavings(ChildcareSavingsRequest request, User user) {
        try {
            // 1. 출금계좌 유효성 검증
            validateWithdrawAccount(user, request.getWithdrawAccountId());
            
            // 2. 하나은행 API로 적금 계좌 생성
            Mono<Map<String, Object>> responseMono = hanaBankApiClient.createChildcareSavings(
                    user.getId().toString(),
                    request.getProductType(),
                    request.getMonthlyAmount().doubleValue(),
                    request.getPeriodMonths()
            );
            
            Map<String, Object> response = responseMono.block();
            
            if (response != null && (Boolean) response.get("success")) {
                // 3. 자동이체 설정 (실제로는 은행 API 호출)
                if (request.getAutoTransfer()) {
                    setupAutoTransfer(user, request);
                }
                
                log.info("육아 적금 계좌 생성 성공: userId={}, productType={}, withdrawAccountId={}", 
                        user.getId(), request.getProductType(), request.getWithdrawAccountId());
                return response;
            }
        } catch (BusinessException e) {
            throw e; // 비즈니스 예외는 그대로 전파
        } catch (Exception e) {
            log.error("육아 적금 계좌 생성 실패: userId={}, request={}", user.getId(), request, e);
        }
        
        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
    }

    /**
     * 모든 적금 상품 조회
     */
    public List<SavingsProductResponse> getAllSavingsProducts() {
        // 실제로는 하나은행 API에서 전체 상품 목록을 가져와야 함
        return getDefaultSavingsProducts();
    }

    /**
     * 사용자 맞춤 적금 상품 추천
     */
    public List<SavingsProductResponse> getPersonalizedRecommendations(Long userId, Map<String, Object> userProfile) {
        // 사용자 프로필 기반 추천 로직
        // 나이, 소득, 가족 구성 등을 고려한 상품 추천
        
        List<SavingsProductResponse> allProducts = getAllSavingsProducts();

        return allProducts.stream()
                .filter(product -> isRecommendedForUser(product, userProfile))
                .limit(3)
                .collect(Collectors.toList());
    }

    private SavingsProductResponse mapToSavingsProductResponse(Map<String, Object> productData) {
        @SuppressWarnings("unchecked")
        List<String> benefits = (List<String>) productData.get("benefits");
        
        return SavingsProductResponse.builder()
                .productId((String) productData.get("productId"))
                .productName((String) productData.get("productName"))
                .description((String) productData.get("description"))
                .interestRate(((Number) productData.get("interestRate")).doubleValue())
                .minAmount(((Number) productData.get("minAmount")).longValue())
                .maxAmount(((Number) productData.get("maxAmount")).longValue())
                .period((String) productData.get("period"))
                .benefits(benefits)
                .build();
    }

    private List<SavingsProductResponse> getDefaultChildcareProducts() {
        return List.of(
            SavingsProductResponse.builder()
                    .productId("CHILD_001")
                    .productName("하나 육아 드림 적금")
                    .description("자녀 양육비 마련을 위한 특별 적금")
                    .interestRate(3.5)
                    .minAmount(100000L)
                    .maxAmount(1000000L)
                    .period("12-60개월")
                    .benefits(List.of("정부 지원금 연계", "세제 혜택", "육아용품 할인"))
                    .build(),
            SavingsProductResponse.builder()
                    .productId("CHILD_002")
                    .productName("하나 아이사랑 통장")
                    .description("아이 미래를 위한 자유적금")
                    .interestRate(3.2)
                    .minAmount(50000L)
                    .maxAmount(500000L)
                    .period("6-36개월")
                    .benefits(List.of("출산 축하금", "교육비 지원", "의료비 할인"))
                    .build()
        );
    }

    private List<SavingsProductResponse> getDefaultSavingsProducts() {
        return List.of(
            SavingsProductResponse.builder()
                    .productId("SAV_001")
                    .productName("하나 자유적금")
                    .description("자유롭게 입출금 가능한 적금")
                    .interestRate(3.0)
                    .minAmount(10000L)
                    .maxAmount(10000000L)
                    .period("6-60개월")
                    .benefits(List.of("자유 입출금", "우대 금리"))
                    .build(),
            SavingsProductResponse.builder()
                    .productId("SAV_002")
                    .productName("하나 정기적금")
                    .description("매월 일정 금액 적립하는 정기적금")
                    .interestRate(3.3)
                    .minAmount(50000L)
                    .maxAmount(5000000L)
                    .period("12-36개월")
                    .benefits(List.of("고정 금리", "만기 보장"))
                    .build(),
            SavingsProductResponse.builder()
                    .productId("SAV_003")
                    .productName("하나 청년 희망적금")
                    .description("청년층을 위한 특별 적금")
                    .interestRate(4.0)
                    .minAmount(100000L)
                    .maxAmount(2000000L)
                    .period("24-36개월")
                    .benefits(List.of("청년 우대금리", "정부 지원", "취업 축하금"))
                    .build()
        );
    }

    private boolean isRecommendedForUser(SavingsProductResponse product, Map<String, Object> userProfile) {
        // 간단한 추천 로직
        Integer age = (Integer) userProfile.get("age");
        String lifeCycle = (String) userProfile.get("lifeCycle");
        
        if (age != null && age < 35 && product.getProductId().contains("청년")) {
            return true;
        }
        
        if ("CHILDCARE".equals(lifeCycle) && product.getProductId().contains("CHILD")) {
            return true;
        }
        
        return product.getInterestRate() >= 3.0; // 기본적으로 금리 3% 이상 상품 추천
    }
    
    /**
     * 출금계좌 유효성 검증
     */
    private void validateWithdrawAccount(User user, Long withdrawAccountId) {
        var withdrawableAccounts = userBankAccountService.getWithdrawableAccounts(user);
        
        boolean isValidAccount = withdrawableAccounts.stream()
                .anyMatch(account -> account.getId().equals(withdrawAccountId));
        
        if (!isValidAccount) {
            throw new BusinessException(ErrorCode.INVALID_ACCOUNT);
        }
    }
    
    /**
     * 자동이체 설정
     */
    private void setupAutoTransfer(User user, ChildcareSavingsRequest request) {
        try {
            log.info("자동이체 설정: userId={}, withdrawAccountId={}, amount={}, day={}", 
                    user.getId(), request.getWithdrawAccountId(), 
                    request.getMonthlyAmount(), request.getAutoTransferDay());
            
            // Mock 자동이체 설정 성공
            log.info("자동이체 설정 완료: 매월 {}일에 {}원씩 자동이체", 
                    request.getAutoTransferDay(), request.getMonthlyAmount());
                    
        } catch (Exception e) {
            log.error("자동이체 설정 실패: userId={}", user.getId(), e);
            // 자동이체 설정 실패는 적금 생성을 막지 않음 (경고만)
            log.warn("자동이체 설정에 실패했지만 적금 계좌는 정상 생성되었습니다.");
        }
    }
}
