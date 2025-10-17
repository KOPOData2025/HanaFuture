package com.hanaTI.HanaFuture.domain.asset.repository;

import com.hanaTI.HanaFuture.domain.asset.entity.AssetTransaction;
import com.hanaTI.HanaFuture.domain.asset.entity.AssetTransactionStatus;
import com.hanaTI.HanaFuture.domain.asset.entity.AssetTransactionType;
import com.hanaTI.HanaFuture.domain.asset.entity.ConnectedAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetTransactionRepository extends JpaRepository<AssetTransaction, Long> {

    // 특정 자산의 거래내역 조회 (페이징)
    Page<AssetTransaction> findByConnectedAssetOrderByTransactionDateDesc(
            ConnectedAsset connectedAsset, Pageable pageable);

    // 특정 자산의 거래내역 조회 (리스트)
    List<AssetTransaction> findByConnectedAssetOrderByTransactionDateDesc(ConnectedAsset connectedAsset);

    // 거래 ID로 조회
    Optional<AssetTransaction> findByTransactionId(String transactionId);

    // 특정 자산의 특정 기간 거래내역 조회
    @Query("SELECT at FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY at.transactionDate DESC")
    List<AssetTransaction> findByAssetAndDateRange(
            @Param("asset") ConnectedAsset asset,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 특정 자산의 거래 유형별 조회
    List<AssetTransaction> findByConnectedAssetAndTransactionTypeOrderByTransactionDateDesc(
            ConnectedAsset connectedAsset, AssetTransactionType transactionType);

    // 특정 자산의 금액 범위별 거래내역 조회
    @Query("SELECT at FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.amount BETWEEN :minAmount AND :maxAmount " +
           "ORDER BY at.transactionDate DESC")
    List<AssetTransaction> findByAssetAndAmountRange(
            @Param("asset") ConnectedAsset asset,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    // 특정 자산의 최근 거래내역 조회
    List<AssetTransaction> findTop10ByConnectedAssetOrderByTransactionDateDesc(ConnectedAsset connectedAsset);

    // 특정 자산의 총 입금액 조회
    @Query("SELECT COALESCE(SUM(at.amount), 0) FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.transactionType IN ('DEPOSIT', 'TRANSFER_IN', 'INTEREST', 'REFUND') " +
           "AND at.status = 'COMPLETED'")
    BigDecimal getTotalCredits(@Param("asset") ConnectedAsset asset);

    // 특정 자산의 총 출금액 조회
    @Query("SELECT COALESCE(SUM(at.amount), 0) FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.transactionType IN ('WITHDRAWAL', 'TRANSFER_OUT', 'PAYMENT', 'FEE') " +
           "AND at.status = 'COMPLETED'")
    BigDecimal getTotalDebits(@Param("asset") ConnectedAsset asset);

    // 특정 자산의 월별 거래 통계
    @Query("SELECT YEAR(at.transactionDate), MONTH(at.transactionDate), " +
           "COUNT(at), SUM(CASE WHEN at.transactionType IN ('DEPOSIT', 'TRANSFER_IN', 'INTEREST', 'REFUND') THEN at.amount ELSE 0 END), " +
           "SUM(CASE WHEN at.transactionType IN ('WITHDRAWAL', 'TRANSFER_OUT', 'PAYMENT', 'FEE') THEN at.amount ELSE 0 END) " +
           "FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.status = 'COMPLETED' " +
           "GROUP BY YEAR(at.transactionDate), MONTH(at.transactionDate) " +
           "ORDER BY YEAR(at.transactionDate) DESC, MONTH(at.transactionDate) DESC")
    List<Object[]> getMonthlyTransactionStats(@Param("asset") ConnectedAsset asset);

    // 카테고리별 거래 통계
    @Query("SELECT at.category, COUNT(at), SUM(at.amount) " +
           "FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.category IS NOT NULL " +
           "AND at.status = 'COMPLETED' " +
           "GROUP BY at.category " +
           "ORDER BY SUM(at.amount) DESC")
    List<Object[]> getCategoryStats(@Param("asset") ConnectedAsset asset);

    // 상대방별 거래 통계
    @Query("SELECT at.counterparty, COUNT(at), SUM(at.amount) " +
           "FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.counterparty IS NOT NULL " +
           "AND at.status = 'COMPLETED' " +
           "GROUP BY at.counterparty " +
           "ORDER BY COUNT(at) DESC")
    List<Object[]> getCounterpartyStats(@Param("asset") ConnectedAsset asset);

    // 거래 상태별 조회
    List<AssetTransaction> findByConnectedAssetAndStatusOrderByTransactionDateDesc(
            ConnectedAsset connectedAsset, AssetTransactionStatus status);

    // 특정 기간의 거래 건수 조회
    @Query("SELECT COUNT(at) FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND at.transactionDate BETWEEN :startDate AND :endDate " +
           "AND at.status = 'COMPLETED'")
    long countTransactionsByDateRange(
            @Param("asset") ConnectedAsset asset,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 중복 거래 확인
    boolean existsByConnectedAssetAndTransactionId(ConnectedAsset connectedAsset, String transactionId);

    // 검색 기능
    @Query("SELECT at FROM AssetTransaction at " +
           "WHERE at.connectedAsset = :asset " +
           "AND (LOWER(at.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(at.counterparty) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(at.memo) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY at.transactionDate DESC")
    List<AssetTransaction> searchTransactions(
            @Param("asset") ConnectedAsset asset, @Param("keyword") String keyword);

    // 사용자의 모든 자산 거래내역 조회
    @Query("SELECT at FROM AssetTransaction at " +
           "WHERE at.connectedAsset.user.id = :userId " +
           "ORDER BY at.transactionDate DESC")
    Page<AssetTransaction> findAllUserTransactions(@Param("userId") Long userId, Pageable pageable);

    // 사용자의 최근 거래내역 조회
    @Query("SELECT at FROM AssetTransaction at " +
           "WHERE at.connectedAsset.user.id = :userId " +
           "ORDER BY at.transactionDate DESC")
    List<AssetTransaction> findTop20ByUserIdOrderByTransactionDateDesc(@Param("userId") Long userId);
}
