package com.hanaTI.HanaFuture.domain.verification.repository;

import com.hanaTI.HanaFuture.domain.verification.entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    
    Optional<PhoneVerification> findByPhoneNumberAndVerificationCodeAndVerifiedFalse(
            String phoneNumber, String verificationCode);
    
    Optional<PhoneVerification> findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(
            String phoneNumber);
    
    @Modifying
    @Query("DELETE FROM PhoneVerification p WHERE p.expiryTime < :now")
    void deleteExpiredVerifications(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM PhoneVerification p WHERE p.phoneNumber = :phoneNumber AND p.verified = false")
    void deleteUnverifiedByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
