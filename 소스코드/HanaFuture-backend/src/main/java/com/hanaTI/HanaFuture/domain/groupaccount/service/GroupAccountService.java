package com.hanaTI.HanaFuture.domain.groupaccount.service;

import com.hanaTI.HanaFuture.domain.groupaccount.dto.request.GroupAccountRequest;
import com.hanaTI.HanaFuture.domain.groupaccount.dto.response.GroupAccountResponse;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountMember;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountMemberStatus;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountRole;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountStatus;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountMemberRepository;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupAccountService {
    
    private final GroupAccountRepository groupAccountRepository;
    private final UserRepository userRepository;
    private final GroupAccountMemberRepository groupAccountMemberRepository;
    private final com.hanaTI.HanaFuture.domain.notification.service.NotificationService notificationService;
    private final com.hanaTI.HanaFuture.domain.family.repository.FamilyMemberRepository familyMemberRepository;
    
    @Transactional
    public GroupAccountResponse createGroupAccount(GroupAccountRequest request) {
        log.info("모임통장 개설 시작: 출금계좌 ID {}, 통장명 {}", 
                request.getWithdrawAccountId(), request.getName());
        
        // 1. 사용자 확인
        User primaryUser = userRepository.findById(request.getPrimaryUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccountNumber = generateNewAccountNumber();
        
        log.info("새 모임통장 계좌번호 생성: {}", newAccountNumber);
        
        // 3. 모임통장 엔티티 생성
        GroupAccount groupAccount = GroupAccount.builder()
                .name(request.getName()) // 모임통장 이름
                .groupAccountName(request.getGroupAccountName()) // 계좌명
                .groupAccountNumber(newAccountNumber) // 새로 생성된 계좌번호
                .primaryUserId(request.getPrimaryUserId())
                .primaryUserName(primaryUser.getName())
                .bankCode("081") // 하나은행
                .bankName("하나은행")
                .password(request.getAccountPassword()) // 계좌 비밀번호 설정
                .purpose(request.getPurpose())
                .status(GroupAccountStatus.ACTIVE)
                .sourceAccountNumber(request.getPrimaryFintechUseNum()) // 출금계좌 (기존계좌)
                .creator(primaryUser) // 생성자 설정
                .build();
        
        log.info("계좌 비밀번호 설정: {}", request.getAccountPassword());
        
        // 4. 데이터베이스 저장
        GroupAccount savedAccount = groupAccountRepository.save(groupAccount);
        log.info(" 모임통장 DB 저장 완료: ID {}, 계좌번호 {}, 생성자 ID {}", 
                savedAccount.getId(), savedAccount.getGroupAccountNumber(), savedAccount.getCreator().getId());
        
        // 5. 생성자를 모임통장 멤버로 자동 추가
        addCreatorAsMember(savedAccount, primaryUser);

        setupAutoTransfer(savedAccount, request);
        
        log.info("모임통장 개설 완료: ID {}, 계좌번호 {}", 
                savedAccount.getId(), savedAccount.getGroupAccountNumber());
        
        // 6. 응답 DTO 생성
        return GroupAccountResponse.builder()
                .id(savedAccount.getId())
                .name(savedAccount.getName())
                .description(savedAccount.getDescription())
                .accountNumber(savedAccount.getGroupAccountNumber())
                .bankCode(savedAccount.getBankCode())
                .bankName(savedAccount.getBankName())
                .currentBalance(savedAccount.getCurrentBalance())
                .purpose(savedAccount.getPurpose())
                .status(savedAccount.getStatus())
                .createdAt(savedAccount.getCreatedAt())
                .build();
    }
    
    /**
     * 새로운 모임통장 계좌번호 생성
     */
    private String generateNewAccountNumber() {
        String prefix = "817"; // 하나은행 모임통장 코드
        
        // 중간 3자리: 101~999 범위의 랜덤 숫자
        int middle = 101 + new Random().nextInt(899);
        
        // 마지막 6자리: 100000~999999 범위의 랜덤 숫자
        int suffix = 100000 + new Random().nextInt(900000);
        
        String accountNumber = String.format("%s-%03d-%06d", prefix, middle, suffix);
        
        // 중복 확인 (실제로는 DB 체크 필요)
        if (groupAccountRepository.findByGroupAccountNumber(accountNumber).isPresent()) {
            return generateNewAccountNumber(); // 재귀 호출로 중복 해결
        }
        
        return accountNumber;
    }
    
    /**
     * 생성자를 모임통장 멤버로 자동 추가
     */
    private void addCreatorAsMember(GroupAccount groupAccount, User creator) {
        log.info(" 모임통장 생성자를 멤버로 추가: 사용자 ID {}, 이름 {}", creator.getId(), creator.getName());
        
        GroupAccountMember creatorMember = GroupAccountMember.builder()
                .groupAccount(groupAccount)
                .user(creator)
                .userName(creator.getName())
                .userEmail(creator.getEmail())
                .phoneNumber(creator.getPhoneNumber())
                .role(GroupAccountRole.ADMIN)
                .status(GroupAccountMemberStatus.ACTIVE)
                .contributionAmount(BigDecimal.ZERO)
                .build();
        
        GroupAccountMember savedMember = groupAccountMemberRepository.save(creatorMember);
        log.info("생성자 멤버 추가 완료: ID {}, 역할 {}", savedMember.getId(), savedMember.getRole());
    }
    
    /**
     * 자동이체 설정 (향후 구현)
     */
    private void setupAutoTransfer(GroupAccount groupAccount, GroupAccountRequest request) {
        log.info("자동이체 설정: {} -> {}", 
                request.getPrimaryFintechUseNum(), 
                groupAccount.getGroupAccountNumber());

        // 1. 기존 계좌에서 새 모임통장으로 매월 자동이체 설정
        // 2. 이체일: 매월 설정한 날짜
        // 3. 이체금액: monthlyTarget
        
        log.info("자동이체 설정 완료 (Mock)");
    }
    
    /**
     * 사용자의 모임통장 목록 조회 (생성한 것 + 참여한 것)
     */
    public java.util.List<GroupAccountResponse> getUserGroupAccounts(Long userId) {
        log.info("사용자 {}의 모임통장 목록 조회 시작", userId);
        
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 1. 사용자가 생성한 모임통장 조회
        var createdAccounts = groupAccountRepository.findByCreatorOrderByCreatedAtDesc(user);
        log.info(" 사용자 {}가 생성한 모임통장: {}개", userId, createdAccounts.size());
        
        // 2. 사용자가 참여한 모임통장 조회 (멤버십을 통해)
        var memberAccounts = groupAccountRepository.findActiveByUserMembership(user);
        log.info(" 사용자 {}가 참여한 모임통장: {}개", userId, memberAccounts.size());
        
        // 3. 중복 제거하여 합치기 (Set 사용)
        var allAccountsSet = new java.util.LinkedHashSet<GroupAccount>();
        allAccountsSet.addAll(createdAccounts);
        allAccountsSet.addAll(memberAccounts);
        
        // 4. CLOSED 상태인 계좌 필터링
        var allAccounts = new java.util.ArrayList<>(allAccountsSet);
        allAccounts.removeIf(account -> account.getStatus() == GroupAccountStatus.CLOSED);
        
        log.info(" 사용자 {}({})의 전체 모임통장 {}개 조회됨 (CLOSED 제외)", userId, user.getName(), allAccounts.size());
        
        // 디버깅: 각 모임통장 정보 출력
        for (GroupAccount account : allAccounts) {
            log.info(" 모임통장: ID={}, 이름={}, 계좌번호={}, 생성자ID={}", 
                    account.getId(), account.getName(), account.getGroupAccountNumber(), account.getCreator().getId());
        }
        
        // DTO로 변환하여 반환 (멤버 정보를 함께 로드)
        return allAccounts.stream()
                .map(account -> {
                    // 멤버 정보를 강제로 로드하여 getActiveMembersCount()가 정상 작동하도록 함
                    int totalMembers = account.getMembers().size();
                    long activeMembers = account.getActiveMembersCount();
                    log.info(" 모임통장 '{}' 멤버 정보: 전체={}명, 활성={}명", 
                            account.getName(), totalMembers, activeMembers);
                    
                    // 각 멤버의 상태 출력
                    account.getMembers().forEach(member -> {
                        log.info("   멤버: 이름={}, 상태={}, 역할={}", 
                                member.getUserName() != null ? member.getUserName() : member.getName(),
                                member.getStatus(),
                                member.getRole());
                    });
                    
                    return GroupAccountResponse.from(account);
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 모임통장 ID로 조회
     */
    public GroupAccount getGroupAccountById(Long groupAccountId) {
        return groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_ACCOUNT_NOT_FOUND));
    }
    
    /**
     * PENDING 초대 생성 (카카오톡 전송 전)
     */
    @Transactional
    public Map<String, String> createPendingInvite(Long groupAccountId, String name, String phone, Long inviterId) {
        log.info(" PENDING 초대 생성 - 모임통장 ID: {}, 이름: {}, 전화번호: {}", groupAccountId, name, phone);
        
        // 1. 모임통장 존재 확인
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 초대자 존재 확인
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 3. 토큰 생성 (name + phone + timestamp 기반)
        String token = generateInviteToken(name, phone);
        
        // 4. 이름과 전화번호로 사용자 검색
        java.util.Optional<User> targetUserOpt = userRepository.findByNameAndPhoneNumber(name, phone);
        
        // 5. PENDING 멤버 생성 (user 없이, 전화번호 기준)
        // 동일 전화번호로 이미 PENDING 초대가 있는지 확인
        List<GroupAccountMember> existingPending = groupAccountMemberRepository
                .findByGroupAccountAndPhone(groupAccount, phone);
        
        GroupAccountMember pendingMember;
        
        if (!existingPending.isEmpty()) {
            // 기존 PENDING 초대를 재사용하고 토큰만 업데이트
            pendingMember = existingPending.get(0);
            pendingMember.updateInviteToken(token);
            log.info("기존 PENDING 초대 갱신: {}, {}", name, phone);
        } else {
            // 새로운 PENDING 멤버 생성
            pendingMember = GroupAccountMember.builder()
                    .groupAccount(groupAccount)
                    .user(null) // 아직 가입하지 않은 상태
                    .name(name)
                    .phone(phone)
                    .role(GroupAccountRole.MEMBER)
                    .status(GroupAccountMemberStatus.PENDING)
                    .invitedBy(inviter)
                    .inviteToken(token)
                    .build();
            
            pendingMember = groupAccountMemberRepository.save(pendingMember);
            log.info("새로운 PENDING 멤버 생성: {}, {}", name, phone);
        }
        
        // 6. 사용자가 등록되어 있으면 알림 생성
        if (targetUserOpt.isPresent()) {
            User targetUser = targetUserOpt.get();
            log.info(" 등록된 사용자 발견: {} ({}), 알림 생성", targetUser.getName(), targetUser.getEmail());
            
            String notificationTitle = String.format("%s님이 모임통장 '%s'에 초대했습니다", 
                    inviter.getName(), groupAccount.getName());
            String notificationContent = String.format("'%s' 모임통장에 참여하시겠습니까? 수락하시면 바로 참여할 수 있습니다.", 
                    groupAccount.getName());
            
            notificationService.createNotification(
                    targetUser.getId(),
                    com.hanaTI.HanaFuture.domain.notification.entity.NotificationType.GROUP_ACCOUNT_INVITE,
                    notificationTitle,
                    notificationContent,
                    pendingMember.getId(),  // PENDING 멤버 ID 저장
                    "GroupAccountMember"
            );
            
            log.info("모임통장 초대 알림 생성 완료 - 수신자: {}", targetUser.getName());
        } else {
            log.info("ℹ 등록되지 않은 사용자: {}, {} - 알림 없이 PENDING 상태로 대기", name, phone);
        }
        
        // 7. 초대 URL 생성 (name은 URL 인코딩)
        try {
            String encodedName = java.net.URLEncoder.encode(name, "UTF-8");
            String inviteUrl = String.format("http://localhost:3000/invite?groupId=%d&token=%s&name=%s", 
                    groupAccountId, token, encodedName);
            
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            result.put("inviteUrl", inviteUrl);
            result.put("groupName", groupAccount.getName());
            result.put("inviteeName", name);  // 원본 이름도 추가
            
            return result;
        } catch (java.io.UnsupportedEncodingException e) {
            log.error("URL 인코딩 실패", e);
            throw new RuntimeException("URL 생성 실패", e);
        }
    }
    
    /**
     * 모임통장 초대 수락
     */
    @Transactional
    public void acceptInvite(Long groupAccountId, Long userId, String token) {
        log.info(" 모임통장 초대 수락 처리 시작 - 모임통장 ID: {}, 사용자 ID: {}", groupAccountId, userId);
        
        // 1. 모임통장 존재 확인
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 3. 이미 활성 멤버인지 확인
        boolean alreadyActiveMember = groupAccountMemberRepository
                .existsByGroupAccountAndUserAndStatus(groupAccount, user, GroupAccountMemberStatus.ACTIVE);
        
        if (alreadyActiveMember) {
            log.warn("이미 활성 멤버임: 사용자 ID {}", userId);
            throw new CustomException(ErrorCode.ALREADY_GROUP_MEMBER);
        }
        
        // 4. 토큰 검증 - PENDING 멤버 찾기 (선택적)
        List<GroupAccountMember> pendingMembers = groupAccountMemberRepository
                .findByGroupAccountAndInviteToken(groupAccount, token);
        
        if (!pendingMembers.isEmpty()) {
            // PENDING 멤버가 있으면 ACTIVE로 전환
            GroupAccountMember pendingMember = pendingMembers.get(0);
            pendingMember.updateUserInfo(user);
            pendingMember.updateStatus(GroupAccountMemberStatus.ACTIVE);
            log.info("PENDING → ACTIVE 전환 완료 - 멤버 ID: {}, 사용자: {}", pendingMember.getId(), user.getName());
        } else {
            // PENDING 멤버가 없으면 새로운 멤버로 추가
            log.info("ℹ PENDING 멤버가 없어 새 멤버로 추가합니다.");
            GroupAccountMember newMember = GroupAccountMember.builder()
                    .groupAccount(groupAccount)
                    .user(user)
                    .role(GroupAccountRole.MEMBER)
                    .status(GroupAccountMemberStatus.ACTIVE)
                    .contributionAmount(BigDecimal.ZERO)
                    .autoTransferEnabled(false)
                    .joinedAt(java.time.LocalDateTime.now())
                    .build();
            
            groupAccountMemberRepository.save(newMember);
            log.info("새 멤버 추가 완료 - 사용자: {}", user.getName());
        }
    }
    
    /**
     * 모임통장 멤버 초대 수락 (memberId 기반)
     */
    @Transactional
    public void acceptInviteByMemberId(Long memberId, Long userId) {
        log.info(" 모임통장 멤버 초대 수락 처리 시작 - 멤버 ID: {}, 사용자 ID: {}", memberId, userId);
        
        // 1. 멤버 존재 확인
        GroupAccountMember member = groupAccountMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 3. 멤버가 PENDING 상태인지 확인
        if (member.getStatus() != GroupAccountMemberStatus.PENDING) {
            log.warn("멤버가 PENDING 상태가 아님: {}", member.getStatus());
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        
        // 4. PENDING → ACTIVE 전환
        member.updateUserInfo(user);
        member.updateStatus(GroupAccountMemberStatus.ACTIVE);
        
        log.info("모임통장 멤버 초대 수락 완료 - 멤버 ID: {}, 사용자: {}, 모임통장: {}", 
                memberId, user.getName(), member.getGroupAccount().getName());
    }
    
    /**
     * 모임통장 멤버 목록 조회
     */
    public Map<String, Object> getGroupAccountMembers(Long groupAccountId) {
        log.info("모임통장 멤버 목록 조회 시작 - 모임통장 ID: {}", groupAccountId);
        
        // 1. 모임통장 존재 확인
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 멤버 목록 조회
        List<GroupAccountMember> allMembers = groupAccount.getMembers();
        
        // 3. 활성 멤버와 초대 대기 멤버 분리
        List<Map<String, Object>> activeMembers = new ArrayList<>();
        List<Map<String, Object>> pendingInvites = new ArrayList<>();
        
        BigDecimal totalContribution = BigDecimal.ZERO;
        
        for (GroupAccountMember member : allMembers) {
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("id", member.getId());
            
            // PENDING 멤버는 name/phone 필드 사용, ACTIVE 멤버는 userName/phoneNumber 사용
            if (member.getStatus() == GroupAccountMemberStatus.PENDING) {
                memberData.put("name", member.getName());  // PENDING용 name 필드
                memberData.put("phoneNumber", member.getPhone());  // PENDING용 phone 필드
                memberData.put("email", null);  // PENDING 멤버는 이메일 없음
            } else {
                memberData.put("name", member.getUserName());
                memberData.put("email", member.getUserEmail());
                memberData.put("phoneNumber", member.getPhoneNumber());
            }
            
            memberData.put("role", member.getRole().name());
            memberData.put("status", member.getStatus().name());
            memberData.put("joinedAt", member.getJoinedAt());
            memberData.put("createdAt", member.getCreatedAt());
            memberData.put("contributionAmount", member.getContributionAmount() != null ? member.getContributionAmount() : BigDecimal.ZERO);
            
            if (member.getStatus() == GroupAccountMemberStatus.ACTIVE) {
                activeMembers.add(memberData);
                BigDecimal contribution = member.getContributionAmount() != null ? member.getContributionAmount() : BigDecimal.ZERO;
                totalContribution = totalContribution.add(contribution);
            } else if (member.getStatus() == GroupAccountMemberStatus.PENDING) {
                memberData.put("invitedAt", member.getCreatedAt());
                memberData.put("expiresAt", member.getInvitationExpiresAt());
                pendingInvites.add(memberData);
                log.info("PENDING 멤버 발견: {} ({})", member.getName(), member.getPhone());
            }
        }
        
        // 4. 통계 계산
        BigDecimal averageContribution = activeMembers.isEmpty() ? 
            BigDecimal.ZERO : 
            totalContribution.divide(BigDecimal.valueOf(activeMembers.size()), 2, java.math.RoundingMode.HALF_UP);
        
        // 5. 응답 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("members", activeMembers);
        result.put("invites", pendingInvites);
        result.put("stats", Map.of(
            "totalMembers", activeMembers.size(),
            "pendingInvites", pendingInvites.size(),
            "totalContribution", totalContribution,
            "averageContribution", averageContribution
        ));
        
        log.info("멤버 목록 조회 완료 - 활성 멤버: {}명, 대기 초대: {}명", 
                activeMembers.size(), pendingInvites.size());
        
        return result;
    }
    
    /**
     * 모임통장 삭제
     */
    @Transactional
    public void deleteGroupAccount(Long groupAccountId, Long userId) {
        log.info(" 모임통장 삭제 시작 - 모임통장 ID: {}, 사용자 ID: {}", groupAccountId, userId);
        
        // 1. 모임통장 존재 확인
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 사용자 권한 확인 (생성자만 삭제 가능)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        if (!groupAccount.getCreator().getId().equals(userId)) {
            log.warn("권한 없음 - 사용자 {}는 모임통장 {}의 생성자가 아님", userId, groupAccountId);
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        
        // 3. 잔액이 남아있으면 경고 로그만 출력 (삭제는 허용)
        if (groupAccount.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
            log.warn("잔액이 남아있는 모임통장 삭제 - 잔액: {}", groupAccount.getCurrentBalance());
        }
        
        // 4. 상태를 CLOSED로 변경 (Soft Delete)
        groupAccount.updateStatus(GroupAccountStatus.CLOSED);
        groupAccountRepository.save(groupAccount);
        
        log.info("모임통장 삭제 완료 - ID: {}, 계좌번호: {}, 남은 잔액: {}", 
                groupAccountId, groupAccount.getGroupAccountNumber(), groupAccount.getCurrentBalance());
    }
    
    /**
     * Base64 인코딩 (간단한 토큰 생성용)
     */
    /**
     * 초대 토큰 생성 (name + phone + timestamp 기반)
     */
    private String generateInviteToken(String name, String phone) {
        String rawToken = name + ":" + phone + ":" + System.currentTimeMillis();
        return btoa(rawToken);
    }
    
    private String btoa(String input) {
        return java.util.Base64.getEncoder().encodeToString(input.getBytes());
    }
    
    /**
     * 모임통장 멤버 추가를 위한 정보 조회 (가족 목록 포함)
     */
    public Map<String, Object> getMemberAdditionInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 가족 목록 조회
        List<com.hanaTI.HanaFuture.domain.family.entity.FamilyMember> familyMembers = 
                familyMemberRepository.findByOwnerOrderByCreatedAtDesc(user);
        
        // 가족 정보를 Map으로 변환
        List<Map<String, Object>> familyList = familyMembers.stream()
                .map(family -> {
                    Map<String, Object> familyInfo = new HashMap<>();
                    familyInfo.put("id", family.getId());
                    familyInfo.put("name", family.getName());
                    familyInfo.put("phoneNumber", family.getPhoneNumber());
                    familyInfo.put("relationType", family.getRelationType().name());
                    familyInfo.put("relationDescription", family.getRelationType().getDescription());
                    familyInfo.put("isConnected", family.getMemberUser() != null);
                    familyInfo.put("memberUserId", family.getMemberUser() != null ? family.getMemberUser().getId() : null);
                    familyInfo.put("inviteStatus", family.getInviteStatus());
                    return familyInfo;
                })
                .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("familyMembers", familyList);
        result.put("familyCount", familyList.size());
        
        log.info("멤버 추가 정보 조회 완료 - 사용자: {}, 가족 수: {}", user.getName(), familyList.size());
        
        return result;
    }
}
