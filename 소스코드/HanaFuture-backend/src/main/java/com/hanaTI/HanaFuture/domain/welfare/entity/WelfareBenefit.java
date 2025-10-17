package com.hanaTI.HanaFuture.domain.welfare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "welfare_benefits")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WelfareBenefit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 서비스 ID (API에서 제공하는 고유 식별자)
     */
    @Column(name = "service_id", unique = true, nullable = false)
    private String serviceId;
    
    /**
     * 서비스명
     */
    @Column(name = "service_name", nullable = false)
    private String serviceName;
    
    /**
     * 서비스 유형 (CENTRAL: 중앙정부, LOCAL: 지자체)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private WelfareType serviceType;
    
    /**
     * 생애주기 (임신출산, 영유아, 아동·청소년 등)
     */
    @Column(name = "life_cycle")
    private String lifeCycle;
    
    /**
     * 서비스 분류 (보육·교육, 출산, 양육 등) - intrsThemaArray에서 추출
     */
    @Column(name = "category")
    private String category;
    
    /**
     * 지원주기 (수시, 분기, 연간 등)
     */
    @Column(name = "support_cycle")
    private String supportCycle;
    
    /**
     * 서비스 제공 형태 (기타, 프로그램/서비스 등)
     */
    @Column(name = "service_provision_type")
    private String serviceProvisionType;
    
    /**
     * 온라인 신청 가능 여부
     */
    @Column(name = "online_application_possible")
    private String onlineApplicationPossible;
    
    /**
     * 서비스 최초 등록일
     */
    @Column(name = "service_first_registration_date")
    private String serviceFirstRegistrationDate;
    
    /**
     * 소관기관명
     */
    @Column(name = "jurisdiction_name")
    private String jurisdictionName;
    
    /**
     * 지역코드 (지자체 서비스의 경우)
     */
    @Column(name = "area_code")
    private String areaCode;
    
    /**
     * 시도명
     */
    @Column(name = "sido_name")
    private String sidoName;
    
    /**
     * 시군구명
     */
    @Column(name = "sigungu_name")
    private String sigunguName;
    
    /**
     * 서비스 대상
     */
    @Lob
    @Column(name = "target_description")
    private String targetDescription;
    
    /**
     * 서비스 내용
     */
    @Lob
    @Column(name = "service_content")
    private String serviceContent;
    
    /**
     * 신청방법
     */
    @Lob
    @Column(name = "application_method")
    private String applicationMethod;
    
    /**
     * 문의처 URL
     */
    @Column(name = "inquiry_url")
    private String inquiryUrl;
    
    /**
     * 지원금액 (파싱된 숫자, 없으면 null)
     */
    @Column(name = "support_amount")
    private Long supportAmount;
    
    /**
     * 지원금액 설명 (원문)
     */
    @Column(name = "support_amount_description")
    private String supportAmountDescription;
    
    /**
     * 활성 상태
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 데이터 마지막 동기화 시간
     */
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
    
    public void updateFromApi(String serviceName, String lifeCycle, String category,
                             String jurisdictionName, String targetDescription, 
                             String serviceContent, String applicationMethod, 
                             String inquiryUrl, String supportCycle, String serviceProvisionType,
                             String onlineApplicationPossible, String serviceFirstRegistrationDate) {
        this.serviceName = serviceName;
        this.lifeCycle = lifeCycle;
        this.category = category;
        this.jurisdictionName = jurisdictionName;
        this.targetDescription = targetDescription;
        this.serviceContent = serviceContent;
        this.applicationMethod = applicationMethod;
        this.inquiryUrl = inquiryUrl;
        this.supportCycle = supportCycle;
        this.serviceProvisionType = serviceProvisionType;
        this.onlineApplicationPossible = onlineApplicationPossible;
        this.serviceFirstRegistrationDate = serviceFirstRegistrationDate;
        this.lastSyncedAt = LocalDateTime.now();
    }
    
    public void updateSupportAmount(Long amount, String description) {
        this.supportAmount = amount;
        this.supportAmountDescription = description;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
}
