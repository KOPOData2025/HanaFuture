package com.hanaTI.HanaFuture.domain.savings.service;

import com.hanaTI.HanaFuture.domain.savings.dto.SavingGoalRequest;
import com.hanaTI.HanaFuture.domain.savings.dto.SavingGoalResponse;
import com.hanaTI.HanaFuture.domain.savings.dto.SavingDepositRequest;
import com.hanaTI.HanaFuture.domain.savings.entity.SavingGoal;
import com.hanaTI.HanaFuture.domain.savings.entity.SavingDeposit;
import com.hanaTI.HanaFuture.domain.savings.entity.SavingStatus;
import com.hanaTI.HanaFuture.domain.savings.entity.SavingCategory;
import com.hanaTI.HanaFuture.domain.savings.repository.SavingGoalRepository;
import com.hanaTI.HanaFuture.domain.savings.repository.SavingDepositRepository;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingsService {
    
    private final SavingGoalRepository savingGoalRepository;
    private final SavingDepositRepository savingDepositRepository;
    private final UserRepository userRepository;
    private final GroupAccountRepository groupAccountRepository;
    
    @Transactional
    public SavingGoalResponse createSavingGoal(SavingGoalRequest request) {
        log.info(" 적금 목표 생성 시작: 사용자 ID {}, 목표명 {}", 
                request.getUserId(), request.getGoalName());
        
        try {
            // 1. 사용자 확인
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            
            log.info("사용자 확인 완료: ID {}, 이름 {}", user.getId(), user.getName());
            
            // 2. 새로운 하나은행 적금 계좌번호 생성
            String savingsAccountNumber = generateSavingsAccountNumber();
            
            log.info(" 새 적금 계좌번호 생성: {}", savingsAccountNumber);
            
            // 3. 적금 목표 엔티티 생성
            SavingGoal savingGoal = SavingGoal.builder()
                    .goalName(request.getGoalName())
                    .description(request.getDescription())
                    .targetAmount(BigDecimal.valueOf(request.getTargetAmount()))
                    .monthlyTarget(BigDecimal.valueOf(request.getMonthlyTarget()))
                    .category(SavingCategory.valueOf(request.getCategory()))
                    .startDate(LocalDate.parse(request.getStartDate()))
                    .endDate(LocalDate.parse(request.getEndDate()))
                    .interestRate(BigDecimal.valueOf(request.getInterestRate()))
                    .savingsAccountNumber(savingsAccountNumber) // 새 적금 계좌번호
                    .bankCode("081") // 하나은행
                    .bankName("하나은행")
                    .sourceType(request.getSourceType()) // "PERSONAL_ACCOUNT" or "GROUP_ACCOUNT"
                    .sourceAccountId(request.getSourceAccountId()) // 출금 계좌 ID
                    .status(SavingStatus.ACTIVE)
                    .creator(user)
                    .build();
            
            log.info(" 적금 엔티티 생성 완료: 목표명={}, 목표금액={}, 출금계좌={}", 
                    savingGoal.getGoalName(), savingGoal.getTargetAmount(), savingGoal.getSourceAccountId());
            
            // 4. 데이터베이스 저장
            SavingGoal savedGoal = savingGoalRepository.save(savingGoal);
            
            log.info(" 데이터베이스 저장 완료: ID {}, 계좌번호 {}", 
                    savedGoal.getId(), savedGoal.getSavingsAccountNumber());
            
            // 5. 자동이체 설정
            setupAutoTransferForSavings(savedGoal, request);
            
            log.info(" 적금 목표 생성 완료: ID {}, 계좌번호 {}", 
                    savedGoal.getId(), savedGoal.getSavingsAccountNumber());
            
            // 6. 응답 DTO 생성
            return SavingGoalResponse.builder()
                    .id(savedGoal.getId())
                    .goalName(savedGoal.getGoalName())
                    .description(savedGoal.getDescription())
                    .targetAmount(savedGoal.getTargetAmount())
                    .currentAmount(savedGoal.getCurrentAmount())
                    .monthlyTarget(savedGoal.getMonthlyTarget())
                    .category(savedGoal.getCategory().name())
                    .savingsAccountNumber(savedGoal.getSavingsAccountNumber())
                    .interestRate(savedGoal.getInterestRate())
                    .status(savedGoal.getStatus().toString())
                    .createdAt(savedGoal.getCreatedAt())
                    .build();
                    
        } catch (Exception e) {
            log.error("적금 목표 생성 실패: 사용자 ID {}, 오류: {}", 
                    request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }
    
    public List<SavingGoalResponse> getUserSavingGoals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        List<SavingGoal> goals = savingGoalRepository.findByCreatorOrderByCreatedAtDesc(user);
        
        return goals.stream()
                .map(goal -> SavingGoalResponse.builder()
                        .id(goal.getId())
                        .goalName(goal.getGoalName())
                        .description(goal.getDescription())
                        .targetAmount(goal.getTargetAmount())
                        .currentAmount(goal.getCurrentAmount())
                        .monthlyTarget(goal.getMonthlyTarget())
                        .category(goal.getCategory().name())
                        .savingsAccountNumber(goal.getSavingsAccountNumber())
                        .interestRate(goal.getInterestRate())
                        .status(goal.getStatus().toString())
                        .createdAt(goal.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void depositToSavingGoal(Long goalId, SavingDepositRequest request) {
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        BigDecimal depositAmount = BigDecimal.valueOf(request.getAmount());
        BigDecimal newBalance = goal.getCurrentAmount().add(depositAmount);
        
        // 적금 잔액 업데이트
        goal.updateCurrentAmount(newBalance);
        
        // 납입 내역 저장
        SavingDeposit deposit = SavingDeposit.createFreeDeposit(
                goal, user, depositAmount, newBalance, request.getDescription());
        
        savingDepositRepository.save(deposit);
        
        log.info("적금 납입 완료: 목표 ID {}, 납입액 {}, 새 잔액 {}", 
                goalId, depositAmount, newBalance);
    }
    
    @Transactional
    public void setupAutoTransfer(Long goalId, String sourceType, String sourceAccountId, Integer transferDay) {
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        log.info("적금 자동이체 설정: 목표 ID {}, 출금 유형 {}, 계좌 {}", 
                goalId, sourceType, sourceAccountId);
        
        // 출금 계좌 유형에 따른 처리
        if ("GROUP_ACCOUNT".equals(sourceType)) {
            // 모임통장에서 출금
            GroupAccount groupAccount = groupAccountRepository.findById(Long.valueOf(sourceAccountId))
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
            
            log.info("모임통장에서 적금으로 자동이체 설정: {} -> {}", 
                    groupAccount.getGroupAccountNumber(), goal.getSavingsAccountNumber());
                    
        } else if ("PERSONAL_ACCOUNT".equals(sourceType)) {
            // 개인 계좌에서 출금
            log.info("개인 계좌에서 적금으로 자동이체 설정: {} -> {}", 
                    sourceAccountId, goal.getSavingsAccountNumber());
        }
        
        // 자동이체 설정 정보 업데이트
        goal.updateAutoTransferSettings(sourceType, sourceAccountId, transferDay);
        
        // TODO: 실제 은행 API 연동하여 자동이체 설정
        // 1. 하나은행 API 호출
        // 2. 출금 계좌에서 적금 계좌로 매월 자동이체 등록
        // 3. 이체일, 이체금액 설정
        
        log.info("적금 자동이체 설정 완료 (Mock)");
    }
    
    /**
     * 새로운 적금 계좌번호 생성
     * 하나은행 적금 계좌: 819(적금)-XXX-XXXXXX
     */
    private String generateSavingsAccountNumber() {
        String prefix = "819"; // 하나은행 적금 코드
        
        // 중간 3자리: 201~999 범위의 랜덤 숫자
        int middle = 201 + new java.util.Random().nextInt(799);
        
        // 마지막 6자리: 100000~999999 범위의 랜덤 숫자
        int suffix = 100000 + new java.util.Random().nextInt(900000);
        
        String accountNumber = String.format("%s-%03d-%06d", prefix, middle, suffix);
        
        // 중복 확인 (실제로는 DB 체크 필요)
        if (savingGoalRepository.findBySavingsAccountNumber(accountNumber).isPresent()) {
            return generateSavingsAccountNumber(); // 재귀 호출로 중복 해결
        }
        
        return accountNumber;
    }
    
    /**
     * 적금 자동이체 설정
     */
    private void setupAutoTransferForSavings(SavingGoal goal, SavingGoalRequest request) {
        log.info("적금 자동이체 초기 설정: {}", goal.getSavingsAccountNumber());
        
        // TODO: 실제 하나은행 API 연동
        // 1. 출금 계좌 확인
        // 2. 적금 계좌로 자동이체 등록
        // 3. 매월 납입일 설정
        
        log.info("적금 자동이체 초기 설정 완료 (Mock)");
    }
}
