package com.hanaTI.HanaFuture.domain.welfare.dto;

import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
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
public class WelfareBenefitResponse {
    
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
    private String recommendationReason; // 추천 이유
    
    public static WelfareBenefitResponse from(WelfareBenefit benefit) {
        return WelfareBenefitResponse.builder()
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
                .build();
    }
    
    /**
     * HanaFuture 혜택 변환 메서드
     */
    public static WelfareBenefitResponse fromHanaFuture(HanaFutureWelfareBenefit benefit) {
        // WelfareType을 문자열로 변환 (HanaFutureWelfareBenefit는 String으로 저장)
        WelfareType type;
        try {
            type = WelfareType.valueOf(benefit.getServiceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            type = WelfareType.CENTRAL; // 기본값
        }
        
        return WelfareBenefitResponse.builder()
                .id(benefit.getId())
                .serviceId(benefit.getServiceId())
                .serviceName(benefit.getServiceName())
                .serviceType(type)
                .serviceTypeDisplayName(type.getDisplayName())
                .lifeCycle(benefit.getLifeCycle())
                .category(benefit.getCategory())
                .jurisdictionName(benefit.getOperatingInstitution()) // jurisdictionName -> operatingInstitution
                .areaCode(benefit.getRegionCode()) // areaCode -> regionCode
                .sidoName(benefit.getSidoName())
                .sigunguName(benefit.getSigunguName())
                .targetDescription(benefit.getTargetDescription()) // serviceTargetDescription -> targetDescription
                .serviceContent(benefit.getServiceContent())
                .applicationMethod(benefit.getApplicationMethod())
                .inquiryUrl(benefit.getRelatedUrl()) // inquiryUrl -> relatedUrl
                .supportAmount(benefit.getSupportAmount())
                .supportAmountDescription(benefit.getSupportType()) // supportDescription -> supportType
                .lastSyncedAt(benefit.getCreatedAt())
                .build();
    }
}
