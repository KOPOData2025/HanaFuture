package com.hanaTI.HanaFuture.global.config;

import com.hanaTI.HanaFuture.domain.user.entity.*;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.child.entity.*;
import com.hanaTI.HanaFuture.domain.child.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("AI 추천 테스트용 사용자 정보 초기화 시작");
        
        updateLeeHanaUserInfo();
        addChildInfo();
        
        log.info("사용자 정보 초기화 완료");
    }

    private void updateLeeHanaUserInfo() {
        String email = "leehana@naver.com";
        
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("password1234"))
                    .name("이하나")
                    .phoneNumber("010-5506-7328")
                    .build();
        }
        
        user.updatePersonalInfo(
                "이하나",
                "010-5506-7328",
                LocalDateTime.of(1994, 3, 15, 0, 0),
                Gender.FEMALE,
                "서울특별시",
                "금천구",
                4500000L,
                EmploymentStatus.EMPLOYED,
                MaritalStatus.MARRIED,
                true,
                1,
                null,
                java.util.Arrays.asList("육아", "교육", "보육", "출산")
        );
        
        user.updatePersonalInfo(
                user.getBirthDate(),
                user.getGender(),
                user.getResidenceSido(),
                user.getResidenceSigungu(),
                user.getMaritalStatus(),
                user.getNumberOfChildren(),
                false,
                null,
                IncomeLevel.MIDDLE,
                false,
                false,
                false,
                "육아,교육,보육,출산"
        );
        
        userRepository.save(user);
        log.info("사용자 정보 업데이트 완료: {} ({}세, {}, 자녀 {}명)", 
                user.getName(), user.getAge(), user.getMaritalStatus(), user.getNumberOfChildren());
    }

    private void addChildInfo() {
        String parentEmail = "leehana@naver.com";
        User parent = userRepository.findByEmail(parentEmail).orElse(null);
        
        if (parent == null) {
            log.warn("부모 사용자를 찾을 수 없음: {}", parentEmail);
            return;
        }
        
        if (!childRepository.findByParentUserOrderByCreatedAtDesc(parent).isEmpty()) {
            log.info("자녀 정보가 이미 존재함");
            return;
        }
        
        Child child = Child.builder()
                .name("이지우")
                .birthDate(LocalDate.of(2018, 9, 12))
                .gender(ChildGender.MALE)
                .schoolName("하나유치원")
                .grade(0)
                .classNumber(1)
                .parentUser(parent)
                .nickname("지우")
                .weeklyAllowance(BigDecimal.valueOf(10000))
                .monthlyAllowance(BigDecimal.valueOf(40000))
                .allowanceCycle(AllowanceCycle.WEEKLY)
                .status(ChildStatus.ACTIVE)
                .financialEducationLevel(1)
                .rewardPoints(BigDecimal.valueOf(5000))
                .build();
        
        childRepository.save(child);
        log.info("자녀 정보 추가 완료: {} ({}세, {})", 
                child.getName(), child.getAge(), child.getSchoolName());
    }
}
