package com.hanaTI.HanaFuture.domain.ai.service;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 백그라운드 AI 추천 처리 서비스
 * - 로그인 시 비동기로 사용자 정보 확인
 * - 실제 AI 추천은 실시간 요청 시 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundAIRecommendationService {

    private final UserRepository userRepository;

    /**
     * 로그인 시 사용자 정보 확인 (비동기)
     * 실제 AI 추천은 사용자가 요청할 때 실시간으로 처리
     */
    @Async("aiRecommendationExecutor")
    public void processUserRecommendationAsync(Long userId) {
        try {
            log.info(" 사용자 {}의 백그라운드 처리 시작", userId);
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("사용자를 찾을 수 없음: {}", userId);
                return;
            }

            // 사용자 정보 확인 완료
            log.info("사용자 {}({})의 백그라운드 처리 완료", userId, user.getName());
            
        } catch (Exception e) {
            log.error("사용자 {}의 백그라운드 처리 실패: {}", userId, e.getMessage(), e);
        }
    }
}
