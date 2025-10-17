package com.hanaTI.HanaFuture.domain.asset.repository;

import com.hanaTI.HanaFuture.domain.asset.entity.AssetAccountType;
import com.hanaTI.HanaFuture.domain.asset.entity.AssetConnectionStatus;
import com.hanaTI.HanaFuture.domain.asset.entity.ConnectedAsset;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectedAssetRepository extends JpaRepository<ConnectedAsset, Long> {

    // 사용자별 연결된 자산 조회
    List<ConnectedAsset> findByUserAndConnectionStatusOrderByCreatedAtDesc(
            User user, AssetConnectionStatus status);

    // 사용자의 모든 자산 조회 (상태 무관)
    List<ConnectedAsset> findByUserOrderByCreatedAtDesc(User user);

    // 사용자의 특정 계좌 조회
    Optional<ConnectedAsset> findByUserAndAccountNumberAndBankCode(
            User user, String accountNumber, String bankCode);

    // 사용자의 계좌 유형별 조회
    List<ConnectedAsset> findByUserAndAccountTypeAndConnectionStatus(
            User user, AssetAccountType accountType, AssetConnectionStatus status);

    // 사용자의 주 계좌 조회
    Optional<ConnectedAsset> findByUserAndIsPrimaryTrue(User user);

    // 동기화가 필요한 자산 조회
    @Query("SELECT ca FROM ConnectedAsset ca " +
           "WHERE ca.syncEnabled = true " +
           "AND ca.connectionStatus = 'CONNECTED' " +
           "AND (ca.lastSyncedAt IS NULL OR ca.lastSyncedAt < :syncThreshold)")
    List<ConnectedAsset> findAssetsNeedingSync(@Param("syncThreshold") LocalDateTime syncThreshold);

    // 사용자의 동기화 필요 자산 조회
    @Query("SELECT ca FROM ConnectedAsset ca " +
           "WHERE ca.user = :user " +
           "AND ca.syncEnabled = true " +
           "AND ca.connectionStatus = 'CONNECTED' " +
           "AND (ca.lastSyncedAt IS NULL OR ca.lastSyncedAt < :syncThreshold)")
    List<ConnectedAsset> findUserAssetsNeedingSync(
            @Param("user") User user, @Param("syncThreshold") LocalDateTime syncThreshold);

    // 만료된 동의 조회
    @Query("SELECT ca FROM ConnectedAsset ca " +
           "WHERE ca.consentExpiresAt IS NOT NULL " +
           "AND ca.consentExpiresAt < :now " +
           "AND ca.connectionStatus = 'CONNECTED'")
    List<ConnectedAsset> findExpiredConsents(@Param("now") LocalDateTime now);

    // 사용자별 총 자산 조회
    @Query("SELECT COALESCE(SUM(ca.balance), 0) FROM ConnectedAsset ca " +
           "WHERE ca.user = :user " +
           "AND ca.connectionStatus = 'CONNECTED' " +
           "AND ca.accountType IN ('CHECKING', 'SAVINGS', 'DEPOSIT', 'CMA')")
    BigDecimal getTotalAssets(@Param("user") User user);

    // 사용자별 총 투자 자산 조회
    @Query("SELECT COALESCE(SUM(ca.currentValue), 0) FROM ConnectedAsset ca " +
           "WHERE ca.user = :user " +
           "AND ca.connectionStatus = 'CONNECTED' " +
           "AND ca.accountType IN ('INVESTMENT', 'STOCK', 'FUND')")
    BigDecimal getTotalInvestments(@Param("user") User user);

    // 사용자별 총 대출 조회
    @Query("SELECT COALESCE(SUM(ca.remainingAmount), 0) FROM ConnectedAsset ca " +
           "WHERE ca.user = :user " +
           "AND ca.connectionStatus = 'CONNECTED' " +
           "AND ca.accountType IN ('LOAN', 'MORTGAGE')")
    BigDecimal getTotalLoans(@Param("user") User user);

    // 사용자별 총 카드 사용액 조회
    @Query("SELECT COALESCE(SUM(ca.usedAmount), 0) FROM ConnectedAsset ca " +
           "WHERE ca.user = :user " +
           "AND ca.connectionStatus = 'CONNECTED' " +
           "AND ca.accountType IN ('CREDIT_CARD')")
    BigDecimal getTotalCardUsage(@Param("user") User user);

    // 은행별 자산 조회
    List<ConnectedAsset> findByUserAndBankCodeAndConnectionStatus(
            User user, String bankCode, AssetConnectionStatus status);

    // 계좌 존재 여부 확인
    boolean existsByUserAndAccountNumberAndBankCode(User user, String accountNumber, String bankCode);

    // 연결 상태별 개수 조회
    @Query("SELECT COUNT(ca) FROM ConnectedAsset ca " +
           "WHERE ca.user = :user AND ca.connectionStatus = :status")
    long countByUserAndConnectionStatus(@Param("user") User user, @Param("status") AssetConnectionStatus status);

    // 최근 연결된 자산 조회
    List<ConnectedAsset> findTop5ByUserAndConnectionStatusOrderByCreatedAtDesc(
            User user, AssetConnectionStatus status);

    // 알림 설정된 자산 조회
    List<ConnectedAsset> findByUserAndNotificationEnabledTrueAndConnectionStatus(
            User user, AssetConnectionStatus status);

    // 자동 동기화 설정된 자산 조회
    List<ConnectedAsset> findByUserAndSyncEnabledTrueAndConnectionStatus(
            User user, AssetConnectionStatus status);

    // 통계용 쿼리들
    @Query("SELECT ca.accountType, COUNT(ca), COALESCE(SUM(ca.balance), 0) " +
           "FROM ConnectedAsset ca " +
           "WHERE ca.user = :user AND ca.connectionStatus = 'CONNECTED' " +
           "GROUP BY ca.accountType")
    List<Object[]> getAssetStatsByType(@Param("user") User user);

    @Query("SELECT ca.bankName, COUNT(ca), COALESCE(SUM(ca.balance), 0) " +
           "FROM ConnectedAsset ca " +
           "WHERE ca.user = :user AND ca.connectionStatus = 'CONNECTED' " +
           "GROUP BY ca.bankName")
    List<Object[]> getAssetStatsByBank(@Param("user") User user);
}
