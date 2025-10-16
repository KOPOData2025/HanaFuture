package com.hanafuture.openbanking.domain.bank.repository;

import com.hanafuture.openbanking.domain.bank.entity.BankCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCodeRepository extends JpaRepository<BankCode, String> {
} 