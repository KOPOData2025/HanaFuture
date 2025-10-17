package com.hanaTI.HanaFuture;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class BothWelfareApisTest {
    
    private static final String SERVICE_KEY = "1182635ebadeb891c8ad3ec6807ce8ac12be533f5a30fa665bcaab57321866ae";
    private static final String BASE_URL = "http://apis.data.go.kr";
    
    @Test
    public void testBothCentralAndLocalApis() {
        System.out.println("=== 중앙정부 + 지자체 복지서비스 API 통합 테스트 ===");
        
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                .build();
        
        // 1. 중앙정부 API 테스트
        testCentralApi(webClient);
        
        // 2. 지자체 API 테스트 (서울)
        testLocalApi(webClient);
        
        System.out.println("\n=== 통합 테스트 완료 ===");
    }
    
    private void testCentralApi(WebClient webClient) {
        System.out.println("\n🏛️ === 중앙정부 복지서비스 API 테스트 ===");
        
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001")
                            .queryParam("serviceKey", SERVICE_KEY)
                            .queryParam("callTp", "L")
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 2)
                            .queryParam("srchKeyCode", "001")
                            .queryParam("lifeArray", "007") // 임신·출산
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("✅ 중앙정부 API 호출 성공!");
            
            if (response.contains("<resultCode>0</resultCode>")) {
                String totalCount = extractXmlValue(response, "totalCount");
                System.out.println("📊 중앙정부 임신·출산 관련 서비스: " + totalCount + "건");
                
                String firstServiceName = extractXmlValue(response, "servNm");
                System.out.println("📝 첫 번째 서비스: " + firstServiceName);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 중앙정부 API 호출 실패: " + e.getMessage());
        }
    }
    
    private void testLocalApi(WebClient webClient) {
        System.out.println("\n🏢 === 지자체 복지서비스 API 테스트 (서울) ===");
        
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B554287/LocalGovernmentWelfareInformations/LcgvWelfarelist")
                            .queryParam("serviceKey", SERVICE_KEY)
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 2)
                            .queryParam("srchKeyCode", "001")
                            .queryParam("ctpvNm", "서울특별시") // 시도명으로 변경
                            .queryParam("lifeArray", "007") // 임신·출산
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("✅ 지자체 API 호출 성공!");
            
            if (response.contains("<resultCode>0</resultCode>")) {
                String totalCount = extractXmlValue(response, "totalCount");
                System.out.println("📊 서울시 임신·출산 관련 서비스: " + totalCount + "건");
                
                String firstServiceName = extractXmlValue(response, "servNm");
                System.out.println("📝 첫 번째 서비스: " + firstServiceName);
                
                String sidoName = extractXmlValue(response, "ctpvNm");
                System.out.println("🏙️ 지역: " + sidoName);
            } else {
                String resultCode = extractXmlValue(response, "resultCode");
                String resultMessage = extractXmlValue(response, "resultMessage");
                System.out.println("⚠️ 지자체 API 응답: 코드=" + resultCode + ", 메시지=" + resultMessage);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 지자체 API 호출 실패: " + e.getMessage());
        }
    }
    
    private String extractXmlValue(String xml, String tagName) {
        try {
            String startTag = "<" + tagName + ">";
            String endTag = "</" + tagName + ">";
            int startIndex = xml.indexOf(startTag);
            if (startIndex == -1) return null;
            
            startIndex += startTag.length();
            int endIndex = xml.indexOf(endTag, startIndex);
            if (endIndex == -1) return null;
            
            return xml.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return null;
        }
    }
}
