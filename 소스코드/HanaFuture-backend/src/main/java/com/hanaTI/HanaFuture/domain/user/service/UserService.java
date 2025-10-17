package com.hanaTI.HanaFuture.domain.user.service;

import com.hanaTI.HanaFuture.domain.user.dto.UserInfoResponse;
import com.hanaTI.HanaFuture.domain.user.dto.UserInfoUpdateRequest;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 정보 조회
     */
    public UserInfoResponse getUserInfo(User user) {
        return UserInfoResponse.from(user);
    }

    /**
     * 사용자 정보 업데이트
     */
    @Transactional
    public UserInfoResponse updateUserInfo(User user, UserInfoUpdateRequest request) {
        // 사용자 정보 업데이트
        user.updatePersonalInfo(
            request.getName(),
            request.getPhoneNumber(),
            request.getBirthDate(),
            request.getGender(),
            request.getResidenceSido(),
            request.getResidenceSigungu(),
            request.getIncome(),
            request.getEmploymentStatus(),
            request.getMaritalStatus(),
            request.getHasChildren(),
            request.getNumberOfChildren(),
            request.getChildrenAges(),
            request.getInterests()
        );

        User savedUser = userRepository.save(user);
        
        log.info("사용자 정보 업데이트 완료: {}", user.getEmail());
        
        return UserInfoResponse.from(savedUser);
    }
}