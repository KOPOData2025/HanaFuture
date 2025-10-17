package com.hanaTI.HanaFuture.domain.banking.mock.repository;

import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HanaBankMockAccountRepository extends JpaRepository<HanaBankMockAccount, Long> {

    /**
     * 사용자 CI로 계좌 목록 조회
     */
    List<HanaBankMockAccount> findByUserCiAndIsActiveTrue(String userCi);

    /**
     * 사용자 이름으로 계좌 목록 조회 (CI가 없을 경우 대체용)
     */
    List<HanaBankMockAccount> findByUserNameAndIsActiveTrue(String userName);

    /**
     * 사용자 이름으로 모든 계좌 조회 (디버깅용)
     */
    List<HanaBankMockAccount> findByUserName(String userName);

    /**
     * 계좌번호로 계좌 조회 (활성 상태만)
     */
    Optional<HanaBankMockAccount> findByAccountNumAndIsActiveTrue(String accountNum);
    
    /**
     * 계좌번호로 계좌 조회 (활성 상태 무관)
     */
    Optional<HanaBankMockAccount> findByAccountNum(String accountNum);

    /**
     * 사용자의 총 자산 조회
     */
    @Query("SELECT COALESCE(SUM(a.balanceAmt), 0) FROM HanaBankMockAccount a WHERE a.userCi = :userCi AND a.isActive = true")
    java.math.BigDecimal getTotalBalanceByUserCi(@Param("userCi") String userCi);

    /**
     * 사용자의 계좌 개수 조회
     */
    long countByUserCiAndIsActiveTrue(String userCi);

    /**
     * 계좌 유형별 조회
     */
    List<HanaBankMockAccount> findByUserCiAndAccountTypeAndIsActiveTrue(String userCi, String accountType);
}
