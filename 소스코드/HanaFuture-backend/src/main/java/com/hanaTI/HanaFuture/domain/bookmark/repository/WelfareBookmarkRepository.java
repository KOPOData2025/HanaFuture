package com.hanaTI.HanaFuture.domain.bookmark.repository;

import com.hanaTI.HanaFuture.domain.bookmark.entity.WelfareBookmark;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WelfareBookmarkRepository extends JpaRepository<WelfareBookmark, Long> {
    
    /**
     * 사용자의 모든 즐겨찾기 조회 (페이징)
     * - LEFT JOIN으로 변경하여 HanaFuture 데이터도 포함
     */
    @Query("SELECT wb FROM WelfareBookmark wb " +
           "LEFT JOIN FETCH wb.welfareBenefit " +
           "WHERE wb.user = :user " +
           "ORDER BY wb.createdAt DESC")
    Page<WelfareBookmark> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);
    
    /**
     * 사용자의 모든 즐겨찾기 조회 (리스트)
     * - LEFT JOIN으로 변경하여 HanaFuture 데이터도 포함
     */
    @Query("SELECT wb FROM WelfareBookmark wb " +
           "LEFT JOIN FETCH wb.welfareBenefit " +
           "WHERE wb.user = :user " +
           "ORDER BY wb.createdAt DESC")
    List<WelfareBookmark> findByUserOrderByCreatedAtDesc(@Param("user") User user);
    
    /**
     * 특정 사용자와 복지 혜택의 즐겨찾기 존재 여부 확인
     */
    boolean existsByUserAndWelfareBenefit(User user, WelfareBenefit welfareBenefit);
    
    /**
     * 특정 사용자와 복지 혜택의 즐겨찾기 조회
     */
    Optional<WelfareBookmark> findByUserAndWelfareBenefit(User user, WelfareBenefit welfareBenefit);
    
    /**
     * 사용자의 즐겨찾기 개수 조회
     */
    long countByUser(User user);
    
    /**
     * 카테고리별 즐겨찾기 조회
     */
    @Query("SELECT wb FROM WelfareBookmark wb " +
           "JOIN FETCH wb.welfareBenefit wf " +
           "WHERE wb.user = :user AND wf.category = :category " +
           "ORDER BY wb.createdAt DESC")
    List<WelfareBookmark> findByUserAndWelfareBenefit_CategoryOrderByCreatedAtDesc(
            @Param("user") User user, @Param("category") String category);
    
    /**
     * 생애주기별 즐겨찾기 조회
     */
    @Query("SELECT wb FROM WelfareBookmark wb " +
           "JOIN FETCH wb.welfareBenefit wf " +
           "WHERE wb.user = :user AND wf.lifeCycle = :lifeCycle " +
           "ORDER BY wb.createdAt DESC")
    List<WelfareBookmark> findByUserAndWelfareBenefit_LifeCycleOrderByCreatedAtDesc(
            @Param("user") User user, @Param("lifeCycle") String lifeCycle);
    
    /**
     * HanaFuture 혜택 즐겨찾기 존재 여부 확인
     */
    boolean existsByUserAndHanaFutureBenefitId(User user, Long hanaFutureBenefitId);
    
    /**
     * HanaFuture 혜택 즐겨찾기 조회
     */
    Optional<WelfareBookmark> findByUserAndHanaFutureBenefitId(User user, Long hanaFutureBenefitId);
}




















