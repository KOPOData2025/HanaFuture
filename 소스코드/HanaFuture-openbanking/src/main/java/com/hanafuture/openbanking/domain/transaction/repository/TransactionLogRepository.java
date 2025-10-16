package com.hanafuture.openbanking.domain.transaction.repository;

import com.hanafuture.openbanking.domain.transaction.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
} 