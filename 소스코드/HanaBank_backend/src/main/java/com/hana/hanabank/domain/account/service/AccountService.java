package com.hana.hanabank.domain.account.service;

import com.hana.hanabank.domain.account.entity.Account;
import com.hana.hanabank.domain.account.repository.AccountRepository;
import com.hana.hanabank.domain.user.entity.User;
import com.hana.hanabank.domain.user.repository.UserRepository;
import com.hana.hanabank.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    
    /**
     * 사용자 계좌 목록 조회
     */
    public List<Account> getUserAccounts(String userId) {
        return accountRepository.findActiveAccountsByUserId(userId);
    }
    
    /**
     * 계좌 잔액 조회
     */
    public BigDecimal getAccountBalance(String accountNum) {
        Account account = accountRepository.findByAccountNum(accountNum)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));
        return account.getBalanceAmt();
    }
    
    /**
     * 계좌 상세 정보 조회
     */
    public Account getAccountDetail(String accountNum) {
        return accountRepository.findByAccountNum(accountNum)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));
    }
    
    /**
     * CI 기반 사용자 계좌 목록 조회
     */
    public List<Account> getUserAccountsByCi(String userCi) {
        // 먼저 CI로 사용자 찾기
        User user = userRepository.findByUserCi(userCi).orElse(null);
        if (user == null) {
            log.warn("사용자를 찾을 수 없음: CI={}", userCi != null ? userCi.substring(0, 8) + "..." : "null");
            return List.of(); // 빈 리스트 반환
        }
        
        // 사용자의 활성 계좌 목록 조회
        return accountRepository.findActiveAccountsByUserId(user.getUserId());
    }
    
    /**
     * 테스트용 계좌 생성
     */
    @Transactional
    public Account createTestAccount(String userId, String accountNum, String productName, BigDecimal balanceAmt) {
        Account account = Account.builder()
                .accountNum(accountNum)
                .userId(userId)
                .bankCodeStd("081") // 하나은행 코드
                .activityType("1") // 활성
                .accountType("1") // 입출금
                .accountNumMasked(maskAccountNumber(accountNum))
                .accountSeq("01")
                .accountLocalCode("0810001")
                .accountIssueDate(getCurrentDateString())
                .maturityDate(null)
                .lastTranDate(getCurrentDateString())
                .productName(productName)
                .productSubName("입출금")
                .dormancyYn("N")
                .balanceAmt(balanceAmt)
                .depositAmt(BigDecimal.ZERO)
                .balanceCalcBasis1("1")
                .balanceCalcBasis2("1")
                .investmentLinkedYn("N")
                .bankLinkedYn("Y")
                .balanceAfterCancelYn("N")
                .savingsBankCode("081")
                .build();
        
        return accountRepository.save(account);
    }
    @Transactional
    public Account createChildcareSavings(String userId, String productType, BigDecimal monthlyAmount, Integer periodMonths) {
        // 상품명 매핑
        Map<String, String> productNames = new HashMap<>();
        productNames.put("CHILDCARE_SAVINGS", "하나 육아적금");
        productNames.put("PREGNANCY_SAVINGS", "하나 임신적금");
        productNames.put("HOUSING_SAVINGS", "하나 주택적금");
        
        String productName = productNames.getOrDefault(productType, "하나 육아적금");
        
        // 계좌번호 생성 (간단한 형태)
        String accountNum = generateAccountNumber();
        
        Account account = Account.builder()
                .accountNum(accountNum)
                .userId(userId)
                .bankCodeStd("081") // 하나은행 코드
                .activityType("1") // 활성
                .accountType("2") // 적금
                .accountNumMasked(maskAccountNumber(accountNum))
                .accountSeq("01")
                .accountLocalCode("0810001")
                .accountIssueDate(getCurrentDateString())
                .maturityDate(getMaturityDate(periodMonths))
                .lastTranDate(getCurrentDateString())
                .productName(productName)
                .productSubName("적금")
                .dormancyYn("N")
                .balanceAmt(BigDecimal.ZERO)
                .depositAmt(monthlyAmount)
                .balanceCalcBasis1("1")
                .balanceCalcBasis2("1")
                .investmentLinkedYn("N")
                .bankLinkedYn("Y")
                .balanceAfterCancelYn("N")
                .savingsBankCode("081")
                .build();
        
        return accountRepository.save(account);
    }
    
    /**
     * 육아 상품 추천
     */
    public List<Map<String, Object>> getChildcareProductRecommendations(String userId) {
        List<Map<String, Object>> recommendations = List.of(
            createProductRecommendation("CHILDCARE_SAVINGS", "하나 육아적금", "자녀 양육비 마련을 위한 특화 적금", 
                "월 50만원", "5년", "연 3.5%", "세액공제 혜택"),
            createProductRecommendation("PREGNANCY_SAVINGS", "하나 임신적금", "출산 준비를 위한 단기 적금", 
                "월 30만원", "10개월", "연 3.2%", "출산 축하금"),
            createProductRecommendation("HOUSING_SAVINGS", "하나 주택적금", "내 집 마련을 위한 장기 적금", 
                "월 80만원", "7년", "연 3.8%", "주택청약 우대")
        );
        
        return recommendations;
    }
    
    private Map<String, Object> createProductRecommendation(String type, String name, String description, 
                                                           String monthlyAmount, String period, String interestRate, String benefit) {
        Map<String, Object> product = new HashMap<>();
        product.put("productType", type);
        product.put("productName", name);
        product.put("description", description);
        product.put("monthlyAmount", monthlyAmount);
        product.put("period", period);
        product.put("interestRate", interestRate);
        product.put("specialBenefit", benefit);
        return product;
    }
    
    private String generateAccountNumber() {
        return "81700" + System.currentTimeMillis() % 10000000;
    }
    
    private String maskAccountNumber(String accountNum) {
        if (accountNum.length() < 8) return accountNum;
        return accountNum.substring(0, 4) + "****" + accountNum.substring(accountNum.length() - 4);
    }
    
    private String getCurrentDateString() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    
    private String getMaturityDate(Integer periodMonths) {
        return java.time.LocalDate.now().plusMonths(periodMonths)
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
