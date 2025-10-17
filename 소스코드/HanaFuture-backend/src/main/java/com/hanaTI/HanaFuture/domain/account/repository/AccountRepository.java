package com.hanaTI.HanaFuture.domain.account.repository;

import com.hanaTI.HanaFuture.domain.account.entity.Account;
import com.hanaTI.HanaFuture.domain.account.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    // 사용자의 모든 계좌 조회 (활성 계좌만)
    List<Account> findByUserIdAndIsActiveTrue(Long userId);
    
    // 사용자의 모든 계좌 조회 (비활성 포함)
    List<Account> findByUserId(Long userId);
    
    // 계좌번호로 계좌 조회
    Optional<Account> findByAccountNumber(String accountNumber);
    
    // 사용자의 특정 타입 계좌들 조회
    List<Account> findByUserIdAndAccountTypeAndIsActiveTrue(Long userId, AccountType accountType);
    
    // 계좌번호 중복 확인
    boolean existsByAccountNumber(String accountNumber);
    
    // 사용자의 총 자산 계산
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    java.math.BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);
    
    // 사용자의 계좌 개수 조회
    long countByUserIdAndIsActiveTrue(Long userId);
}

