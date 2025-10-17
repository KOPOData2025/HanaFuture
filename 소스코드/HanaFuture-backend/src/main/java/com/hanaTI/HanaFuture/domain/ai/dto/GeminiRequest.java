package com.hanaTI.HanaFuture.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {
    
    private List<Content> contents;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }
    
    // 편의 메서드: 간단한 텍스트 요청 생성
    public static GeminiRequest createTextRequest(String text) {
        return GeminiRequest.builder()
                .contents(List.of(
                        Content.builder()
                                .parts(List.of(
                                        Part.builder()
                                                .text(text)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}
