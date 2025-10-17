package com.hanaTI.HanaFuture;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hanaTI.HanaFuture.domain.welfare.dto.external.WelfareApiResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.domain.welfare.service.WelfareXmlParsingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class WelfareDataMappingTest {
    
    private static final String SERVICE_KEY = "1182635ebadeb891c8ad3ec6807ce8ac12be533f5a30fa665bcaab57321866ae";
    private static final String BASE_URL = "http://apis.data.go.kr";
    
    @Test
    public void testCompleteDataFlow() {
        System.out.println("=== 복지로 데이터 → 프론트엔드 형태 변환 테스트 ===");
        
        try {
            // 1. 실제 API 호출
            WebClient webClient = WebClient.builder()
                    .baseUrl(BASE_URL)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                    .build();
            
            String xmlResponse = webClient.get()
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
            
            System.out.println("✅ 1단계: API 호출 성공");
            
            // 2. XML 파싱
            WelfareXmlParsingService parsingService = new WelfareXmlParsingService();
            WelfareApiResponse apiResponse = parsingService.parseXmlResponse(xmlResponse);
            
            System.out.println("✅ 2단계: XML 파싱 성공");
            System.out.println("   - 결과 코드: " + apiResponse.getResultCode());
            System.out.println("   - 결과 메시지: " + apiResponse.getResultMessage());
            System.out.println("   - 총 데이터 수: " + apiResponse.getTotalCount());
            
            // 3. 엔티티 변환
            List<WelfareBenefit> benefits = parsingService.convertToEntities(apiResponse, WelfareType.CENTRAL);
            
            System.out.println("✅ 3단계: 엔티티 변환 성공 (" + benefits.size() + "개)");
            
            // 4. 프론트엔드용 응답 형태 확인
            for (int i = 0; i < Math.min(2, benefits.size()); i++) {
                WelfareBenefit benefit = benefits.get(i);
                System.out.println("\n📋 복지서비스 " + (i+1) + ":");
                System.out.println("   🆔 서비스ID: " + benefit.getServiceId());
                System.out.println("   📝 서비스명: " + benefit.getServiceName());
                System.out.println("   🏛️ 소관부처: " + benefit.getJurisdictionName());
                System.out.println("   🎯 서비스유형: " + benefit.getServiceType().getDisplayName());
                System.out.println("   👶 생애주기: " + benefit.getLifeCycle());
                System.out.println("   📄 서비스내용: " + (benefit.getServiceContent() != null ? 
                        benefit.getServiceContent().substring(0, Math.min(100, benefit.getServiceContent().length())) + "..." : "없음"));
                System.out.println("   🔗 상세링크: " + benefit.getInquiryUrl());
                System.out.println("   💰 지원금액: " + (benefit.getSupportAmount() != null ? 
                        String.format("%,d원", benefit.getSupportAmount()) : "파싱되지않음"));
            }
            
            // 5. 프론트엔드 JSON 응답 시뮬레이션
            System.out.println("\n🌐 프론트엔드 API 응답 형태:");
            System.out.println("{");
            System.out.println("  \"success\": true,");
            System.out.println("  \"message\": \"복지 혜택을 조회했습니다.\",");
            System.out.println("  \"data\": {");
            System.out.println("    \"content\": [");
            System.out.println("      {");
            System.out.println("        \"id\": null,");
            System.out.println("        \"serviceId\": \"" + benefits.get(0).getServiceId() + "\",");
            System.out.println("        \"serviceName\": \"" + benefits.get(0).getServiceName() + "\",");
            System.out.println("        \"serviceType\": \"" + benefits.get(0).getServiceType() + "\",");
            System.out.println("        \"jurisdictionName\": \"" + benefits.get(0).getJurisdictionName() + "\",");
            System.out.println("        \"lifeCycle\": \"" + benefits.get(0).getLifeCycle() + "\"");
            System.out.println("      }");
            System.out.println("    ],");
            System.out.println("    \"totalElements\": " + apiResponse.getTotalCount());
            System.out.println("  }");
            System.out.println("}");
            
        } catch (Exception e) {
            System.out.println("❌ 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== 데이터 플로우 테스트 완료 ===");
    }
}
