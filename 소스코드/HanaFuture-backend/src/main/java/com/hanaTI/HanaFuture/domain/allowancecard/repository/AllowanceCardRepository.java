package com.hanaTI.HanaFuture.domain.allowancecard.repository;

import com.hanaTI.HanaFuture.domain.allowancecard.entity.AllowanceCard;
import com.hanaTI.HanaFuture.domain.allowancecard.entity.AllowanceCardStatus;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AllowanceCardRepository extends JpaRepository<AllowanceCard, Long> {
    
    /**
     * 부모 사용자의 모든 아이카드 조회 (생성일 내림차순)
     */
    List<AllowanceCard> findByParentUserOrderByCreatedAtDesc(User parentUser);
    
    /**
     * 부모 사용자의 활성 아이카드 조회
     */
    List<AllowanceCard> findByParentUserAndStatusOrderByCreatedAtDesc(User parentUser, AllowanceCardStatus status);
    
    /**
     * 자녀의 아이카드 조회
     */
    List<AllowanceCard> findByChildIdOrderByCreatedAtDesc(Long childId);
    
    /**
     * 카드 번호로 아이카드 조회
     */
    Optional<AllowanceCard> findByCardNumber(String cardNumber);
    
    /**
     * 특정 상태의 아이카드 조회
     */
    List<AllowanceCard> findByStatusOrderByCreatedAtDesc(AllowanceCardStatus status);
    
    /**
     * 자동 충전 활성화된 아이카드 조회
     */
    List<AllowanceCard> findByAutoChargeEnabledTrueAndStatus(AllowanceCardStatus status);
    
    /**
     * 잔액이 임계값 이하인 아이카드 조회
     */
    @Query("SELECT ac FROM AllowanceCard ac WHERE ac.currentBalance <= ac.lowBalanceAlert " +
           "AND ac.notificationEnabled = true AND ac.status = :status")
    List<AllowanceCard> findCardsWithLowBalance(@Param("status") AllowanceCardStatus status);
    
    /**
     * 특정 기간에 생성된 아이카드 조회
     */
    @Query("SELECT ac FROM AllowanceCard ac WHERE ac.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY ac.createdAt DESC")
    List<AllowanceCard> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    /**
     * 부모 사용자의 아이카드 수 조회
     */
    long countByParentUserAndStatus(User parentUser, AllowanceCardStatus status);
    
    /**
     * 자녀별 아이카드 수 조회
     */
    long countByChildIdAndStatus(Long childId, AllowanceCardStatus status);
    
    /**
     * 월 사용 한도 초과 위험 아이카드 조회
     */
    @Query("SELECT ac FROM AllowanceCard ac WHERE ac.monthlyLimit IS NOT NULL " +
           "AND ac.status = :status " +
           "AND (SELECT COALESCE(SUM(au.amount), 0) FROM AllowanceCardUsage au " +
           "WHERE au.allowanceCard = ac AND MONTH(au.createdAt) = MONTH(CURRENT_DATE) " +
           "AND YEAR(au.createdAt) = YEAR(CURRENT_DATE)) >= ac.monthlyLimit * 0.8")
    List<AllowanceCard> findCardsNearMonthlyLimit(@Param("status") AllowanceCardStatus status);
}










