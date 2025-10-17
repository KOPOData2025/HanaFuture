package com.hanaTI.HanaFuture.domain.welfare.dto.external;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "wantedList")
public class WelfareApiResponse {
    
    @JacksonXmlProperty(localName = "totalCount")
    private Integer totalCount;
    
    @JacksonXmlProperty(localName = "pageNo")
    private Integer pageNo;
    
    @JacksonXmlProperty(localName = "numOfRows")
    private Integer numOfRows;
    
    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;
    
    @JacksonXmlProperty(localName = "resultMessage")
    private String resultMessage;
    
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "servList")
    private List<WelfareItem> servList;
    
    // 응답 성공 여부 확인
    public boolean isSuccess() {
        return "0".equals(resultCode);
    }
    
    @Data
    public static class WelfareItem {
        // 중앙정부 API 응답 필드 (실제 명세서 기준)
        @JacksonXmlProperty(localName = "servId")
        private String serviceId;
        
        @JacksonXmlProperty(localName = "servNm")
        private String serviceName;
        
        @JacksonXmlProperty(localName = "lifeArray")
        private String lifeCycle;
        
        @JacksonXmlProperty(localName = "servDgst")
        private String serviceDigest; // 서비스 요약
        
        @JacksonXmlProperty(localName = "servDtlLink")
        private String serviceDetailLink;
        
        @JacksonXmlProperty(localName = "trgterIndvdlArray")
        private String targetDescription;
        
        @JacksonXmlProperty(localName = "intrsThemaArray")
        private String interestTheme; // 관심주제
        
        @JacksonXmlProperty(localName = "inqNum")
        private String inquiryNumber;
        
        @JacksonXmlProperty(localName = "rprsCtadr")
        private String representativeContact;
        
        // 중앙부처 서비스 전용 필드
        @JacksonXmlProperty(localName = "jurMnofNm")
        private String jurisdictionName; // 소관부처명
        
        @JacksonXmlProperty(localName = "jurOrgNm")
        private String jurisdictionOrgName; // 소관조직명
        
        @JacksonXmlProperty(localName = "onapPsbltYn")
        private String onlineApplicationPossible; // 온라인신청가능여부
        
        @JacksonXmlProperty(localName = "sprtCycNm")
        private String supportCycle; // 지원주기
        
        @JacksonXmlProperty(localName = "srvPvsnNm")
        private String serviceProvisionType; // 서비스 제공 형태
        
        @JacksonXmlProperty(localName = "svcfrstRegTs")
        private String serviceFirstRegistrationDate; // 서비스 최초 등록일
        
        // 지자체 서비스 전용 필드 (향후 지자체 API 연동 시 사용)
        @JacksonXmlProperty(localName = "ctprvnNm")
        private String sidoName; // 시도명
        
        @JacksonXmlProperty(localName = "signguNm")
        private String sigunguName; // 시군구명
        
        @JacksonXmlProperty(localName = "ctprvnCd")
        private String sidoCode; // 시도코드
        
        @JacksonXmlProperty(localName = "signguCd")
        private String sigunguCode; // 시군구코드
    }
}
