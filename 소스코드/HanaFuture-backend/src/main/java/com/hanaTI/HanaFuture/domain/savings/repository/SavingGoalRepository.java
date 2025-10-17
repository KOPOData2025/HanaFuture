package com.hanaTI.HanaFuture.domain.savings.repository;

import com.hanaTI.HanaFuture.domain.savings.entity.SavingGoal;
import com.hanaTI.HanaFuture.domain.savings.entity.SavingStatus;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {

    // 적금 계좌번호로 조회 (중복 확인용)
    Optional<SavingGoal> findBySavingsAccountNumber(String savingsAccountNumber);

    // 생성자별 적금 목표 조회
    List<SavingGoal> findByCreatorOrderByCreatedAtDesc(User creator);

    // 생성자와 상태별 조회
    List<SavingGoal> findByCreatorAndStatusOrderByCreatedAtDesc(User creator, SavingStatus status);

    // 특정 출금 계좌와 연결된 적금 목표들 조회
    List<SavingGoal> findBySourceAccountId(String sourceAccountId);

    // 모임통장과 연결된 적금 목표들 조회
    @Query("SELECT sg FROM SavingGoal sg WHERE sg.sourceType = 'GROUP_ACCOUNT' AND sg.sourceAccountId = :groupAccountId")
    List<SavingGoal> findByGroupAccountId(@Param("groupAccountId") String groupAccountId);

    // 목표 달성률 기준 조회
    @Query("SELECT sg FROM SavingGoal sg " +
           "WHERE sg.creator = :creator " +
           "AND (sg.currentAmount / sg.targetAmount * 100) >= :minPercentage " +
           "AND sg.status = 'ACTIVE' " +
           "ORDER BY (sg.currentAmount / sg.targetAmount) DESC")
    List<SavingGoal> findByCreatorAndAchievementRateGreaterThan(
            @Param("creator") User creator, 
            @Param("minPercentage") BigDecimal minPercentage);

    // 만료 임박 적금 조회
    @Query("SELECT sg FROM SavingGoal sg " +
           "WHERE sg.endDate BETWEEN :startDate AND :endDate " +
           "AND sg.status = 'ACTIVE'")
    List<SavingGoal> findExpiringGoals(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    // 월별 납입 현황 조회
    @Query("SELECT sg FROM SavingGoal sg " +
           "WHERE sg.creator = :creator " +
           "AND sg.status = 'ACTIVE' " +
           "ORDER BY sg.createdAt DESC")
    List<SavingGoal> findActiveGoalsByCreator(@Param("creator") User creator);

    // 카테고리별 적금 목표 조회
    List<SavingGoal> findByCreatorAndCategory(User creator, String category);
}
