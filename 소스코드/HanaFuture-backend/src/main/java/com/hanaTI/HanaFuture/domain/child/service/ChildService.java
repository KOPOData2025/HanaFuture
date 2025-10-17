package com.hanaTI.HanaFuture.domain.child.service;

import com.hanaTI.HanaFuture.domain.child.dto.ChildRegistrationRequest;
import com.hanaTI.HanaFuture.domain.child.dto.ChildResponse;
import com.hanaTI.HanaFuture.domain.child.entity.Child;
import com.hanaTI.HanaFuture.domain.child.entity.ChildStatus;
import com.hanaTI.HanaFuture.domain.child.repository.ChildRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildService {
    
    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ChildResponse registerChild(ChildRegistrationRequest request) {
        log.info("자녀 등록 시작: 이름 {}, 부모 ID {}", 
                request.getName(), request.getParentUserId());
        
        // 1. 부모 사용자 확인
        User parentUser = userRepository.findById(request.getParentUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 2. 자녀 엔티티 생성
        Child child = Child.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .schoolName(request.getSchoolName())
                .grade(request.getGrade())
                .classNumber(request.getClassNumber())
                .parentUser(parentUser)
                .profileImageUrl(request.getProfileImageUrl())
                .nickname(request.getNickname())
                .weeklyAllowance(request.getWeeklyAllowance())
                .monthlyAllowance(request.getMonthlyAllowance())
                .allowanceCycle(request.getAllowanceCycle())
                .status(ChildStatus.ACTIVE)
                .build();
        
        // 3. 데이터베이스 저장
        Child savedChild = childRepository.save(child);
        
        log.info("자녀 등록 완료: ID {}, 이름 {}", 
                savedChild.getId(), savedChild.getName());
        
        // 4. 응답 DTO 생성
        return convertToResponse(savedChild);
    }
    
    public List<ChildResponse> getParentChildren(Long parentUserId) {
        log.info("부모의 자녀 목록 조회: 부모 ID {}", parentUserId);
        
        // 1. 부모 사용자 확인
        User parentUser = userRepository.findById(parentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 2. 자녀 목록 조회
        List<Child> children = childRepository.findByParentUserAndStatusOrderByCreatedAtDesc(
                parentUser, ChildStatus.ACTIVE);
        
        log.info("자녀 목록 조회 완료: {}명", children.size());
        
        // 3. 응답 DTO 변환
        return children.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ChildResponse updateChild(Long childId, ChildRegistrationRequest request) {
        log.info("자녀 정보 수정: ID {}, 이름 {}", childId, request.getName());
        
        // 1. 자녀 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 정보 업데이트
        child.updateProfile(
                request.getNickname(),
                request.getProfileImageUrl(),
                request.getSchoolName(),
                request.getGrade(),
                request.getClassNumber()
        );
        
        // 3. 용돈 설정 업데이트
        if (request.getWeeklyAllowance() != null || request.getMonthlyAllowance() != null) {
            child.updateAllowanceSettings(
                    request.getWeeklyAllowance(),
                    request.getMonthlyAllowance(),
                    request.getAllowanceCycle()
            );
        }
        
        log.info("자녀 정보 수정 완료: ID {}", childId);
        
        // 4. 응답 DTO 생성
        return convertToResponse(child);
    }
    
    @Transactional
    public void setAllowanceSettings(Long childId, java.util.Map<String, Object> request) {
        log.info("자녀 용돈 설정: ID {}", childId);
        
        // 1. 자녀 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 용돈 설정 추출
        java.math.BigDecimal weeklyAllowance = null;
        java.math.BigDecimal monthlyAllowance = null;
        com.hanaTI.HanaFuture.domain.child.entity.AllowanceCycle cycle = 
                com.hanaTI.HanaFuture.domain.child.entity.AllowanceCycle.WEEKLY;
        
        if (request.containsKey("weeklyAllowance")) {
            weeklyAllowance = new java.math.BigDecimal(request.get("weeklyAllowance").toString());
        }
        
        if (request.containsKey("monthlyAllowance")) {
            monthlyAllowance = new java.math.BigDecimal(request.get("monthlyAllowance").toString());
        }
        
        if (request.containsKey("allowanceCycle")) {
            cycle = com.hanaTI.HanaFuture.domain.child.entity.AllowanceCycle.valueOf(
                    request.get("allowanceCycle").toString());
        }
        
        // 3. 용돈 설정 업데이트
        child.updateAllowanceSettings(weeklyAllowance, monthlyAllowance, cycle);
        
        log.info("자녀 용돈 설정 완료: ID {}, 주기 {}", childId, cycle);
    }
    
    /**
     * Child 엔티티를 ChildResponse DTO로 변환
     */
    private ChildResponse convertToResponse(Child child) {
        return ChildResponse.builder()
                .id(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .age(child.getAge())
                .gender(child.getGender())
                .schoolName(child.getSchoolName())
                .grade(child.getGrade())
                .classNumber(child.getClassNumber())
                .classInfo(child.getClassInfo())
                .parentUserId(child.getParentUser().getId())
                .profileImageUrl(child.getProfileImageUrl())
                .nickname(child.getNickname())
                .weeklyAllowance(child.getWeeklyAllowance())
                .monthlyAllowance(child.getMonthlyAllowance())
                .allowanceCycle(child.getAllowanceCycle())
                .status(child.getStatus())
                .financialEducationLevel(child.getFinancialEducationLevel())
                .rewardPoints(child.getRewardPoints())
                .createdAt(child.getCreatedAt())
                .updatedAt(child.getUpdatedAt())
                .build();
    }
}










