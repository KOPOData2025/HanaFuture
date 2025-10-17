package com.hanaTI.HanaFuture.domain.user.repository;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccount;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBankAccountRepository extends JpaRepository<UserBankAccount, Long> {

    /**
     * 사용자의 모든 연동 계좌 조회
     */
    List<UserBankAccount> findByUserOrderByIsPrimaryDescLinkedAtAsc(User user);

    /**
     * 사용자의 활성 계좌만 조회
     */
    List<UserBankAccount> findByUserAndStatusOrderByIsPrimaryDescLinkedAtAsc(User user, UserBankAccountStatus status);

    /**
     * 사용자의 주계좌 조회
     */
    Optional<UserBankAccount> findByUserAndIsPrimaryTrue(User user);

    /**
     * 사용자의 출금 가능 계좌 조회
     */
    @Query("SELECT uba FROM UserBankAccount uba WHERE uba.user = :user " +
           "AND uba.status = 'ACTIVE' AND uba.accountType IN ('1', '입출금') " +
           "ORDER BY uba.isPrimary DESC, uba.linkedAt ASC")
    List<UserBankAccount> findWithdrawableAccountsByUser(@Param("user") User user);

    /**
     * 계좌번호로 사용자 계좌 조회 (사용자 지정)
     */
    Optional<UserBankAccount> findByUserAndAccountNumber(User user, String accountNumber);

    /**
     * 계좌번호로 사용자 계좌 조회 (모든 사용자 대상)
     */
    Optional<UserBankAccount> findByAccountNumber(String accountNumber);

    /**
     * 계좌번호 중복 확인 (같은 사용자)
     */
    boolean existsByUserAndAccountNumber(User user, String accountNumber);

    /**
     * 사용자의 특정 은행 계좌 개수 조회
     */
    long countByUserAndBankCode(User user, String bankCode);

    /**
     * 자동 동기화 활성화된 계좌 조회
     */
    @Query("SELECT uba FROM UserBankAccount uba WHERE uba.autoSyncEnabled = true " +
           "AND uba.status = 'ACTIVE' " +
           "AND (uba.lastSyncedAt IS NULL OR uba.lastSyncedAt < :beforeTime)")
    List<UserBankAccount> findAccountsNeedingSync(@Param("beforeTime") java.time.LocalDateTime beforeTime);
}
