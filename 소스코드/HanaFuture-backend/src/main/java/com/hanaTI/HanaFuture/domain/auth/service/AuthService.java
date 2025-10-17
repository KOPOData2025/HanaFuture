package com.hanaTI.HanaFuture.domain.auth.service;

import com.hanaTI.HanaFuture.domain.auth.dto.*;
import com.hanaTI.HanaFuture.domain.user.entity.Gender;
import com.hanaTI.HanaFuture.domain.user.entity.IncomeLevel;
import com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus;
import com.hanaTI.HanaFuture.domain.user.entity.Role;
import com.hanaTI.HanaFuture.domain.user.entity.SocialProvider;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.ai.service.BackgroundAIRecommendationService;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import com.hanaTI.HanaFuture.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BackgroundAIRecommendationService backgroundAIService;
    
    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        
        User savedUser = userRepository.save(user);
        
        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(savedUser.getEmail(), savedUser.getId());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail(), savedUser.getId());
        
        // 리프레시 토큰 저장
        savedUser.updateRefreshToken(refreshToken);
        userRepository.save(savedUser);
        
        log.info("새 사용자 가입: {}", savedUser.getEmail());
        
        return TokenResponse.of(accessToken, refreshToken, 7200L); // 2시간
    }
    
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());
        
        // 리프레시 토큰 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
        
        log.info(" 사용자 로그인 성공: {}", user.getEmail());
        log.info(" 토큰 발급 완료 - accessToken 길이: {}, refreshToken 길이: {}", 
                accessToken.length(), refreshToken.length());
        
        // 로그인 성공 시 백그라운드에서 AI 추천 처리 시작
        try {
            backgroundAIService.processUserRecommendationAsync(user.getId());
            log.info(" 사용자 {}의 백그라운드 AI 추천 처리 시작", user.getEmail());
        } catch (Exception e) {
            log.warn("백그라운드 AI 추천 시작 실패: {}", e.getMessage());
        }
        
        return TokenResponse.of(accessToken, refreshToken, 7200L); // 2시간
    }
    
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 토큰에서 사용자 정보 추출
        String email = jwtUtil.extractEmail(refreshToken);
        Long userId = jwtUtil.extractUserId(refreshToken);
        
        // DB에서 리프레시 토큰 확인
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        
        // 새로운 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(email, userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(email, userId);
        
        // 새로운 리프레시 토큰 저장
        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);
        
        log.info("토큰 재발급: {}", email);
        
        return TokenResponse.of(newAccessToken, newRefreshToken, 7200L);
    }
    
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        user.clearRefreshToken();
        userRepository.save(user);
        
        log.info("사용자 로그아웃: {}", email);
    }
    
    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        return UserResponse.from(user);
    }
    
    @Transactional
    public TokenResponse extendedSignUp(ExtendedSignUpRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 사용자 생성 (확장 정보 포함)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .residenceSido(request.getResidenceSido())
                .residenceSigungu(request.getResidenceSigungu())
                .maritalStatus(request.getMaritalStatus())
                .numberOfChildren(request.getNumberOfChildren())
                .isPregnant(request.getIsPregnant())
                .expectedDueDate(request.getExpectedDueDate())
                .incomeLevel(request.getIncomeLevel())
                .hasDisability(request.getHasDisability())
                .isSingleParent(request.getIsSingleParent())
                .isMulticultural(request.getIsMulticultural())
                .interestCategories(request.getInterestCategories())
                .build();
        
        User savedUser = userRepository.save(user);
        
        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(savedUser.getEmail(), savedUser.getId());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail(), savedUser.getId());
        
        // 리프레시 토큰 저장
        savedUser.updateRefreshToken(refreshToken);
        userRepository.save(savedUser);
        
        log.info("확장 정보 포함 사용자 가입: {} (나이: {}, 거주지: {}, 자녀: {}명)", 
                savedUser.getEmail(), savedUser.getAge(), savedUser.getResidenceSido(), savedUser.getNumberOfChildren());
        
        return TokenResponse.of(accessToken, refreshToken, 7200L); // 2시간
    }
    
    @Transactional
    public UserResponse updateUserInfo(String email, ExtendedSignUpRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // OAuth2 사용자의 추가 정보 업데이트
        user.updatePersonalInfo(
                request.getBirthDate(),
                request.getGender(),
                request.getResidenceSido(),
                request.getResidenceSigungu(),
                request.getMaritalStatus(),
                request.getNumberOfChildren(),
                request.getIsPregnant(),
                request.getExpectedDueDate(),
                request.getIncomeLevel(),
                request.getHasDisability(),
                request.getIsSingleParent(),
                request.getIsMulticultural(),
                request.getInterestCategories()
        );
        
        // 전화번호가 있으면 업데이트
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            user.updateProfile(user.getName(), request.getPhoneNumber());
        }
        
        User savedUser = userRepository.save(user);
        
        log.info("사용자 정보 업데이트 완료: {}", savedUser.getEmail());
        
        return UserResponse.from(savedUser);
    }
    
    @Transactional
    public UserResponse updateOAuth2UserInfo(String email, OAuth2UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // OAuth2 사용자의 추가 정보 업데이트
        user.updatePersonalInfo(
                request.getBirthDate(),
                request.getGender(),
                request.getResidenceSido(),
                request.getResidenceSigungu(),
                request.getMaritalStatus(),
                request.getNumberOfChildren(),
                request.getIsPregnant(),
                request.getExpectedDueDate(),
                request.getIncomeLevel(),
                request.getHasDisability(),
                request.getIsSingleParent(),
                request.getIsMulticultural(),
                request.getInterestCategories()
        );
        
        // 이름과 전화번호 업데이트 (전화번호는 필수)
        String userName = request.getName() != null && !request.getName().isEmpty() ? 
                         request.getName() : user.getName();
        user.updateProfile(userName, request.getPhoneNumber());
        
        User savedUser = userRepository.save(user);
        
        log.info("OAuth2 사용자 정보 업데이트 완료: {}", savedUser.getEmail());
        
        return UserResponse.from(savedUser);
    }
    
    /**
     * 임시 OAuth2 토큰으로 신규 사용자 등록 및 추가 정보 저장
     */
    @Transactional
    public TokenResponse registerOAuth2UserWithAdditionalInfo(String tempToken, OAuth2UserUpdateRequest request) {
        // 임시 토큰 검증
        if (!jwtUtil.validateToken(tempToken) || !jwtUtil.isTempOAuth2Token(tempToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        
        // 임시 토큰에서 사용자 정보 추출
        String email = jwtUtil.extractEmail(tempToken);
        String name = jwtUtil.extractNameFromTempToken(tempToken);
        String provider = jwtUtil.extractProviderFromTempToken(tempToken);
        String providerId = jwtUtil.extractProviderIdFromTempToken(tempToken);
        
        // 이미 등록된 사용자인지 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        // 소셜 제공자 변환
        SocialProvider socialProvider = SocialProvider.valueOf(provider.toUpperCase());
        
        // 새 사용자 생성
        User user = User.builder()
                .email(email)
                .name(request.getName() != null ? request.getName() : name)
                .password("OAUTH2_USER")
                .provider(socialProvider)
                .providerId(providerId)
                .role(Role.USER)
                .build();
        
        // 추가 정보 설정
        user.updatePersonalInfo(
                request.getBirthDate(),
                request.getGender(),
                request.getResidenceSido(),
                request.getResidenceSigungu(),
                request.getMaritalStatus(),
                request.getNumberOfChildren(),
                request.getIsPregnant(),
                request.getExpectedDueDate(),
                request.getIncomeLevel(),
                request.getHasDisability(),
                request.getIsSingleParent(),
                request.getIsMulticultural(),
                request.getInterestCategories()
        );
        
        // 전화번호 설정 (OAuth2 사용자도 필수)
        user.updateProfile(user.getName(), request.getPhoneNumber());
        
        // 사용자 저장
        User savedUser = userRepository.save(user);
        
        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(savedUser.getEmail(), savedUser.getId());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail(), savedUser.getId());
        
        // 리프레시 토큰 저장
        savedUser.updateRefreshToken(refreshToken);
        userRepository.save(savedUser);
        
        log.info("OAuth2 신규 사용자 등록 완료: {} ({})", savedUser.getEmail(), provider);
        
        return TokenResponse.of(accessToken, refreshToken, 7200L);
    }
}
