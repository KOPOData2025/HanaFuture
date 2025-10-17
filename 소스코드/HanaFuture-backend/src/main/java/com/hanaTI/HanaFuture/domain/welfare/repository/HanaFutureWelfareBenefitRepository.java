package com.hanaTI.HanaFuture.domain.welfare.repository;

import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 하나퓨처 맞춤 복지 혜택 Repository
 */
@Repository
public interface HanaFutureWelfareBenefitRepository extends JpaRepository<HanaFutureWelfareBenefit, Long> {

    /**
     * 활성 상태인 모든 혜택 조회 (AI 관련도 순)
     */
    Page<HanaFutureWelfareBenefit> findByIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(Pageable pageable);

    /**
     * 활성 상태인 모든 혜택 조회 (서비스 타입 균형 정렬)
     */
    Page<HanaFutureWelfareBenefit> findByIsActiveTrueOrderByServiceTypeAscAiRelevanceScoreDescCreatedAtDesc(Pageable pageable);

    /**
     * 서비스 타입별 활성 혜택 조회
     */
    Page<HanaFutureWelfareBenefit> findByServiceTypeAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String serviceType, Pageable pageable);

    /**
     * 카테고리별 활성 혜택 조회
     */
    Page<HanaFutureWelfareBenefit> findByCategoryContainingAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String category, Pageable pageable);

    /**
     * 생애주기별 활성 혜택 조회
     */
    Page<HanaFutureWelfareBenefit> findByLifeCycleContainingAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String lifeCycle, Pageable pageable);

    /**
     * 지역별 활성 혜택 조회 (지자체 복지)
     */
    Page<HanaFutureWelfareBenefit> findBySidoNameAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String sidoName, Pageable pageable);

    /**
     * 키워드 검색 (서비스명, 내용, 대상 등에서 검색)
     */
    @Query("SELECT h FROM HanaFutureWelfareBenefit h WHERE h.isActive = true AND " +
           "(h.serviceName LIKE %:keyword% OR " +
           "h.serviceContent LIKE %:keyword% OR " +
           "h.targetDescription LIKE %:keyword% OR " +
           "h.category LIKE %:keyword% OR " +
           "h.lifeCycle LIKE %:keyword%) " +
           "ORDER BY h.aiRelevanceScore DESC, h.createdAt DESC")
    Page<HanaFutureWelfareBenefit> findByKeywordAndIsActiveTrue(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 복합 검색 (서비스 타입 + 키워드)
     */
    @Query("SELECT h FROM HanaFutureWelfareBenefit h WHERE h.isActive = true AND " +
           "h.serviceType = :serviceType AND " +
           "(h.serviceName LIKE %:keyword% OR " +
           "h.serviceContent LIKE %:keyword% OR " +
           "h.targetDescription LIKE %:keyword% OR " +
           "h.category LIKE %:keyword% OR " +
           "h.lifeCycle LIKE %:keyword%) " +
           "ORDER BY h.aiRelevanceScore DESC, h.createdAt DESC")
    Page<HanaFutureWelfareBenefit> findByServiceTypeAndKeywordAndIsActiveTrue(
            @Param("serviceType") String serviceType, 
            @Param("keyword") String keyword, 
            Pageable pageable);

    /**
     * 활성 혜택 총 개수
     */
    long countByIsActiveTrue();

    /**
     * 서비스 타입별 활성 혜택 개수
     */
    long countByServiceTypeAndIsActiveTrue(String serviceType);

    /**
     * AI 관련도 점수가 높은 상위 혜택 조회
     */
    @Query("SELECT h FROM HanaFutureWelfareBenefit h WHERE h.isActive = true " +
           "ORDER BY h.aiRelevanceScore DESC, h.createdAt DESC")
    Page<HanaFutureWelfareBenefit> findTopByAiRelevanceScore(Pageable pageable);

    /**
     * 원본 복지 혜택 ID로 조회
     */
    List<HanaFutureWelfareBenefit> findByOriginalWelfareId(Long originalWelfareId);

    /**
     * 중복 확인 (원본 ID 기준)
     */
    boolean existsByOriginalWelfareId(Long originalWelfareId);

    /**
     * 지자체별 혜택 조회 (시도 + 시군구)
     */
    Page<HanaFutureWelfareBenefit> findBySidoNameAndSigunguNameAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String sidoName, String sigunguName, Pageable pageable);

    /**
     * 시군구별 혜택 조회 (시군구 이름으로 검색)
     */
    Page<HanaFutureWelfareBenefit> findBySigunguNameContainingAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String sigunguName, Pageable pageable);

    /**
     * 시도별 전체 혜택 조회 (시군구가 없는 경우 = 시도 전체 혜택)
     */
    Page<HanaFutureWelfareBenefit> findBySidoNameContainingAndSigunguNameIsNullAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            String sidoName, Pageable pageable);

    /**
     * 중앙정부 혜택만 조회 (sido_name, sigungu_name 둘 다 null)
     */
    Page<HanaFutureWelfareBenefit> findBySidoNameIsNullAndSigunguNameIsNullAndIsActiveTrueOrderByAiRelevanceScoreDescCreatedAtDesc(
            Pageable pageable);

    /**
     * 최근 추가된 혜택 조회
     */
    @Query("SELECT h FROM HanaFutureWelfareBenefit h WHERE h.isActive = true " +
           "ORDER BY h.aiFilteredAt DESC, h.createdAt DESC")
    Page<HanaFutureWelfareBenefit> findRecentlyAdded(Pageable pageable);
}

