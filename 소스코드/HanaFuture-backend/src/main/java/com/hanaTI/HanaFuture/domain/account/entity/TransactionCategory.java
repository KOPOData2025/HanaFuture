package com.hanaTI.HanaFuture.domain.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionCategory {
    // 생활비
    GROCERIES("GROCERIES", "식료품", ""),
    UTILITIES("UTILITIES", "공과금", ""),
    TRANSPORTATION("TRANSPORTATION", "교통비", ""),
    DINING("DINING", "외식", ""),
    
    // 육아 관련
    CHILDCARE("CHILDCARE", "육아용품", ""),
    EDUCATION("EDUCATION", "교육비", ""),
    MEDICAL("MEDICAL", "의료비", ""),
    
    // 주거 관련
    RENT("RENT", "월세/관리비", ""),
    HOUSEHOLD("HOUSEHOLD", "생활용품", ""),
    
    // 수입
    INCOME("INCOME", "수입", ""),
    GOVERNMENT_SUPPORT("GOVERNMENT_SUPPORT", "정부지원금", ""),
    
    // 쇼핑
    SHOPPING("SHOPPING", "쇼핑", ""),
    
    // 기타
    ENTERTAINMENT("ENTERTAINMENT", "여가/문화", ""),
    CLOTHING("CLOTHING", "의류", ""),
    SAVINGS("SAVINGS", "적금/저축", ""),
    ALLOWANCE("ALLOWANCE", "용돈", ""),
    OTHER("OTHER", "기타", "");
    
    private final String key;
    private final String description;
    private final String emoji;
}

