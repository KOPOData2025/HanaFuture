package com.hanaTI.HanaFuture.global.config;

import com.hanaTI.HanaFuture.domain.user.entity.Gender;
import com.hanaTI.HanaFuture.domain.user.entity.IncomeLevel;
import com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@Order(2) // SQL 초기화 후 실행
@RequiredArgsConstructor
public class TestUserInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createOrUpdateChulwooTestUser();
        
        if (!userRepository.existsByEmail("test@naver.com")) {
            createBasicTestUser();
            log.info("기본 테스트 사용자가 생성되었습니다: test@naver.com / password123");
        } else {
            log.info("기본 테스트 사용자가 이미 존재합니다: test@naver.com");
        }
    }
    
    private void createOrUpdateChulwooTestUser() {
        User user = userRepository.findByEmail("chulwoo@naver.com").orElse(null);
        
        if (user == null) {
            user = User.builder()
                    .email("chulwoo@naver.com")
                    .password(passwordEncoder.encode("password123"))
                    .name("이철우")
                    .phoneNumber("010-3614-7328")
                    .userCi("CI3003HANAFUTURE2024CHULWOO")
                    .birthDate(LocalDateTime.of(1998, 5, 14, 0, 0))
                    .gender(Gender.MALE)
                    .residenceSido("서울특별시")
                    .residenceSigungu("금천구")
                    .maritalStatus(MaritalStatus.SINGLE)
                    .numberOfChildren(0)
                    .isPregnant(false)
                    .incomeLevel(IncomeLevel.MIDDLE)
                    .hasDisability(false)
                    .isSingleParent(false)
                    .isMulticultural(false)
                    .interestCategories("육아,교육,의료,주거")
                    .build();
            log.info("이철우 테스트 사용자 생성 완료");
        } else {
            user.updatePersonalInfo(
                    "이철우",
                    "010-3614-7328",
                    LocalDateTime.of(1998, 5, 14, 0, 0),
                    Gender.MALE,
                    "서울특별시",
                    "금천구",
                    5000000L,
                    com.hanaTI.HanaFuture.domain.user.entity.EmploymentStatus.EMPLOYED,
                    MaritalStatus.SINGLE,
                    false,
                    0,
                    null,
                    java.util.Arrays.asList("육아,교육,의료,주거")
            );
            log.info("이철우 테스트 사용자 업데이트 완료");
        }
        
        userRepository.save(user);
    }
    
    private void createBasicTestUser() {
        User testUser = User.builder()
                .email("test@naver.com")
                .password(passwordEncoder.encode("password123"))
                .name("테스트0")
                .phoneNumber("010-1234-5678")
                // 복지 혜택 맞춤 추천을 위한 정보
                .birthDate(LocalDateTime.of(1990, 1, 1, 0, 0))
                .gender(Gender.MALE)
                .residenceSido("서울특별시")
                .residenceSigungu("구로구")
                .maritalStatus(MaritalStatus.MARRIED)
                .numberOfChildren(1)
                .isPregnant(false)
                .incomeLevel(IncomeLevel.MIDDLE)
                .hasDisability(false)
                .isSingleParent(false)
                .isMulticultural(false)
                .interestCategories("육아,교육,의료,주거")
                .build();
        
        userRepository.save(testUser);
        log.info("기본 테스트 사용자 생성 완료 - ID: {}, 이메일: {}", testUser.getId(), testUser.getEmail());
    }
}