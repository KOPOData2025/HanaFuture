package com.hanaTI.HanaFuture.domain.child.repository;

import com.hanaTI.HanaFuture.domain.child.entity.Child;
import com.hanaTI.HanaFuture.domain.child.entity.ChildStatus;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    
    /**
     * 부모 사용자의 활성 자녀 목록 조회 (생성일 내림차순)
     */
    List<Child> findByParentUserAndStatusOrderByCreatedAtDesc(User parentUser, ChildStatus status);
    
    /**
     * 부모 사용자의 모든 자녀 목록 조회
     */
    List<Child> findByParentUserOrderByCreatedAtDesc(User parentUser);
    
    /**
     * 특정 이름의 자녀 조회 (부모별로 중복 가능)
     */
    List<Child> findByParentUserAndName(User parentUser, String name);
    
    /**
     * 생년월일로 자녀 조회
     */
    List<Child> findByBirthDate(LocalDate birthDate);
    
    /**
     * 학교별 자녀 목록 조회
     */
    List<Child> findBySchoolNameOrderByGradeAscClassNumberAsc(String schoolName);
    
    /**
     * 학년별 자녀 목록 조회
     */
    List<Child> findByGradeOrderByClassNumberAsc(Integer grade);
    
    /**
     * 특정 학급의 자녀 목록 조회
     */
    List<Child> findByGradeAndClassNumberOrderByNameAsc(Integer grade, Integer classNumber);
    
    /**
     * 부모 사용자의 자녀 수 조회
     */
    long countByParentUserAndStatus(User parentUser, ChildStatus status);
    
    /**
     * 특정 학교의 총 자녀 수 조회
     */
    long countBySchoolNameAndStatus(String schoolName, ChildStatus status);
    
    /**
     * 금융 교육 레벨별 자녀 목록 조회
     */
    List<Child> findByFinancialEducationLevelOrderByRewardPointsDesc(Integer level);
    
    /**
     * 리워드 포인트 상위 자녀 목록 조회
     */
    @Query("SELECT c FROM Child c WHERE c.status = :status ORDER BY c.rewardPoints DESC")
    List<Child> findTopByRewardPointsOrderByRewardPointsDesc(@Param("status") ChildStatus status);
    
    /**
     * 특정 기간에 등록된 자녀 목록 조회
     */
    @Query("SELECT c FROM Child c WHERE c.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY c.createdAt DESC")
    List<Child> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate);
}










