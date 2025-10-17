package com.hanaTI.HanaFuture.domain.auth.dto;

import com.hanaTI.HanaFuture.domain.user.entity.Role;
import com.hanaTI.HanaFuture.domain.user.entity.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth2 인증 후 추가 정보 입력 전까지 임시로 저장할 사용자 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingOAuth2User {
    private String email;
    private String name;
    private String providerId;
    private SocialProvider provider;
    private Role role;
    
    /**
     * 임시 사용자 정보를 실제 User 엔티티로 변환
     */
    public static PendingOAuth2User from(String email, String name, String providerId, SocialProvider provider) {
        return PendingOAuth2User.builder()
                .email(email)
                .name(name)
                .providerId(providerId)
                .provider(provider)
                .role(Role.USER)
                .build();
    }
}
