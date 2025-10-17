package com.hanaTI.HanaFuture.domain.bookmark.controller;

import com.hanaTI.HanaFuture.domain.bookmark.service.WelfareBookmarkService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "즐겨찾기 관련 API")
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final WelfareBookmarkService bookmarkService;

    @Operation(
        summary = "사용자 즐겨찾기 개수 조회", 
        description = "사용자의 즐겨찾기 개수를 조회합니다."
    )
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Object>> getUserBookmarkCount(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("사용자 {}의 즐겨찾기 개수 조회", userId);
        
        try {
            long bookmarkCount = bookmarkService.getBookmarkCount(userId);
            
            log.info("즐겨찾기 {}개 조회됨", bookmarkCount);
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", bookmarkCount));
            
        } catch (Exception e) {
            log.error("즐겨찾기 개수 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("즐겨찾기 개수 조회에 실패했습니다."));
        }
    }
}



