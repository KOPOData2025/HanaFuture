package com.hanaTI.HanaFuture.domain.bookmark.controller;

import com.hanaTI.HanaFuture.domain.bookmark.dto.BookmarkRequest;
import com.hanaTI.HanaFuture.domain.bookmark.dto.BookmarkResponse;
import com.hanaTI.HanaFuture.domain.bookmark.dto.UpdateMemoRequest;
import com.hanaTI.HanaFuture.domain.bookmark.service.WelfareBookmarkService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.hanaTI.HanaFuture.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Welfare Bookmark", description = "복지 혜택 즐겨찾기 API")
@Slf4j
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class WelfareBookmarkController {
    
    private final WelfareBookmarkService bookmarkService;
    private final JwtUtil jwtUtil;
    
    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    private Long extractUserIdFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            return jwtUtil.extractUserId(token);
        }
        throw new IllegalArgumentException("토큰에서 사용자 ID를 추출할 수 없습니다.");
    }
    
    /**
     * 요청에서 JWT 토큰 추출
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    @Operation(summary = "즐겨찾기 추가", description = "복지 혜택을 즐겨찾기에 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BookmarkResponse>> addBookmark(
            HttpServletRequest request,
            @Valid @RequestBody BookmarkRequest requestBody) {
        
        Long userId = extractUserIdFromToken(request);
        BookmarkResponse response = bookmarkService.addBookmark(userId, requestBody);
        
        return ResponseEntity.ok(ApiResponse.success("즐겨찾기가 추가되었습니다.", response));
    }
    
    @Operation(summary = "즐겨찾기 제거", description = "복지 혜택을 즐겨찾기에서 제거합니다.")
    @DeleteMapping("/welfare/{welfareBenefitId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            HttpServletRequest request,
            @Parameter(description = "복지 혜택 ID") @PathVariable Long welfareBenefitId) {
        
        Long userId = extractUserIdFromToken(request);
        bookmarkService.removeBookmark(userId, welfareBenefitId);
        
        return ResponseEntity.ok(ApiResponse.success("즐겨찾기가 제거되었습니다."));
    }
    
    @Operation(summary = "즐겨찾기 목록 조회 (페이징)", description = "사용자의 즐겨찾기 목록을 페이징으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookmarkResponse>>> getBookmarks(
            HttpServletRequest request,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Long userId = extractUserIdFromToken(request);
        Page<BookmarkResponse> response = bookmarkService.getBookmarks(userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "즐겨찾기 목록 조회 (전체)", description = "사용자의 모든 즐겨찾기 목록을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getAllBookmarks(
            HttpServletRequest request) {
        
        Long userId = extractUserIdFromToken(request);
        List<BookmarkResponse> response = bookmarkService.getAllBookmarks(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "카테고리별 즐겨찾기 조회", description = "특정 카테고리의 즐겨찾기만 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarksByCategory(
            HttpServletRequest request,
            @Parameter(description = "카테고리명") @PathVariable String category) {
        
        Long userId = extractUserIdFromToken(request);
        List<BookmarkResponse> response = bookmarkService.getBookmarksByCategory(userId, category);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "생애주기별 즐겨찾기 조회", description = "특정 생애주기의 즐겨찾기만 조회합니다.")
    @GetMapping("/lifecycle/{lifeCycle}")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarksByLifeCycle(
            HttpServletRequest request,
            @Parameter(description = "생애주기") @PathVariable String lifeCycle) {
        
        Long userId = extractUserIdFromToken(request);
        List<BookmarkResponse> response = bookmarkService.getBookmarksByLifeCycle(userId, lifeCycle);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "즐겨찾기 여부 확인", description = "특정 복지 혜택의 즐겨찾기 여부를 확인합니다.")
    @GetMapping("/check/{welfareBenefitId}")
    public ResponseEntity<ApiResponse<Boolean>> checkBookmark(
            HttpServletRequest request,
            @Parameter(description = "복지 혜택 ID") @PathVariable Long welfareBenefitId) {
        
        Long userId = extractUserIdFromToken(request);
        boolean isBookmarked = bookmarkService.isBookmarked(userId, welfareBenefitId);
        
        return ResponseEntity.ok(ApiResponse.success(isBookmarked));
    }
    
    @Operation(summary = "즐겨찾기 개수 조회", description = "사용자의 총 즐겨찾기 개수를 조회합니다.")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getBookmarkCount(
            HttpServletRequest request) {
        
        Long userId = extractUserIdFromToken(request);
        long count = bookmarkService.getBookmarkCount(userId);
        
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @Operation(summary = "즐겨찾기 메모 업데이트", description = "즐겨찾기의 메모를 업데이트합니다.")
    @PutMapping("/{bookmarkId}/memo")
    public ResponseEntity<ApiResponse<BookmarkResponse>> updateMemo(
            HttpServletRequest httpRequest,
            @Parameter(description = "즐겨찾기 ID") @PathVariable Long bookmarkId,
            @Valid @RequestBody UpdateMemoRequest request) {
        
        Long userId = extractUserIdFromToken(httpRequest);
        BookmarkResponse response = bookmarkService.updateMemo(userId, bookmarkId, request);
        
        return ResponseEntity.ok(ApiResponse.success("메모가 업데이트되었습니다.", response));
    }
    
    // ==================== HanaFuture 전용 엔드포인트 ====================
    
    @Operation(summary = "[HanaFuture] 즐겨찾기 추가", description = "HanaFuture 복지 혜택을 즐겨찾기에 추가합니다.")
    @PostMapping("/hana-future/{hanaFutureBenefitId}")
    public ResponseEntity<ApiResponse<BookmarkResponse>> addHanaFutureBookmark(
            HttpServletRequest request,
            @Parameter(description = "HanaFuture 혜택 ID") @PathVariable Long hanaFutureBenefitId) {
        
        Long userId = extractUserIdFromToken(request);
        BookmarkResponse response = bookmarkService.addHanaFutureBookmark(userId, hanaFutureBenefitId);
        
        return ResponseEntity.ok(ApiResponse.success("즐겨찾기가 추가되었습니다.", response));
    }
    
    @Operation(summary = "[HanaFuture] 즐겨찾기 제거", description = "HanaFuture 복지 혜택을 즐겨찾기에서 제거합니다.")
    @DeleteMapping("/hana-future/{hanaFutureBenefitId}")
    public ResponseEntity<ApiResponse<Void>> removeHanaFutureBookmark(
            HttpServletRequest request,
            @Parameter(description = "HanaFuture 혜택 ID") @PathVariable Long hanaFutureBenefitId) {
        
        Long userId = extractUserIdFromToken(request);
        bookmarkService.removeHanaFutureBookmark(userId, hanaFutureBenefitId);
        
        return ResponseEntity.ok(ApiResponse.success("즐겨찾기가 제거되었습니다."));
    }
    
    @Operation(summary = "[HanaFuture] 즐겨찾기 여부 확인", description = "HanaFuture 복지 혜택의 즐겨찾기 여부를 확인합니다.")
    @GetMapping("/hana-future/check/{hanaFutureBenefitId}")
    public ResponseEntity<ApiResponse<Boolean>> checkHanaFutureBookmark(
            HttpServletRequest request,
            @Parameter(description = "HanaFuture 혜택 ID") @PathVariable Long hanaFutureBenefitId) {
        
        Long userId = extractUserIdFromToken(request);
        boolean isBookmarked = bookmarkService.isHanaFutureBookmarked(userId, hanaFutureBenefitId);
        
        return ResponseEntity.ok(ApiResponse.success(isBookmarked));
    }
}
