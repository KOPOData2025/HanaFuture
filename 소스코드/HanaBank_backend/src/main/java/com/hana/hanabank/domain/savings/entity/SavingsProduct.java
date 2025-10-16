package com.hana.hanabank.domain.savings.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "savings_products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String productCode;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal baseInterestRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxInterestRate;

    @Column(nullable = false)
    private Integer minPeriodMonths;

    @Column(nullable = false)
    private Integer maxPeriodMonths;

    @Column(nullable = false)
    private Long minMonthlyAmount;

    @Column(nullable = false)
    private Long maxMonthlyAmount;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String targetCustomer;

    @ElementCollection
    @CollectionTable(name = "savings_product_benefits", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "benefit")
    private List<String> benefits;

    @ElementCollection
    @CollectionTable(name = "savings_product_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "feature")
    private List<String> features;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
