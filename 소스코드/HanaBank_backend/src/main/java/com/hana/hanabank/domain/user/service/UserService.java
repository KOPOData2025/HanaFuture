package com.hana.hanabank.domain.user.service;

import com.hana.hanabank.domain.user.entity.User;
import com.hana.hanabank.domain.user.repository.UserRepository;
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
     * 테스트용 사용자 생성
     */
    @Transactional
    public User createTestUser(String userId, String userCi, String userNum, 
                              String username, String phoneNumber, String email) {
        
        // 이미 존재하는 사용자인지 확인
        if (userRepository.existsByUserCi(userCi)) {
            log.warn("이미 존재하는 사용자: CI={}", userCi);
            return userRepository.findByUserCi(userCi).orElse(null);
        }
        
        User user = User.builder()
                .userId(userId)
                .userCi(userCi)
                .userNum(userNum)
                .username(username)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * CI로 사용자 조회
     */
    public User getUserByCi(String userCi) {
        return userRepository.findByUserCi(userCi).orElse(null);
    }
    
    /**
     * 사용자 존재 여부 확인
     */
    public boolean existsByUserCi(String userCi) {
        return userRepository.existsByUserCi(userCi);
    }
}














