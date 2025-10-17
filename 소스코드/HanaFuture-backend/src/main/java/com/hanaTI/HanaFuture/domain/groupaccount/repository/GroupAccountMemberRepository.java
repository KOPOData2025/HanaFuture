package com.hanaTI.HanaFuture.domain.groupaccount.repository;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountMember;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountMemberStatus;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountRole;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupAccountMemberRepository extends JpaRepository<GroupAccountMember, Long> {

    // 특정 모임통장의 멤버 조회
    List<GroupAccountMember> findByGroupAccountAndStatus(GroupAccount groupAccount, GroupAccountMemberStatus status);

    // 특정 사용자의 모임통장 멤버십 조회
    List<GroupAccountMember> findByUserAndStatus(User user, GroupAccountMemberStatus status);

    // 특정 모임통장에서 특정 사용자 조회
    Optional<GroupAccountMember> findByGroupAccountAndUser(GroupAccount groupAccount, User user);

    // 특정 모임통장에서 특정 사용자의 활성 멤버십 조회
    Optional<GroupAccountMember> findByGroupAccountAndUserAndStatus(
            GroupAccount groupAccount, User user, GroupAccountMemberStatus status);

    // 특정 모임통장의 관리자 조회
    List<GroupAccountMember> findByGroupAccountAndRole(GroupAccount groupAccount, GroupAccountRole role);

    // 특정 모임통장의 활성 멤버 수 조회
    @Query("SELECT COUNT(m) FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount AND m.status = 'ACTIVE'")
    long countActiveMembers(@Param("groupAccount") GroupAccount groupAccount);

    // 특정 모임통장의 총 기여금 조회
    @Query("SELECT COALESCE(SUM(m.contributionAmount), 0) FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount AND m.status = 'ACTIVE'")
    BigDecimal getTotalContribution(@Param("groupAccount") GroupAccount groupAccount);

    // 특정 사용자의 특정 모임통장 기여금 조회
    @Query("SELECT COALESCE(m.contributionAmount, 0) FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount AND m.user = :user AND m.status = 'ACTIVE'")
    BigDecimal getUserContribution(@Param("groupAccount") GroupAccount groupAccount, @Param("user") User user);

    // 자동이체 설정된 멤버 조회
    @Query("SELECT m FROM GroupAccountMember m " +
           "WHERE m.autoTransferEnabled = true AND m.status = 'ACTIVE'")
    List<GroupAccountMember> findMembersWithAutoTransfer();

    // 특정 모임통장에서 자동이체 설정된 멤버 조회
    @Query("SELECT m FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount " +
           "AND m.autoTransferEnabled = true AND m.status = 'ACTIVE'")
    List<GroupAccountMember> findMembersWithAutoTransferByAccount(@Param("groupAccount") GroupAccount groupAccount);

    // 멤버십 존재 여부 확인
    boolean existsByGroupAccountAndUserAndStatus(GroupAccount groupAccount, User user, GroupAccountMemberStatus status);

    // 멤버십 존재 여부 확인 (상태 무관)
    boolean existsByGroupAccountAndUser(GroupAccount groupAccount, User user);

    // 특정 역할의 멤버 수 조회
    @Query("SELECT COUNT(m) FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount AND m.role = :role AND m.status = 'ACTIVE'")
    long countMembersByRole(@Param("groupAccount") GroupAccount groupAccount, @Param("role") GroupAccountRole role);
    
    // 전화번호로 PENDING 멤버 조회 (초대 토큰 생성 시 중복 확인)
    @Query("SELECT m FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount AND m.phone = :phone AND m.status = 'PENDING'")
    List<GroupAccountMember> findByGroupAccountAndPhone(@Param("groupAccount") GroupAccount groupAccount, @Param("phone") String phone);
    
    // 초대 토큰으로 멤버 조회
    @Query("SELECT m FROM GroupAccountMember m " +
           "WHERE m.groupAccount = :groupAccount AND m.inviteToken = :token")
    List<GroupAccountMember> findByGroupAccountAndInviteToken(@Param("groupAccount") GroupAccount groupAccount, @Param("token") String token);
}
