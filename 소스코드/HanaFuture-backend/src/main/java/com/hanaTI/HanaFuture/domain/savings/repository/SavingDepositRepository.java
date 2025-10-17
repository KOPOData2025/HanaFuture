package com.hanaTI.HanaFuture.domain.savings.repository;

import com.hanaTI.HanaFuture.domain.savings.entity.SavingDeposit;
import com.hanaTI.HanaFuture.domain.savings.entity.SavingGoal;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SavingDepositRepository extends JpaRepository<SavingDeposit, Long> {
    
    /**
     * 특정 적금 목표의 모든 납입 내역 조회
     */
    List<SavingDeposit> findBySavingGoalOrderByDepositDateDesc(SavingGoal savingGoal);
    
    /**
     * 특정 사용자의 모든 납입 내역 조회
     */
    List<SavingDeposit> findByUserOrderByDepositDateDesc(User user);
    
    /**
     * 특정 적금 목표와 사용자의 납입 내역 조회
     */
    List<SavingDeposit> findBySavingGoalAndUserOrderByDepositDateDesc(SavingGoal savingGoal, User user);
    
    /**
     * 특정 기간의 납입 내역 조회
     */
    @Query("SELECT sd FROM SavingDeposit sd WHERE sd.savingGoal = :savingGoal " +
           "AND sd.depositDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sd.depositDate DESC")
    List<SavingDeposit> findBySavingGoalAndDepositDateBetween(
            @Param("savingGoal") SavingGoal savingGoal,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 적금 목표의 총 납입 횟수
     */
    long countBySavingGoal(SavingGoal savingGoal);
    
    /**
     * 특정 적금 목표의 총 납입 금액
     */
    @Query("SELECT COALESCE(SUM(sd.amount), 0) FROM SavingDeposit sd WHERE sd.savingGoal = :savingGoal")
    java.math.BigDecimal getTotalDepositAmountBySavingGoal(@Param("savingGoal") SavingGoal savingGoal);
}










