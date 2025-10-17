package com.hanaTI.HanaFuture.domain.groupaccount.repository;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountTransaction;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupAccountTransactionRepository extends JpaRepository<GroupAccountTransaction, Long> {

    // 특정 모임통장의 거래내역 조회 (최신순) - ID로 조회
    Page<GroupAccountTransaction> findByGroupAccountIdOrderByTransactionDateDesc(Long groupAccountId, Pageable pageable);

    // 특정 모임통장의 거래내역 조회 (최신순) - User를 JOIN FETCH로 미리 로드
    @Query("SELECT t FROM GroupAccountTransaction t " +
           "LEFT JOIN FETCH t.user " +
           "WHERE t.groupAccount = :groupAccount " +
           "ORDER BY t.transactionDate DESC")
    Page<GroupAccountTransaction> findByGroupAccountOrderByTransactionDateDesc(
            @Param("groupAccount") GroupAccount groupAccount, Pageable pageable);

    // 특정 모임통장의 특정 사용자 거래내역 조회
    Page<GroupAccountTransaction> findByGroupAccountAndUserOrderByTransactionDateDesc(
            GroupAccount groupAccount, User user, Pageable pageable);

    // 특정 모임통장의 거래 유형별 조회
    Page<GroupAccountTransaction> findByGroupAccountAndTransactionTypeOrderByTransactionDateDesc(
            GroupAccount groupAccount, GroupAccountTransaction.TransactionType transactionType, Pageable pageable);

    // 특정 기간의 거래내역 조회
    @Query("SELECT t FROM GroupAccountTransaction t " +
           "WHERE t.groupAccount = :groupAccount " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    Page<GroupAccountTransaction> findByGroupAccountAndDateRange(
            @Param("groupAccount") GroupAccount groupAccount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 특정 모임통장의 총 입금액 조회
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM GroupAccountTransaction t " +
           "WHERE t.groupAccount = :groupAccount " +
           "AND t.transactionType IN ('DEPOSIT', 'TRANSFER_IN', 'AUTO_TRANSFER') " +
           "AND t.status = 'COMPLETED'")
    BigDecimal getTotalDeposits(@Param("groupAccount") GroupAccount groupAccount);

    // 특정 모임통장의 총 출금액 조회
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM GroupAccountTransaction t " +
           "WHERE t.groupAccount = :groupAccount " +
           "AND t.transactionType IN ('WITHDRAWAL', 'TRANSFER_OUT') " +
           "AND t.status = 'COMPLETED'")
    BigDecimal getTotalWithdrawals(@Param("groupAccount") GroupAccount groupAccount);

    // 특정 사용자의 기여금 조회
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM GroupAccountTransaction t " +
           "WHERE t.groupAccount = :groupAccount " +
           "AND t.user = :user " +
           "AND t.transactionType IN ('DEPOSIT', 'TRANSFER_IN', 'AUTO_TRANSFER') " +
           "AND t.status = 'COMPLETED'")
    BigDecimal getUserContributions(@Param("groupAccount") GroupAccount groupAccount, @Param("user") User user);

    // 최근 N개의 거래내역 조회
    List<GroupAccountTransaction> findTop10ByGroupAccountOrderByTransactionDateDesc(GroupAccount groupAccount);

    // 특정 모임통장의 현재 잔액 계산 (최신 거래의 balanceAfter)
    @Query("SELECT t.balanceAfter FROM GroupAccountTransaction t " +
           "WHERE t.groupAccount = :groupAccount " +
           "AND t.status = 'COMPLETED' " +
           "ORDER BY t.transactionDate DESC, t.id DESC " +
           "LIMIT 1")
    BigDecimal getCurrentBalance(@Param("groupAccount") GroupAccount groupAccount);

    // 월별 거래 통계
    @Query("SELECT EXTRACT(MONTH FROM t.transactionDate) as month, " +
           "SUM(CASE WHEN t.transactionType IN ('DEPOSIT', 'TRANSFER_IN', 'AUTO_TRANSFER') THEN t.amount ELSE 0 END) as totalDeposits, " +
           "SUM(CASE WHEN t.transactionType IN ('WITHDRAWAL', 'TRANSFER_OUT') THEN t.amount ELSE 0 END) as totalWithdrawals " +
           "FROM GroupAccountTransaction t " +
           "WHERE t.groupAccount = :groupAccount " +
           "AND t.status = 'COMPLETED' " +
           "AND EXTRACT(YEAR FROM t.transactionDate) = :year " +
           "GROUP BY EXTRACT(MONTH FROM t.transactionDate) " +
           "ORDER BY month")
    List<Object[]> getMonthlyTransactionStats(@Param("groupAccount") GroupAccount groupAccount, @Param("year") int year);
}