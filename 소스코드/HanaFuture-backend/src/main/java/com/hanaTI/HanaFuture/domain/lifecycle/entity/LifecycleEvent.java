package com.hanaTI.HanaFuture.domain.lifecycle.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lifecycle_events")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class LifecycleEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LifecycleEventType eventType;
    
    @Column(nullable = false)
    private LocalDate eventDate;
    
    @Column(length = 100)
    private String childName; // 자녀 관련 이벤트인 경우
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isProcessed = false; // 추천 처리 완료 여부
    
    @Column
    private LocalDateTime processedAt; // 추천 처리 시간
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // 비즈니스 메서드
    public void markAsProcessed() {
        this.isProcessed = true;
        this.processedAt = LocalDateTime.now();
    }
    
    public boolean isUpcoming() {
        return eventDate.isAfter(LocalDate.now());
    }
    
    public boolean isToday() {
        return eventDate.equals(LocalDate.now());
    }
    
    public boolean isPast() {
        return eventDate.isBefore(LocalDate.now());
    }
    
    public long getDaysUntilEvent() {
        return LocalDate.now().until(eventDate).getDays();
    }
    
    public int getChildAgeAtEvent() {
        if (childName == null) return 0;
        // 사용자의 자녀 정보에서 나이 계산 로직 (추후 구현)
        return 0;
    }
}
