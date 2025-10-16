package com.hana.hanabank.domain.savings.repository;

import com.hana.hanabank.domain.savings.entity.SavingsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsProductRepository extends JpaRepository<SavingsProduct, Long> {
    
    List<SavingsProduct> findByIsActiveTrueOrderByMaxInterestRateDesc();
    
    @Query("SELECT sp FROM SavingsProduct sp WHERE sp.isActive = true AND sp.targetCustomer LIKE %:targetCustomer% ORDER BY sp.maxInterestRate DESC")
    List<SavingsProduct> findByTargetCustomerContaining(@Param("targetCustomer") String targetCustomer);
    
    @Query("SELECT sp FROM SavingsProduct sp WHERE sp.isActive = true AND sp.minMonthlyAmount <= :monthlyAmount AND sp.maxMonthlyAmount >= :monthlyAmount ORDER BY sp.maxInterestRate DESC")
    List<SavingsProduct> findByMonthlyAmountRange(@Param("monthlyAmount") Long monthlyAmount);
}
