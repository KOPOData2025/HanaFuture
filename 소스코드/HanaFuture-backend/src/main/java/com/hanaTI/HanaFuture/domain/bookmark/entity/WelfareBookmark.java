package com.hanaTI.HanaFuture.domain.bookmark.entity;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자의 복지 혜택 즐겨찾기 엔티티
 */
@Entity
@Table(name = "welfare_bookmarks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WelfareBookmark {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 즐겨찾기한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 즐겨찾기한 복지 혜택
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "welfare_benefit_id")
    private WelfareBenefit welfareBenefit;
    
    /**
     * HanaFuture 복지 혜택 ID (nullable - HanaFuture 혜택 전용)
     * welfare_benefit_id와 둘 중 하나만 사용
     */
    @Column(name = "hana_future_benefit_id")
    private Long hanaFutureBenefitId;
    
    /**
     * 사용자 메모 (선택사항)
     */
    @Lob
    @Column(name = "memo")
    private String memo;
    
    /**
     * 즐겨찾기 추가 시간
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 즐겨찾기 생성 메서드
     */
    public static WelfareBookmark create(User user, WelfareBenefit welfareBenefit) {
        return WelfareBookmark.builder()
                .user(user)
                .welfareBenefit(welfareBenefit)
                .build();
    }
    
    /**
     * 즐겨찾기 생성 메서드 (메모 포함)
     */
    public static WelfareBookmark create(User user, WelfareBenefit welfareBenefit, String memo) {
        return WelfareBookmark.builder()
                .user(user)
                .welfareBenefit(welfareBenefit)
                .memo(memo)
                .build();
    }
    
    /**
     * HanaFuture 혜택 즐겨찾기 생성 메서드
     */
    public static WelfareBookmark createForHanaFuture(User user, Long hanaFutureBenefitId) {
        return WelfareBookmark.builder()
                .user(user)
                .hanaFutureBenefitId(hanaFutureBenefitId)
                .build();
    }
    
    /**
     * HanaFuture 혜택 즐겨찾기 생성 메서드 (메모 포함)
     */
    public static WelfareBookmark createForHanaFuture(User user, Long hanaFutureBenefitId, String memo) {
        return WelfareBookmark.builder()
                .user(user)
                .hanaFutureBenefitId(hanaFutureBenefitId)
                .memo(memo)
                .build();
    }
    
    /**
     * 메모 업데이트
     */
    public void updateMemo(String memo) {
        this.memo = memo;
    }
}


















