package com.hanaTI.HanaFuture.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    LOCAL("local", "일반 가입"),
    GOOGLE("google", "구글"),
    KAKAO("kakao", "카카오");
    
    private final String key;
    private final String title;
}