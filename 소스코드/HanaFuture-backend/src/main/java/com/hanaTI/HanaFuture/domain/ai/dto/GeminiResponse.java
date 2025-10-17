package com.hanaTI.HanaFuture.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    
    private List<Candidate> candidates;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
        private List<SafetyRating> safetyRatings;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;
        private String role;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    public String getFirstResponseText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate firstCandidate = candidates.get(0);
            if (firstCandidate.getContent() != null && 
                firstCandidate.getContent().getParts() != null && 
                !firstCandidate.getContent().getParts().isEmpty()) {
                return firstCandidate.getContent().getParts().get(0).getText();
            }
        }
        return null;
    }
}



