package com.hanaTI.HanaFuture.domain.welfare.dto;

import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WelfareBenefitDetailResponse {
    
    private Long id;
    private String serviceId;
    private String serviceName;
    private WelfareType serviceType;
    private String serviceTypeDisplayName;
    private String lifeCycle;
    private String category;
    private String jurisdictionName;
    private String areaCode;
    private String sidoName;
    private String sigunguName;
    private String targetDescription;
    private String serviceContent;
    private String applicationMethod;
    private String inquiryUrl;
    private Long supportAmount;
    private String supportAmountDescription;
    private LocalDateTime lastSyncedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 상세 정보
    private String eligibilityCriteria; // 신청 자격
    private String requiredDocuments; // 필요 서류
    private String applicationPeriod; // 신청 기간
    private String contactInfo; // 문의처
    private String relatedLinks; // 관련 링크
    
    public static WelfareBenefitDetailResponse from(WelfareBenefit benefit) {
        return WelfareBenefitDetailResponse.builder()
                .id(benefit.getId())
                .serviceId(benefit.getServiceId())
                .serviceName(benefit.getServiceName())
                .serviceType(benefit.getServiceType())
                .serviceTypeDisplayName(benefit.getServiceType().getDisplayName())
                .lifeCycle(benefit.getLifeCycle())
                .category(benefit.getCategory())
                .jurisdictionName(benefit.getJurisdictionName())
                .areaCode(benefit.getAreaCode())
                .sidoName(benefit.getSidoName())
                .sigunguName(benefit.getSigunguName())
                .targetDescription(benefit.getTargetDescription())
                .serviceContent(benefit.getServiceContent())
                .applicationMethod(benefit.getApplicationMethod())
                .inquiryUrl(benefit.getInquiryUrl())
                .supportAmount(benefit.getSupportAmount())
                .supportAmountDescription(benefit.getSupportAmountDescription())
                .lastSyncedAt(benefit.getLastSyncedAt())
                .createdAt(benefit.getCreatedAt())
                .updatedAt(benefit.getUpdatedAt())
                .build();
    }
    
    /**
     * 지원금액을 포맷된 문자열로 반환
     */
    public String getFormattedSupportAmount() {
        if (supportAmount != null) {
            return String.format("%,d원", supportAmount);
        }
        return supportAmountDescription != null ? supportAmountDescription : "지원금액 정보 없음";
    }
    
    /**
     * 서비스 요약 정보 (카드 표시용)
     */
    public String getServiceSummary() {
        if (serviceContent != null && serviceContent.length() > 100) {
            return serviceContent.substring(0, 100) + "...";
        }
        return serviceContent != null ? serviceContent : targetDescription;
    }
    
    /**
     * 신청 가능 여부 확인
     */
    public boolean isApplicationAvailable() {
        return inquiryUrl != null && !inquiryUrl.trim().isEmpty();
    }
}
