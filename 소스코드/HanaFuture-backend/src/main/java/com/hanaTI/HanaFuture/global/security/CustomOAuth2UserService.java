package com.hanaTI.HanaFuture.global.security;

import com.hanaTI.HanaFuture.domain.user.entity.Role;
import com.hanaTI.HanaFuture.domain.user.entity.SocialProvider;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("OAuth2 사용자 처리 중 오류 발생", ex);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 중 오류가 발생했습니다.");
        }
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());
        
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 이메일을 찾을 수 없습니다.");
        }
        
        // 기존 사용자인지 확인
        User existingUser = userRepository.findByEmail(userInfo.getEmail()).orElse(null);
        
        if (existingUser != null) {
            // 기존 사용자 - 정보 업데이트 후 로그인 처리
            log.info("기존 사용자 로그인: {}", existingUser.getEmail());
            User updatedUser = updateExistingUser(existingUser, userInfo);
            return OAuth2UserPrincipal.create(updatedUser, oauth2User.getAttributes());
        } else {
            log.info("새로운 OAuth2 사용자 - 추가 정보 입력 필요: {}", userInfo.getEmail());
            User tempUser = createTempUser(userInfo, registrationId);
            return OAuth2UserPrincipal.create(tempUser, oauth2User.getAttributes());
        }
    }
    
    /**
     * 임시 사용자 객체 생성 (DB에 저장하지 않음)
     * 추가 정보 입력 완료 후 실제 저장됨
     */
    private User createTempUser(OAuth2UserInfo userInfo, String provider) {
        SocialProvider socialProvider = "google".equals(provider) ? SocialProvider.GOOGLE : 
                                      "kakao".equals(provider) ? SocialProvider.KAKAO : 
                                      SocialProvider.LOCAL;
        
        // DB에 저장하지 않고 임시 객체만 생성
        return User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .password("OAUTH2_USER")  // OAuth2 사용자는 더미 패스워드 설정
                .provider(socialProvider)
                .providerId(userInfo.getId())
                .role(Role.USER)
                .build();
    }
    
    /**
     * 추가 정보 입력 완료 후 실제 사용자 등록
     */
    public User registerNewUser(OAuth2UserInfo userInfo, String provider) {
        SocialProvider socialProvider = "google".equals(provider) ? SocialProvider.GOOGLE : 
                                      "kakao".equals(provider) ? SocialProvider.KAKAO : 
                                      SocialProvider.LOCAL;
        
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .password("OAUTH2_USER")
                .provider(socialProvider)
                .providerId(userInfo.getId())
                .role(Role.USER)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("새로운 OAuth2 사용자 등록: {} ({})", savedUser.getEmail(), provider);
        
        return savedUser;
    }
    
    private User updateExistingUser(User existingUser, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !userInfo.getName().equals(existingUser.getName())) {
            existingUser.updateName(userInfo.getName());
            userRepository.save(existingUser);
            log.info("기존 사용자 정보 업데이트: {}", existingUser.getEmail());
        }
        
        return existingUser;
    }
}