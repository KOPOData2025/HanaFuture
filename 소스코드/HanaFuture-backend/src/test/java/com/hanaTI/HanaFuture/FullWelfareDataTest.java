package com.hanaTI.HanaFuture;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class FullWelfareDataTest {
    
    private static final String SERVICE_KEY = "1182635ebadeb891c8ad3ec6807ce8ac12be533f5a30fa665bcaab57321866ae";
    private static final String BASE_URL = "http://apis.data.go.kr";
    
    @Test
    public void testFullWelfareData() {
        System.out.println("=== 전체 복지서비스 데이터 조회 테스트 ===");
        
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                .build();
        
        // 1. 중앙정부 전체 데이터 조회 (필터 없음)
        testCentralFullData(webClient);
        
        // 2. 생애주기별 데이터 조회
        testByLifeCycle(webClient);
        
        // 3. 관심주제별 데이터 조회  
        testByInterestTheme(webClient);
        
        System.out.println("\n=== 전체 데이터 조회 테스트 완료 ===");
    }
    
    private void testCentralFullData(WebClient webClient) {
        System.out.println("\n📊 === 중앙정부 전체 복지서비스 조회 ===");
        
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001")
                            .queryParam("serviceKey", SERVICE_KEY)
                            .queryParam("callTp", "L")
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 1)
                            .queryParam("srchKeyCode", "001")
                            // 필터 없음 - 전체 데이터 조회
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response.contains("<resultCode>0</resultCode>")) {
                String totalCount = extractXmlValue(response, "totalCount");
                System.out.println("🎯 중앙정부 전체 복지서비스: " + totalCount + "건");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 전체 데이터 조회 실패: " + e.getMessage());
        }
    }
    
    private void testByLifeCycle(WebClient webClient) {
        System.out.println("\n👶 === 생애주기별 데이터 조회 ===");
        
        String[] lifeCycles = {"001", "002", "003", "004", "005", "006", "007"};
        String[] lifeNames = {"영유아", "아동", "청소년", "청년", "중장년", "노년", "임신·출산"};
        
        for (int i = 0; i < lifeCycles.length; i++) {
            final String lifeCycle = lifeCycles[i];
            final String lifeName = lifeNames[i];
            
            try {
                String response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001")
                                .queryParam("serviceKey", SERVICE_KEY)
                                .queryParam("callTp", "L")
                                .queryParam("pageNo", 1)
                                .queryParam("numOfRows", 1)
                                .queryParam("srchKeyCode", "001")
                                .queryParam("lifeArray", lifeCycle)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                if (response.contains("<resultCode>0</resultCode>")) {
                    String totalCount = extractXmlValue(response, "totalCount");
                    System.out.println("   " + lifeName + ": " + totalCount + "건");
                }
                
            } catch (Exception e) {
                System.out.println("   " + lifeName + ": 조회 실패");
            }
        }
    }
    
    private void testByInterestTheme(WebClient webClient) {
        System.out.println("\n🎭 === 관심주제별 데이터 조회 (주요 분야) ===");
        
        String[] themes = {"030", "040", "050", "080", "090", "100"};
        String[] themeNames = {"생활지원", "주거", "일자리", "임신출산", "보육", "교육"};
        
        for (int i = 0; i < themes.length; i++) {
            final String theme = themes[i];
            final String themeName = themeNames[i];
            
            try {
                String response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001")
                                .queryParam("serviceKey", SERVICE_KEY)
                                .queryParam("callTp", "L")
                                .queryParam("pageNo", 1)
                                .queryParam("numOfRows", 1)
                                .queryParam("srchKeyCode", "001")
                                .queryParam("intrsThemaArray", theme)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                if (response.contains("<resultCode>0</resultCode>")) {
                    String totalCount = extractXmlValue(response, "totalCount");
                    System.out.println("   " + themeName + ": " + totalCount + "건");
                }
                
            } catch (Exception e) {
                System.out.println("   " + themeName + ": 조회 실패");
            }
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
