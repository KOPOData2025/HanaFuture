package com.hanaTI.HanaFuture;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class WelfareApiTest {
    
    private static final String SERVICE_KEY = "1182635ebadeb891c8ad3ec6807ce8ac12be533f5a30fa665bcaab57321866ae";
    private static final String BASE_URL = "http://apis.data.go.kr";
    
    @Test
    public void testCentralWelfareApi() {
        System.out.println("=== 복지로 중앙정부 API 테스트 시작 ===");
        
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                .build();
        
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001")
                            .queryParam("serviceKey", SERVICE_KEY)
                            .queryParam("callTp", "L")
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 3)
                            .queryParam("srchKeyCode", "001")
                            .queryParam("lifeArray", "007") // 임신·출산
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("✅ API 호출 성공!");
            System.out.println("📄 응답 길이: " + response.length() + " characters");
            
            // XML 응답 파싱 확인
            if (response.contains("<wantedList>")) {
                System.out.println("✅ XML 구조 확인됨");
                
                if (response.contains("<resultCode>0</resultCode>")) {
                    System.out.println("✅ API 응답 성공 (resultCode: 0)");
                } else if (response.contains("<resultCode>")) {
                    String resultCode = extractXmlValue(response, "resultCode");
                    String resultMessage = extractXmlValue(response, "resultMessage");
                    System.out.println("❌ API 응답 실패 - 코드: " + resultCode + ", 메시지: " + resultMessage);
                }
                
                String totalCount = extractXmlValue(response, "totalCount");
                if (totalCount != null) {
                    System.out.println("📊 총 데이터 수: " + totalCount + "건");
                }
                
                // 첫 번째 서비스 정보 추출
                if (response.contains("<servList>")) {
                    String servNm = extractXmlValue(response, "servNm");
                    String servId = extractXmlValue(response, "servId");
                    String jurMnofNm = extractXmlValue(response, "jurMnofNm");
                    
                    System.out.println("🏛️ 첫 번째 서비스:");
                    System.out.println("   - ID: " + servId);
                    System.out.println("   - 이름: " + servNm);
                    System.out.println("   - 소관부처: " + jurMnofNm);
                }
            } else {
                System.out.println("❌ 예상과 다른 XML 구조");
                System.out.println("응답 내용 (처음 500자): " + response.substring(0, Math.min(500, response.length())));
            }
            
        } catch (Exception e) {
            System.out.println("❌ API 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== 테스트 완료 ===");
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
