package com.hanaTI.HanaFuture.domain.notification.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class GroupInviteRequest {
    
    @NotBlank(message = "그룹 ID는 필수입니다")
    private String groupId;
    
    @NotBlank(message = "그룹명은 필수입니다")
    private String groupName;
    
    @NotBlank(message = "초대자명은 필수입니다")
    private String inviterName;
    
    @NotEmpty(message = "초대할 멤버 목록은 필수입니다")
    @Valid
    private List<MemberInfo> members;

    @Data
    public static class MemberInfo {
        
        @NotBlank(message = "멤버 이름은 필수입니다")
        private String name;
        
        @NotBlank(message = "전화번호는 필수입니다")
        @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
        private String phoneNumber;
        
        private String email; // 선택사항
        
        private String role = "member"; // 역할 (owner, member)
    }
}
