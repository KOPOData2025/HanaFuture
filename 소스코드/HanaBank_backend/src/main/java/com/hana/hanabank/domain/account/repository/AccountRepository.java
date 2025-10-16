package com.hana.hanabank.domain.account.repository;

import com.hana.hanabank.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(String userId);

    Optional<Account> findByUserIdAndAccountNum(String userId, String accountNum);

    Optional<Account> findByAccountNum(String accountNum);

    /**
     * 계좌 잔액 업데이트
     */
    @Modifying
    @Query("UPDATE Account a SET a.balanceAmt = :newBalance WHERE a.accountId = :accountId")
    int updateAccountBalance(@Param("accountId") Long accountId, @Param("newBalance") BigDecimal newBalance);
    
    /**
     * 활성 계좌만 조회
     */
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.activityType = '1'")
    List<Account> findActiveAccountsByUserId(@Param("userId") String userId);
    
    /**
     * 특정 상품의 계좌 조회
     */
    List<Account> findByUserIdAndProductName(String userId, String productName);
}
