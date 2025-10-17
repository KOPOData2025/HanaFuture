package com.hanaTI.HanaFuture.domain.bookmark.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    
    /**
     * 즐겨찾기할 복지 혜택 ID
     */
    @NotNull(message = "복지 혜택 ID는 필수입니다.")
    private Long welfareBenefitId;
    
    /**
     * 사용자 메모 (선택사항)
     */
    private String memo;
}






















