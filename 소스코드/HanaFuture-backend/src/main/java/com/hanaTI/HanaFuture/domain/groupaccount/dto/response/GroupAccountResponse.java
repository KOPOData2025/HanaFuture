package com.hanaTI.HanaFuture.domain.groupaccount.dto.response;

import com.hanaTI.HanaFuture.domain.groupaccount.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAccountResponse {

    private Long id;
    private String name;
    private String description;
    private GroupAccountPurpose purpose;
    private String purposeDescription;
    private String accountNumber;
    private String bankCode;
    private String bankName;
    private BigDecimal currentBalance;
    private GroupAccountStatus status;
    private String statusDescription;
    private Boolean autoTransferEnabled;
    private Integer autoTransferDay;
    private Boolean notificationEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 계산된 필드들
    private Long activeMembersCount;
    private List<String> memberNames; // 활성 멤버 이름 목록

    // 생성자 정보
    private Long creatorId;
    private String creatorName;

    // 멤버 정보 (요약)
    private List<GroupAccountMemberSummary> members;

    // 최근 거래내역 (요약)
    private List<GroupAccountTransactionSummary> recentTransactions;

    @Data
    @Builder
    public static class GroupAccountMemberSummary {
        private Long memberId;
        private Long userId;
        private String userName;
        private String userEmail;
        private GroupAccountRole role;
        private String roleDescription;
        private GroupAccountMemberStatus status;
        private String statusDescription;
        private BigDecimal contributionAmount;
        private BigDecimal monthlyContribution;
        private Boolean autoTransferEnabled;
        private LocalDateTime joinedAt;
    }

    @Data
    @Builder
    public static class GroupAccountTransactionSummary {
        private Long transactionId;
        private GroupAccountTransactionType type;
        private String typeDescription;
        private BigDecimal amount;
        private String description;
        private Long userId;
        private String userName;
        private GroupAccountTransactionStatus status;
        private String statusDescription;
        private LocalDateTime createdAt;
    }

    // Entity에서 DTO로 변환하는 정적 메서드
    public static GroupAccountResponse from(GroupAccount groupAccount) {
        // 활성 멤버 이름 목록 추출
        List<String> memberNames = groupAccount.getMembers().stream()
                .filter(member -> member.getStatus() == GroupAccountMemberStatus.ACTIVE)
                .map(member -> member.getUserName() != null ? member.getUserName() : member.getName())
                .filter(name -> name != null && !name.isEmpty())
                .collect(java.util.stream.Collectors.toList());
        
        return GroupAccountResponse.builder()
                .id(groupAccount.getId())
                .name(groupAccount.getName())
                .description(groupAccount.getDescription())
                .purpose(groupAccount.getPurpose())
                .purposeDescription(groupAccount.getPurpose().getDescription())
                .accountNumber(groupAccount.getGroupAccountNumber())
                .bankCode(groupAccount.getBankCode())
                .bankName(groupAccount.getBankName())
                .currentBalance(groupAccount.getCurrentBalance())
                .status(groupAccount.getStatus())
                .statusDescription(groupAccount.getStatus().getDescription())
                .autoTransferEnabled(groupAccount.getAutoTransferEnabled())
                .autoTransferDay(groupAccount.getAutoTransferDay())
                .notificationEnabled(groupAccount.getNotificationEnabled())
                .createdAt(groupAccount.getCreatedAt())
                .updatedAt(groupAccount.getUpdatedAt())
                .activeMembersCount(groupAccount.getActiveMembersCount())
                .memberNames(memberNames)
                .creatorId(groupAccount.getCreator().getId())
                .creatorName(groupAccount.getCreator().getName())
                .build();
    }
}
