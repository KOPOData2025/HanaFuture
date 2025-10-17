package com.hanaTI.HanaFuture.domain.welfare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 하나퓨처 맞춤 복지 혜택 엔티티
 * - Gemini AI로 필터링된 하나퓨처 서비스에 적합한 복지 혜택
 * - welfare_benefits와 동일한 구조 + AI 관련 필드 추가
 */
@Entity
@Table(name = "hana_future_welfare_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HanaFutureWelfareBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원본 데이터 참조
    @Column(name = "original_welfare_id")
    private Long originalWelfareId;

    // 기본 정보 (welfare_benefits와 동일)
    @Column(name = "service_id", length = 100)
    private String serviceId;

    @Column(name = "service_name", length = 500, nullable = false)
    private String serviceName;

    @Column(name = "service_type", length = 50, nullable = false)
    private String serviceType;

    @Lob
    @Column(name = "service_purpose")
    private String servicePurpose;

    @Lob
    @Column(name = "service_content")
    private String serviceContent;

    @Lob
    @Column(name = "selection_criteria")
    private String selectionCriteria;

    // 지원 정보
    @Column(name = "support_type", length = 100)
    private String supportType;

    @Column(name = "support_amount")
    private Long supportAmount;

    @Lob
    @Column(name = "application_method")
    private String applicationMethod;

    @Column(name = "application_deadline", length = 200)
    private String applicationDeadline;

    @Lob
    @Column(name = "required_documents")
    private String requiredDocuments;

    @Column(name = "reception_agency", length = 200)
    private String receptionAgency;

    @Column(name = "contact_info", length = 200)
    private String contactInfo;

    @Column(name = "related_url", length = 500)
    private String relatedUrl;

    // 분류 정보
    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "life_cycle", length = 100)
    private String lifeCycle;

    @Lob
    @Column(name = "target_description")
    private String targetDescription;

    // 지역 정보
    @Column(name = "region_code", length = 20)
    private String regionCode;

    @Column(name = "region_name", length = 100)
    private String regionName;

    // 추가 정보
    @Column(name = "application_site_url", length = 500)
    private String applicationSiteUrl;

    @Column(name = "reference_url_1", length = 500)
    private String referenceUrl1;

    @Column(name = "reference_url_2", length = 500)
    private String referenceUrl2;

    @Column(name = "operating_institution", length = 200)
    private String operatingInstitution;

    @Column(name = "sido_name", length = 50)
    private String sidoName;

    @Column(name = "sigungu_name", length = 50)
    private String sigunguName;

    // AI 필터링 정보
    @Column(name = "ai_filtered_at")
    @Builder.Default
    private LocalDateTime aiFilteredAt = LocalDateTime.now();

    @Column(name = "ai_relevance_score", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal aiRelevanceScore = BigDecimal.ONE;

    @Column(name = "ai_filter_reason", length = 500)
    private String aiFilterReason;

    // 상태 정보
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * 원본 WelfareBenefit에서 HanaFutureWelfareBenefit으로 변환 (실제 존재하는 필드만)
     */
    public static HanaFutureWelfareBenefit fromOriginal(WelfareBenefit original, String aiFilterReason) {
        return HanaFutureWelfareBenefit.builder()
                .originalWelfareId(original.getId())
                .serviceId(original.getServiceId())
                .serviceName(original.getServiceName())
                .serviceType(original.getServiceType() != null ? original.getServiceType().name() : null)
                .serviceContent(original.getServiceContent())
                .applicationMethod(original.getApplicationMethod())
                .supportAmount(original.getSupportAmount())
                .category(original.getCategory())
                .lifeCycle(original.getLifeCycle())
                .targetDescription(original.getTargetDescription())
                .regionCode(original.getAreaCode()) // areaCode를 regionCode로 매핑
                .sidoName(original.getSidoName())
                .sigunguName(original.getSigunguName())
                .relatedUrl(original.getInquiryUrl()) // inquiryUrl을 relatedUrl로 매핑
                .receptionAgency(original.getJurisdictionName()) // jurisdictionName을 receptionAgency로 매핑
                .isActive(original.getIsActive() != null ? original.getIsActive() : true)
                .aiFilterReason(aiFilterReason)
                .build();
    }

    /**
     * AI 관련도 점수 업데이트
     */
    public void updateRelevanceScore(BigDecimal score) {
        this.aiRelevanceScore = score;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}