package com.hanaTI.HanaFuture.domain.welfare.repository;

import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WelfareBenefitRepository extends JpaRepository<WelfareBenefit, Long> {
    
    /**
     * 서비스 ID로 복지 혜택 조회
     */
    Optional<WelfareBenefit> findByServiceId(String serviceId);
    
    /**
     * 활성 상태인 복지 혜택만 조회
     */
    Page<WelfareBenefit> findByIsActiveTrue(Pageable pageable);
    
    /**
     * 서비스 유형별 조회
     */
    Page<WelfareBenefit> findByServiceTypeAndIsActiveTrue(WelfareType serviceType, Pageable pageable);
    
    /**
     * 지역별 복지 혜택 조회 (지자체만)
     */
    Page<WelfareBenefit> findByServiceTypeAndSidoNameAndIsActiveTrue(
            WelfareType serviceType, String sidoName, Pageable pageable);
    
    /**
     * 시군구별 복지 혜택 조회
     */
    Page<WelfareBenefit> findByServiceTypeAndSidoNameAndSigunguNameAndIsActiveTrue(
            WelfareType serviceType, String sidoName, String sigunguName, Pageable pageable);
    
    /**
     * 생애주기별 조회
     */
    @Query("SELECT wb FROM WelfareBenefit wb WHERE wb.isActive = true " +
           "AND (:lifeCycle IS NULL OR wb.lifeCycle LIKE %:lifeCycle%)")
    Page<WelfareBenefit> findByLifeCycleContaining(@Param("lifeCycle") String lifeCycle, Pageable pageable);
    
    /**
     * 키워드 검색 (서비스명, 내용, 대상 등에서 검색)
     */
    @Query("SELECT wb FROM WelfareBenefit wb WHERE wb.isActive = true " +
           "AND (:keyword IS NULL OR " +
           "wb.serviceName LIKE %:keyword% OR " +
           "wb.serviceContent LIKE %:keyword% OR " +
           "wb.targetDescription LIKE %:keyword%)")
    Page<WelfareBenefit> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 복합 조건 검색
     */
    @Query("SELECT wb FROM WelfareBenefit wb WHERE wb.isActive = true " +
           "AND (:serviceType IS NULL OR wb.serviceType = :serviceType) " +
           "AND (:sidoName IS NULL OR wb.sidoName = :sidoName) " +
           "AND (:sigunguName IS NULL OR wb.sigunguName = :sigunguName) " +
           "AND (:lifeCycle IS NULL OR wb.lifeCycle LIKE %:lifeCycle%) " +
           "AND (:category IS NULL OR wb.category LIKE %:category%) " +
           "AND (:keyword IS NULL OR " +
           "wb.serviceName LIKE %:keyword% OR " +
           "wb.serviceContent LIKE %:keyword% OR " +
           "wb.targetDescription LIKE %:keyword%)")
    Page<WelfareBenefit> findByComplexConditions(
            @Param("serviceType") WelfareType serviceType,
            @Param("sidoName") String sidoName,
            @Param("sigunguName") String sigunguName,
            @Param("lifeCycle") String lifeCycle,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable);
    
    /**
     * 지원금액이 있는 혜택들을 금액 순으로 조회
     */
    @Query("SELECT wb FROM WelfareBenefit wb WHERE wb.isActive = true " +
           "AND wb.supportAmount IS NOT NULL " +
           "ORDER BY wb.supportAmount DESC")
    Page<WelfareBenefit> findBySupportAmountOrderByAmountDesc(Pageable pageable);
    
    /**
     * 활성 상태인 복지 혜택 수 조회
     */
    long countByIsActiveTrue();
    
    /**
     * 서비스 유형별 활성 복지 혜택 수 조회
     */
    long countByServiceTypeAndIsActiveTrue(WelfareType serviceType);
    
    /**
     * 활성 상태인 복지 혜택들을 생성일 내림차순으로 조회
     */
    Page<WelfareBenefit> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 최근 업데이트된 혜택들 조회
     */
    @Query("SELECT wb FROM WelfareBenefit wb WHERE wb.isActive = true " +
           "ORDER BY wb.lastSyncedAt DESC")
    Page<WelfareBenefit> findRecentlyUpdated(Pageable pageable);
    
    /**
     * 서비스명 또는 대상 설명에서 키워드 검색
     */
    List<WelfareBenefit> findByServiceNameContainingOrTargetDescriptionContaining(String serviceName, String targetDescription);
    
    /**
     * 시군구명으로 검색 (활성 상태만)
     */
    Page<WelfareBenefit> findBySigunguNameContainingAndIsActiveTrue(String sigunguName, Pageable pageable);
    
    // 데이터 품질 분석용 메서드들
    Page<WelfareBenefit> findBySupportAmountIsNullOrApplicationMethodIsNull(Pageable pageable);
    Long countBySupportAmountIsNull();
    Long countByApplicationMethodIsNull();
    Long countByCategoryIsNull();
    Long countByAreaCodeIsNull();
    Long countBySupportCycleIsNull();
}
