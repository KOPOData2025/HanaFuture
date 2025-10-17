package com.hanaTI.HanaFuture.domain.lifecycle.repository;

import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEvent;
import com.hanaTI.HanaFuture.domain.lifecycle.entity.LifecycleEventType;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LifecycleEventRepository extends JpaRepository<LifecycleEvent, Long> {
    
    // 사용자별 모든 생애주기 이벤트 조회
    List<LifecycleEvent> findByUserOrderByEventDateDesc(User user);
    
    // 사용자별 미처리 이벤트 조회
    List<LifecycleEvent> findByUserAndIsProcessedFalseOrderByEventDateAsc(User user);
    
    // 사용자별 특정 기간 내 이벤트 조회
    @Query("SELECT le FROM LifecycleEvent le WHERE le.user = :user " +
           "AND le.eventDate BETWEEN :startDate AND :endDate " +
           "ORDER BY le.eventDate ASC")
    List<LifecycleEvent> findByUserAndEventDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    // 오늘 발생하는 이벤트 조회
    @Query("SELECT le FROM LifecycleEvent le WHERE le.eventDate = :today " +
           "AND le.isProcessed = false ORDER BY le.createdAt ASC")
    List<LifecycleEvent> findTodayEvents(@Param("today") LocalDate today);
    
    // 다가오는 이벤트 조회 (D-7, D-3, D-1)
    @Query("SELECT le FROM LifecycleEvent le WHERE le.eventDate IN :dates " +
           "AND le.isProcessed = false ORDER BY le.eventDate ASC")
    List<LifecycleEvent> findUpcomingEvents(@Param("dates") List<LocalDate> dates);
    
    // 특정 사용자의 특정 타입 이벤트 조회
    List<LifecycleEvent> findByUserAndEventType(User user, LifecycleEventType eventType);
    
    // 특정 사용자의 자녀별 이벤트 조회
    List<LifecycleEvent> findByUserAndChildNameOrderByEventDateDesc(User user, String childName);
    
    // 사용자별 최근 이벤트 조회
    @Query("SELECT le FROM LifecycleEvent le WHERE le.user = :user " +
           "ORDER BY le.eventDate DESC LIMIT :limit")
    List<LifecycleEvent> findRecentEventsByUser(@Param("user") User user, @Param("limit") int limit);
    
    // 특정 날짜 범위의 미처리 이벤트 조회 (스케줄러용)
    @Query("SELECT le FROM LifecycleEvent le WHERE le.eventDate BETWEEN :startDate AND :endDate " +
           "AND le.isProcessed = false ORDER BY le.eventDate ASC, le.user.id ASC")
    List<LifecycleEvent> findUnprocessedEventsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    // 중복 이벤트 체크
    Optional<LifecycleEvent> findByUserAndEventTypeAndEventDateAndChildName(
            User user, 
            LifecycleEventType eventType, 
            LocalDate eventDate, 
            String childName
    );
    
    // 사용자별 이벤트 타입별 개수 조회
    @Query("SELECT le.eventType, COUNT(le) FROM LifecycleEvent le " +
           "WHERE le.user = :user GROUP BY le.eventType")
    List<Object[]> countEventsByTypeForUser(@Param("user") User user);
    
    // 처리된 이벤트 중 최근 처리된 것들 조회
    @Query("SELECT le FROM LifecycleEvent le WHERE le.user = :user " +
           "AND le.isProcessed = true ORDER BY le.processedAt DESC LIMIT :limit")
    List<LifecycleEvent> findRecentProcessedEvents(@Param("user") User user, @Param("limit") int limit);
}
