package com.hanaTI.HanaFuture.domain.account.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, unique = true)
    private String accountNumber;
    
    @Column(nullable = false)
    private String accountName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private String bankName;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 잔액 업데이트 메서드
    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }
    
    // 입금
    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    // 출금
    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);
    }
    
    // 계좌 정보 업데이트
    public void updateAccountInfo(String accountName, String description) {
        this.accountName = accountName;
        this.description = description;
    }
    
    // 계좌 비활성화
    public void deactivate() {
        this.isActive = false;
    }
    
    // 계좌 활성화
    public void activate() {
        this.isActive = true;
    }
}

