package com.hanaTI.HanaFuture.domain.welfare.dto.external;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "wantedList")
public class LocalWelfareApiResponse {
    
    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;
    
    @JacksonXmlProperty(localName = "resultMessage")
    private String resultMessage;
    
    @JacksonXmlProperty(localName = "numOfRows")
    private String numOfRows;
    
    @JacksonXmlProperty(localName = "pageNo")
    private String pageNo;
    
    @JacksonXmlProperty(localName = "totalCount")
    private String totalCount;
    
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "servList")
    private List<LocalWelfareItem> servList;
    
    // 응답 성공 여부 확인
    public boolean isSuccess() {
        return "0".equals(resultCode);
    }
    
    @Data
    public static class LocalWelfareItem {
        // 기본 정보
        @JacksonXmlProperty(localName = "servId")
        private String serviceId;
        
        @JacksonXmlProperty(localName = "servNm")
        private String serviceName;
        
        // 시행 기간
        @JacksonXmlProperty(localName = "enfcBgngYmd")
        private String enforcementStartDate; // 시행시작일
        
        @JacksonXmlProperty(localName = "enfcEndYmd")
        private String enforcementEndDate; // 시행종료일
        
        // 담당 부서 및 지역 정보
        @JacksonXmlProperty(localName = "bizChrDeptNm")
        private String businessDepartmentName; // 사업담당부서명
        
        @JacksonXmlProperty(localName = "ctpvNm")
        private String sidoName; // 시도명
        
        @JacksonXmlProperty(localName = "sggNm")
        private String sigunguName; // 시군구명
        
        // 서비스 내용
        @JacksonXmlProperty(localName = "servDgst")
        private String serviceDigest; // 서비스요약
        
        @JacksonXmlProperty(localName = "lifeNmArray")
        private String lifeCycleArray; // 생애주기
        
        @JacksonXmlProperty(localName = "trgterIndvdlNmArray")
        private String targetIndividualArray; // 대상자
        
        @JacksonXmlProperty(localName = "intrsThemaNmArray")
        private String interestThemeArray; // 관심주제
        
        // 지원 정보
        @JacksonXmlProperty(localName = "sprtCycNm")
        private String supportCycleName; // 지원주기
        
        @JacksonXmlProperty(localName = "srvPvsnNm")
        private String serviceProvisionName; // 서비스제공유형
        
        @JacksonXmlProperty(localName = "aplyMtdNm")
        private String applicationMethodName; // 신청방법명
        
        // 상세 내용 (상세 조회 시 사용)
        @JacksonXmlProperty(localName = "sprtTrgtCn")
        private String supportTargetContent; // 지원대상내용
        
        @JacksonXmlProperty(localName = "slctCritCn")
        private String selectionCriteriaContent; // 선정기준내용
        
        @JacksonXmlProperty(localName = "alwServCn")
        private String allowServiceContent; // 급여서비스내용
        
        @JacksonXmlProperty(localName = "aplyMtdCn")
        private String applicationMethodContent; // 신청방법내용
        
        // 기타 정보
        @JacksonXmlProperty(localName = "inqNum")
        private String inquiryNumber; // 조회수
        
        @JacksonXmlProperty(localName = "lastModYmd")
        private String lastModifiedDate; // 최종수정일
        
        // 관련 정보 리스트들
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "inqplCtadrList")
        private List<WelfareInfoDetail> inquiryContactList; // 문의처목록
        
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "inqplHmpgReldList")
        private List<WelfareInfoDetail> inquiryHomepageList; // 문의홈페이지목록
        
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "baslawList")
        private List<WelfareInfoDetail> basisLawList; // 근거법령목록
        
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "basfrmList")
        private List<WelfareInfoDetail> basisFormList; // 서식목록
        
        // 서비스 상세 링크 (실제 API 응답에 포함됨)
        @JacksonXmlProperty(localName = "servDtlLink")
        private String serviceDetailLink;
    }
    
    @Data
    public static class WelfareInfoDetail {
        @JacksonXmlProperty(localName = "wlfareInfoDtlCd")
        private String welfareInfoDetailCode; // 복지정보상세코드
        
        @JacksonXmlProperty(localName = "wlfareInfoReldNm")
        private String welfareInfoRelatedName; // 복지정보관련명
        
        @JacksonXmlProperty(localName = "wlfareInfoReldCn")
        private String welfareInfoRelatedContent; // 복지정보관련내용
    }
}
