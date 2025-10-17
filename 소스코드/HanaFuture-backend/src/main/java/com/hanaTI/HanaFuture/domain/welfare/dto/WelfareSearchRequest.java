package com.hanaTI.HanaFuture.domain.welfare.dto;

import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WelfareSearchRequest {
    
    /**
     * 서비스 유형 (CENTRAL, LOCAL)
     */
    private WelfareType serviceType;
    
    /**
     * 시도명
     */
    private String sidoName;
    
    /**
     * 시군구명
     */
    private String sigunguName;
    
    /**
     * 생애주기 (임신출산, 영유아 등)
     */
    private String lifeCycle;
    
    /**
     * 서비스 분류
     */
    private String category;
    
    /**
     * 검색 키워드
     */
    private String keyword;
    
    /**
     * 페이지 번호 (0부터 시작)
     */
    private Integer page = 0;
    
    /**
     * 페이지 크기
     */
    private Integer size = 20;
    
    /**
     * 정렬 기준 (createdAt, supportAmount, serviceName 등)
     */
    private String sort = "createdAt";
    
    /**
     * 정렬 방향 (asc, desc)
     */
    private String direction = "desc";
}
