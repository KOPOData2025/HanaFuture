package com.hanaTI.HanaFuture.domain.family.service;

import com.hanaTI.HanaFuture.domain.family.dto.FamilyMemberRequest;
import com.hanaTI.HanaFuture.domain.family.dto.FamilyMemberResponse;
import com.hanaTI.HanaFuture.domain.family.entity.FamilyMember;
import com.hanaTI.HanaFuture.domain.family.repository.FamilyMemberRepository;
import com.hanaTI.HanaFuture.domain.notification.entity.NotificationType;
import com.hanaTI.HanaFuture.domain.notification.service.NotificationService;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 가족 멤버 추가
     */
    @Transactional
    public FamilyMemberResponse addFamilyMember(Long userId, FamilyMemberRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이름으로 사용자 검색
        User targetUser = null;
        if (request.getName() != null) {
            targetUser = userRepository.findByName(request.getName())
                    .orElse(null);
        }

        // 가족 멤버 생성
        FamilyMember familyMember = FamilyMember.builder()
                .owner(owner)
                .memberUser(targetUser)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .relationType(request.getRelationType() != null ? request.getRelationType() : com.hanaTI.HanaFuture.domain.family.entity.FamilyRelationType.OTHER)
                .birthDate(request.getBirthDate())
                .memo(request.getMemo())
                .inviteStatus(targetUser != null ? "PENDING" : "NOT_INVITED")
                .build();

        familyMember = familyMemberRepository.save(familyMember);
        log.info("가족 멤버 추가 - 소유자: {}, 멤버: {}", owner.getName(), request.getName());

        // 등록된 사용자면 알림 전송
        if (targetUser != null) {
            String notificationTitle = String.format("%s님이 가족으로 초대합니다", owner.getName());
            String notificationContent = String.format("%s님이 가족 등록을 요청했습니다. 수락하시겠습니까?", owner.getName());

            notificationService.createNotification(
                    targetUser.getId(),
                    NotificationType.SYSTEM_NOTICE,
                    notificationTitle,
                    notificationContent,
                    familyMember.getId(),
                    "FamilyMember"
            );

            log.info(" 가족 등록 알림 전송 - 수신자: {}", targetUser.getName());
        }

        return FamilyMemberResponse.from(familyMember);
    }

    /**
     * 내 가족 목록 조회
     */
    public List<FamilyMemberResponse> getMyFamily(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<FamilyMember> familyMembers = familyMemberRepository.findByOwnerOrderByCreatedAtDesc(owner);

        return familyMembers.stream()
                .map(FamilyMemberResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 가족 멤버 상세 조회
     */
    public FamilyMemberResponse getFamilyMember(Long userId, Long familyMemberId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FamilyMember familyMember = familyMemberRepository.findById(familyMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인의 가족인지 확인
        if (!familyMember.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return FamilyMemberResponse.from(familyMember);
    }

    /**
     * 가족 멤버 정보 수정
     */
    @Transactional
    public FamilyMemberResponse updateFamilyMember(Long userId, Long familyMemberId, FamilyMemberRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FamilyMember familyMember = familyMemberRepository.findById(familyMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인의 가족인지 확인
        if (!familyMember.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        familyMember.updateInfo(request.getName(), request.getPhoneNumber(), request.getRelationType());

        log.info("가족 멤버 정보 수정 - ID: {}", familyMemberId);

        return FamilyMemberResponse.from(familyMember);
    }

    /**
     * 가족 멤버 삭제
     */
    @Transactional
    public void deleteFamilyMember(Long userId, Long familyMemberId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FamilyMember familyMember = familyMemberRepository.findById(familyMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인의 가족인지 확인
        if (!familyMember.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        familyMemberRepository.delete(familyMember);
        log.info("가족 멤버 삭제 - ID: {}", familyMemberId);
    }

    /**
     * 가족 초대 수락
     */
    @Transactional
    public void acceptFamilyInvite(Long userId, Long familyMemberId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FamilyMember familyMember = familyMemberRepository.findById(familyMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 본인에게 온 초대인지 확인
        if (familyMember.getMemberUser() == null || !familyMember.getMemberUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 초대 수락 처리
        familyMember.connectUser(user);
        
        User inviter = familyMember.getOwner();
        log.info("가족 초대 수락 - 멤버 ID: {}, 사용자: {}", familyMemberId, user.getName());

        // 역방향 관계 자동 생성 (이미 존재하는지 확인)
        boolean reverseExists = familyMemberRepository.findByOwnerOrderByCreatedAtDesc(user)
                .stream()
                .anyMatch(fm -> fm.getMemberUser() != null && fm.getMemberUser().getId().equals(inviter.getId()));

        if (!reverseExists) {
            FamilyMember reverseMember = FamilyMember.builder()
                    .owner(user)
                    .memberUser(inviter)
                    .name(inviter.getName())
                    .phoneNumber(inviter.getPhoneNumber())
                    .relationType(familyMember.getRelationType())
                    .inviteStatus("ACCEPTED")
                    .build();
            
            familyMemberRepository.save(reverseMember);
            log.info("역방향 가족 관계 생성 - 소유자: {}, 멤버: {}", user.getName(), inviter.getName());
        } else {
            log.info("ℹ 역방향 가족 관계가 이미 존재합니다 - 소유자: {}, 멤버: {}", user.getName(), inviter.getName());
        }
    }
}

