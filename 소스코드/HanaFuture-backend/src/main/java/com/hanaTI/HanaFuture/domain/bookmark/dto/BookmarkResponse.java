package com.hanaTI.HanaFuture.domain.bookmark.dto;

import com.hanaTI.HanaFuture.domain.bookmark.entity.WelfareBookmark;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {
    
    /**
     * 즐겨찾기 ID
     */
    private Long id;
    
    /**
     * 즐겨찾기한 복지 혜택 정보
     */
    private WelfareBenefitResponse welfareBenefit;
    
    /**
     * 사용자 메모
     */
    private String memo;
    
    /**
     * 즐겨찾기 추가 시간
     */
    private LocalDateTime createdAt;
    
    /**
     * 즐겨찾기 여부 (UI에서 사용)
     */
    @Builder.Default
    private boolean isBookmarked = true;
    
    public static BookmarkResponse from(WelfareBookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .welfareBenefit(WelfareBenefitResponse.from(bookmark.getWelfareBenefit()))
                .memo(bookmark.getMemo())
                .createdAt(bookmark.getCreatedAt())
                .isBookmarked(true)
                .build();
    }
    
    /**
     * HanaFuture 혜택 즐겨찾기 변환 메서드
     */
    public static BookmarkResponse fromHanaFuture(WelfareBookmark bookmark, HanaFutureWelfareBenefit benefit) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .welfareBenefit(WelfareBenefitResponse.fromHanaFuture(benefit))
                .memo(bookmark.getMemo())
                .createdAt(bookmark.getCreatedAt())
                .isBookmarked(true)
                .build();
    }
}




















