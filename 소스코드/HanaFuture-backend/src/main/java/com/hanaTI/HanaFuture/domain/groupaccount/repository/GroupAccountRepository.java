package com.hanaTI.HanaFuture.domain.groupaccount.repository;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountStatus;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupAccountRepository extends JpaRepository<GroupAccount, Long> {

    // 계좌번호로 조회 (중복 확인용)
    Optional<GroupAccount> findByGroupAccountNumber(String groupAccountNumber);

    // 생성자로 조회
    List<GroupAccount> findByCreatorOrderByCreatedAtDesc(User creator);

    // 생성자와 상태로 조회
    List<GroupAccount> findByCreatorAndStatusOrderByCreatedAtDesc(User creator, GroupAccountStatus status);

    // 사용자가 참여한 모임통장 조회 (멤버십을 통해)
    @Query("SELECT DISTINCT ga FROM GroupAccount ga " +
           "JOIN ga.members m " +
           "WHERE m.user = :user AND m.status = 'ACTIVE' " +
           "ORDER BY ga.createdAt DESC")
    List<GroupAccount> findByUserMembership(@Param("user") User user);

    // 사용자가 참여한 활성 모임통장 조회
    @Query("SELECT DISTINCT ga FROM GroupAccount ga " +
           "JOIN ga.members m " +
           "WHERE m.user = :user " +
           "AND m.status = 'ACTIVE' " +
           "AND ga.status = 'ACTIVE' " +
           "ORDER BY ga.createdAt DESC")
    List<GroupAccount> findActiveByUserMembership(@Param("user") User user);

    // 이름으로 조회 (중복 체크용)
    boolean existsByName(String name);

    // 상태별 조회
    List<GroupAccount> findByStatusOrderByCreatedAtDesc(GroupAccountStatus status);

    // 총 모임통장 수 (사용자별)
    @Query("SELECT COUNT(DISTINCT ga) FROM GroupAccount ga " +
           "JOIN ga.members m " +
           "WHERE m.user = :user AND m.status = 'ACTIVE'")
    long countByUserMembership(@Param("user") User user);

    // 페이징 조회 (사용자 참여 기준)
    @Query("SELECT DISTINCT ga FROM GroupAccount ga " +
           "JOIN ga.members m " +
           "WHERE m.user = :user AND m.status = 'ACTIVE' " +
           "ORDER BY ga.createdAt DESC")
    Page<GroupAccount> findByUserMembershipWithPaging(@Param("user") User user, Pageable pageable);
}

