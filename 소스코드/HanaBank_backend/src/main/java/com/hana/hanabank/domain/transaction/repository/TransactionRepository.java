package com.hana.hanabank.domain.transaction.repository;

import com.hana.hanabank.domain.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    // 계좌 ID로 거래내역 조회
    List<Transaction> findByAccountId(Long accountId);
    
    // 계좌 ID와 거래일자로 거래내역 조회
    List<Transaction> findByAccountIdAndTranDate(Long accountId, LocalDate tranDate);
    
    // 계좌 ID와 거래일자 범위로 거래내역 조회
    List<Transaction> findByAccountIdAndTranDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);
    
    // 계좌 ID와 입출금 구분으로 거래내역 조회
    List<Transaction> findByAccountIdAndInoutType(Long accountId, String inoutType);
    
    // 계좌번호로 거래내역 조회
    List<Transaction> findByWdAccountNum(String wdAccountNum);
    
    // 계좌번호와 거래일자로 거래내역 조회
    List<Transaction> findByWdAccountNumAndTranDate(String wdAccountNum, LocalDate tranDate);
    
    // 계좌번호와 거래일자 범위로 거래내역 조회
    List<Transaction> findByWdAccountNumAndTranDateBetween(String wdAccountNum, LocalDate startDate, LocalDate endDate);
    
    // 입출금 구분으로 거래내역 조회
    List<Transaction> findByWdAccountNumAndInoutType(String wdAccountNum, String inoutType);

    // 최신 거래내역 조회 (내림차순)
    List<Transaction> findByAccountIdOrderByTranDateDescTranTimeDesc(Long accountId);

    // 오래된 거래내역 조회 (오름차순)
    List<Transaction> findByAccountIdOrderByTranDateAscTranTimeAsc(Long accountId);
    
    // 페이징 처리된 거래내역 조회
    Page<Transaction> findByAccountIdOrderByTranDateDescTranTimeDesc(Long accountId, Pageable pageable);
    
    // 최근 N개 거래내역 조회
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId ORDER BY t.tranDate DESC, t.tranTime DESC LIMIT :limit")
    List<Transaction> findRecentTransactions(@Param("accountId") Long accountId, @Param("limit") int limit);
}
