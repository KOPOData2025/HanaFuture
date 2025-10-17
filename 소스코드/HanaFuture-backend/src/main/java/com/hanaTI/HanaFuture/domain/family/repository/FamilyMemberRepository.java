package com.hanaTI.HanaFuture.domain.family.repository;

import com.hanaTI.HanaFuture.domain.family.entity.FamilyMember;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    /**
     * 사용자의 모든 가족 멤버 조회
     */
    List<FamilyMember> findByOwnerOrderByCreatedAtDesc(User owner);

    /**
     * 사용자의 특정 관계 가족 멤버 조회
     */
    List<FamilyMember> findByOwnerAndRelationType(User owner, com.hanaTI.HanaFuture.domain.family.entity.FamilyRelationType relationType);

    /**
     * 전화번호로 가족 멤버 조회
     */
    Optional<FamilyMember> findByOwnerAndPhoneNumber(User owner, String phoneNumber);

    /**
     * 연결된 사용자로 가족 멤버 조회
     */
    Optional<FamilyMember> findByOwnerAndMemberUser(User owner, User memberUser);

    /**
     * 가족 멤버 수 조회
     */
    long countByOwner(User owner);
}

