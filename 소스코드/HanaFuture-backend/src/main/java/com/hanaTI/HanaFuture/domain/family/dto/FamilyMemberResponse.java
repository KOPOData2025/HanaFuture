package com.hanaTI.HanaFuture.domain.family.dto;

import com.hanaTI.HanaFuture.domain.family.entity.FamilyMember;
import com.hanaTI.HanaFuture.domain.family.entity.FamilyRelationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMemberResponse {

    private Long id;
    private String name;
    private String phoneNumber;
    private FamilyRelationType relationType;
    private LocalDateTime birthDate;
    private String inviteStatus;
    private String memo;
    private LocalDateTime createdAt;
    
    // 연결된 사용자 정보
    private Long memberUserId;
    private String memberUserEmail;
    private Boolean isConnected;

    public static FamilyMemberResponse from(FamilyMember member) {
        return FamilyMemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .relationType(member.getRelationType())
                .birthDate(member.getBirthDate())
                .inviteStatus(member.getInviteStatus())
                .memo(member.getMemo())
                .createdAt(member.getCreatedAt())
                .memberUserId(member.getMemberUser() != null ? member.getMemberUser().getId() : null)
                .memberUserEmail(member.getMemberUser() != null ? member.getMemberUser().getEmail() : null)
                .isConnected(member.getMemberUser() != null)
                .build();
    }
}

