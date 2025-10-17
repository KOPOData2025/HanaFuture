package com.hanaTI.HanaFuture.domain.bookmark.service;

import com.hanaTI.HanaFuture.domain.bookmark.dto.BookmarkRequest;
import com.hanaTI.HanaFuture.domain.bookmark.dto.BookmarkResponse;
import com.hanaTI.HanaFuture.domain.bookmark.dto.UpdateMemoRequest;
import com.hanaTI.HanaFuture.domain.bookmark.entity.WelfareBookmark;
import com.hanaTI.HanaFuture.domain.bookmark.repository.WelfareBookmarkRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WelfareBookmarkService {
    
    private final WelfareBookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final WelfareBenefitRepository welfareBenefitRepository;
    private final HanaFutureWelfareBenefitRepository hanaFutureWelfareBenefitRepository;
    
    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public BookmarkResponse addBookmark(Long userId, BookmarkRequest request) {
        User user = getUserById(userId);
        WelfareBenefit welfareBenefit = getWelfareBenefitById(request.getWelfareBenefitId());
        
        // 이미 즐겨찾기가 있는지 확인
        if (bookmarkRepository.existsByUserAndWelfareBenefit(user, welfareBenefit)) {
            log.warn("중복 즐겨찾기 추가 시도 - 사용자: {}, 복지혜택: {}", user.getEmail(), welfareBenefit.getServiceName());
            // 이미 존재하는 즐겨찾기를 반환 (에러가 아닌 성공으로 처리)
            WelfareBookmark existingBookmark = bookmarkRepository.findByUserAndWelfareBenefit(user, welfareBenefit)
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
            return BookmarkResponse.from(existingBookmark);
        }
        
        WelfareBookmark bookmark = WelfareBookmark.create(user, welfareBenefit, request.getMemo());
        WelfareBookmark savedBookmark = bookmarkRepository.save(bookmark);
        
        log.info("즐겨찾기 추가 완료 - 사용자: {}, 복지혜택: {}", user.getEmail(), welfareBenefit.getServiceName());
        
        return BookmarkResponse.from(savedBookmark);
    }
    
    /**
     * 즐겨찾기 제거
     */
    @Transactional
    public void removeBookmark(Long userId, Long welfareBenefitId) {
        User user = getUserById(userId);
        WelfareBenefit welfareBenefit = getWelfareBenefitById(welfareBenefitId);
        
        WelfareBookmark bookmark = bookmarkRepository.findByUserAndWelfareBenefit(user, welfareBenefit)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
        
        bookmarkRepository.delete(bookmark);
        
        log.info("즐겨찾기 제거 완료 - 사용자: {}, 복지혜택: {}", user.getEmail(), welfareBenefit.getServiceName());
    }
    
    /**
     * 즐겨찾기 목록 조회 (페이징)
     * - WelfareBenefit과 HanaFuture 혜택 모두 포함
     */
    public Page<BookmarkResponse> getBookmarks(Long userId, int page, int size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<WelfareBookmark> bookmarks = bookmarkRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return bookmarks.map(bookmark -> {
            // HanaFuture 혜택인 경우
            if (bookmark.getHanaFutureBenefitId() != null) {
                HanaFutureWelfareBenefit benefit = getHanaFutureBenefitById(bookmark.getHanaFutureBenefitId());
                return BookmarkResponse.fromHanaFuture(bookmark, benefit);
            } 
            // 기존 복지 혜택인 경우
            else if (bookmark.getWelfareBenefit() != null) {
                return BookmarkResponse.from(bookmark);
            }
            // 둘 다 없는 경우 (비정상)
            else {
                log.warn("비정상적인 즐겨찾기 데이터: ID={}", bookmark.getId());
                return null;
            }
        });
    }
    
    /**
     * 즐겨찾기 목록 조회 (전체)
     * - WelfareBenefit과 HanaFuture 혜택 모두 포함
     */
    public List<BookmarkResponse> getAllBookmarks(Long userId) {
        User user = getUserById(userId);
        
        List<WelfareBookmark> bookmarks = bookmarkRepository.findByUserOrderByCreatedAtDesc(user);
        
        return bookmarks.stream()
                .map(bookmark -> {
                    // HanaFuture 혜택인 경우
                    if (bookmark.getHanaFutureBenefitId() != null) {
                        HanaFutureWelfareBenefit benefit = getHanaFutureBenefitById(bookmark.getHanaFutureBenefitId());
                        return BookmarkResponse.fromHanaFuture(bookmark, benefit);
                    } 
                    // 기존 복지 혜택인 경우
                    else if (bookmark.getWelfareBenefit() != null) {
                        return BookmarkResponse.from(bookmark);
                    }
                    // 둘 다 없는 경우 (비정상)
                    else {
                        log.warn("비정상적인 즐겨찾기 데이터: ID={}", bookmark.getId());
                        return null;
                    }
                })
                .filter(response -> response != null) // null 제거
                .collect(Collectors.toList());
    }
    
    /**
     * 카테고리별 즐겨찾기 조회
     */
    public List<BookmarkResponse> getBookmarksByCategory(Long userId, String category) {
        User user = getUserById(userId);
        
        List<WelfareBookmark> bookmarks = bookmarkRepository
                .findByUserAndWelfareBenefit_CategoryOrderByCreatedAtDesc(user, category);
        
        return bookmarks.stream()
                .map(BookmarkResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 생애주기별 즐겨찾기 조회
     */
    public List<BookmarkResponse> getBookmarksByLifeCycle(Long userId, String lifeCycle) {
        User user = getUserById(userId);
        
        List<WelfareBookmark> bookmarks = bookmarkRepository
                .findByUserAndWelfareBenefit_LifeCycleOrderByCreatedAtDesc(user, lifeCycle);
        
        return bookmarks.stream()
                .map(BookmarkResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 즐겨찾기 여부 확인
     */
    public boolean isBookmarked(Long userId, Long welfareBenefitId) {
        User user = getUserById(userId);
        WelfareBenefit welfareBenefit = getWelfareBenefitById(welfareBenefitId);
        
        return bookmarkRepository.existsByUserAndWelfareBenefit(user, welfareBenefit);
    }
    
    /**
     * 즐겨찾기 개수 조회
     */
    public long getBookmarkCount(Long userId) {
        User user = getUserById(userId);
        return bookmarkRepository.countByUser(user);
    }
    
    /**
     * 즐겨찾기 메모 업데이트
     */
    @Transactional
    public BookmarkResponse updateMemo(Long userId, Long bookmarkId, UpdateMemoRequest request) {
        User user = getUserById(userId);
        
        WelfareBookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
        
        // 본인의 즐겨찾기인지 확인
        if (!bookmark.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.BOOKMARK_ACCESS_DENIED);
        }
        
        bookmark.updateMemo(request.getMemo());
        
        log.info("즐겨찾기 메모 업데이트 완료 - 사용자: {}, 즐겨찾기ID: {}", user.getEmail(), bookmarkId);
        
        return BookmarkResponse.from(bookmark);
    }
    
    /**
     * 사용자 조회 헬퍼 메서드
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
    
    /**
     * 복지 혜택 조회 헬퍼 메서드
     */
    private WelfareBenefit getWelfareBenefitById(Long welfareBenefitId) {
        return welfareBenefitRepository.findById(welfareBenefitId)
                .orElseThrow(() -> new CustomException(ErrorCode.WELFARE_BENEFIT_NOT_FOUND));
    }
    
    // ==================== HanaFuture 전용 메서드 ====================
    
    /**
     * HanaFuture 혜택 즐겨찾기 추가
     */
    @Transactional
    public BookmarkResponse addHanaFutureBookmark(Long userId, Long hanaFutureBenefitId) {
        User user = getUserById(userId);
        
        // HanaFuture 혜택 존재 확인
        if (!hanaFutureWelfareBenefitRepository.existsById(hanaFutureBenefitId)) {
            throw new CustomException(ErrorCode.WELFARE_BENEFIT_NOT_FOUND);
        }
        
        // 이미 즐겨찾기가 있는지 확인
        if (bookmarkRepository.existsByUserAndHanaFutureBenefitId(user, hanaFutureBenefitId)) {
            log.warn("중복 HanaFuture 즐겨찾기 추가 시도 - 사용자: {}, 혜택ID: {}", user.getEmail(), hanaFutureBenefitId);
            WelfareBookmark existingBookmark = bookmarkRepository.findByUserAndHanaFutureBenefitId(user, hanaFutureBenefitId)
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
            return BookmarkResponse.fromHanaFuture(existingBookmark, getHanaFutureBenefitById(hanaFutureBenefitId));
        }
        
        WelfareBookmark bookmark = WelfareBookmark.createForHanaFuture(user, hanaFutureBenefitId);
        WelfareBookmark savedBookmark = bookmarkRepository.save(bookmark);
        
        log.info("HanaFuture 즐겨찾기 추가 완료 - 사용자: {}, 혜택ID: {}", user.getEmail(), hanaFutureBenefitId);
        
        HanaFutureWelfareBenefit benefit = getHanaFutureBenefitById(hanaFutureBenefitId);
        return BookmarkResponse.fromHanaFuture(savedBookmark, benefit);
    }
    
    /**
     * HanaFuture 혜택 즐겨찾기 제거
     */
    @Transactional
    public void removeHanaFutureBookmark(Long userId, Long hanaFutureBenefitId) {
        User user = getUserById(userId);
        
        WelfareBookmark bookmark = bookmarkRepository.findByUserAndHanaFutureBenefitId(user, hanaFutureBenefitId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
        
        bookmarkRepository.delete(bookmark);
        
        log.info("HanaFuture 즐겨찾기 제거 완료 - 사용자: {}, 혜택ID: {}", user.getEmail(), hanaFutureBenefitId);
    }
    
    /**
     * HanaFuture 혜택 즐겨찾기 여부 확인
     */
    public boolean isHanaFutureBookmarked(Long userId, Long hanaFutureBenefitId) {
        User user = getUserById(userId);
        return bookmarkRepository.existsByUserAndHanaFutureBenefitId(user, hanaFutureBenefitId);
    }
    
    /**
     * HanaFuture 복지 혜택 조회 헬퍼 메서드
     */
    private HanaFutureWelfareBenefit getHanaFutureBenefitById(Long hanaFutureBenefitId) {
        return hanaFutureWelfareBenefitRepository.findById(hanaFutureBenefitId)
                .orElseThrow(() -> new CustomException(ErrorCode.WELFARE_BENEFIT_NOT_FOUND));
    }
}

