package com.hanaTI.HanaFuture.domain.verification.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone_verifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 10)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 50)
    private String purpose; // MYDATA_AUTH, SIGNUP, PASSWORD_RESET

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public void markAsVerified() {
        this.verified = true;
    }

    public boolean isVerified() {
        return this.verified;
    }
}
