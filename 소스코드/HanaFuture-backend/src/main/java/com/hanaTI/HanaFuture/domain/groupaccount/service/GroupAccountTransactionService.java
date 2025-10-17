package com.hanaTI.HanaFuture.domain.groupaccount.service;

import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountTransaction;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountRepository;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountTransactionRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.entity.UserBankAccount;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.user.repository.UserBankAccountRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GroupAccountTransactionService {

    private final GroupAccountRepository groupAccountRepository;
    private final GroupAccountTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final HanaBankMockAccountRepository mockAccountRepository;
    private final UserBankAccountRepository userBankAccountRepository;

    /**
     * 모임통장 입금 (채우기)
     */
    @Transactional
    public GroupAccountTransaction depositToGroupAccount(
            Long groupAccountId,
            Long userId,
            BigDecimal amount,
            String password,
            String sourceAccountNumber,
            String sourceBankName,
            String description) {

        log.info(" 모임통장 입금 요청 - 모임통장 ID: {}, 사용자 ID: {}, 금액: {}", 
                groupAccountId, userId, amount);

        // 유효성 검증
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(ErrorCode.INVALID_TRANSACTION_AMOUNT);
        }

        // 엔티티 조회
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_ACCOUNT_NOT_FOUND));
        
        // 비밀번호 검증
        if (!groupAccount.getPassword().equals(password)) {
            log.warn("모임통장 비밀번호 불일치 - 모임통장 ID: {}", groupAccountId);
            throw new CustomException(ErrorCode.INVALID_GROUP_ACCOUNT_PASSWORD);
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 잔액 조회
        BigDecimal currentBalance = getCurrentBalance(groupAccount);
        BigDecimal newBalance = currentBalance.add(amount);

        // 거래내역 생성
        GroupAccountTransaction transaction = GroupAccountTransaction.createDeposit(
                groupAccount, user, amount, newBalance, 
                sourceAccountNumber, sourceBankName, description);

        // 저장
        GroupAccountTransaction savedTransaction = transactionRepository.save(transaction);

        // 모임통장 잔액 업데이트
        updateGroupAccountBalance(groupAccount, newBalance);
        
        // 출금 계좌(Mock 계좌) 잔액 차감
        updateSourceAccountBalance(sourceAccountNumber, amount, false);

        log.info("모임통장 입금 완료 - 거래 ID: {}, 새 잔액: {}, 출금 계좌: {}", 
                savedTransaction.getId(), newBalance, sourceAccountNumber);

        // User 프록시 초기화 (Lazy Loading 에러 방지)
        if (savedTransaction.getUser() != null) {
            savedTransaction.getUser().getName();
        }

        return savedTransaction;
    }

    /**
     * 모임통장 출금 (보내기)
     */
    @Transactional
    public GroupAccountTransaction withdrawFromGroupAccount(
            Long groupAccountId,
            Long userId,
            BigDecimal amount,
            String password,
            String targetAccountNumber,
            String targetBankName,
            String description) {

        log.info(" 모임통장 출금 요청 - 모임통장 ID: {}, 사용자 ID: {}, 금액: {}", 
                groupAccountId, userId, amount);

        // 유효성 검증
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(ErrorCode.INVALID_TRANSACTION_AMOUNT);
        }

        // 엔티티 조회
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_ACCOUNT_NOT_FOUND));
        
        // 비밀번호 검증
        if (!groupAccount.getPassword().equals(password)) {
            log.warn("모임통장 비밀번호 불일치 - 모임통장 ID: {}", groupAccountId);
            throw new CustomException(ErrorCode.INVALID_GROUP_ACCOUNT_PASSWORD);
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 잔액 조회 및 잔액 부족 검증
        BigDecimal currentBalance = getCurrentBalance(groupAccount);
        if (currentBalance.compareTo(amount) < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        BigDecimal newBalance = currentBalance.subtract(amount);

        // 거래내역 생성
        GroupAccountTransaction transaction = GroupAccountTransaction.createWithdrawal(
                groupAccount, user, amount, newBalance, 
                targetAccountNumber, targetBankName, description);

        // 저장
        GroupAccountTransaction savedTransaction = transactionRepository.save(transaction);

        // 모임통장 잔액 업데이트
        updateGroupAccountBalance(groupAccount, newBalance);
        
        // 입금 계좌 잔액 증가
        updateSourceAccountBalance(targetAccountNumber, amount, true);

        log.info("모임통장 출금 완료 - 거래 ID: {}, 새 잔액: {}, 입금 계좌: {}", 
                savedTransaction.getId(), newBalance, targetAccountNumber);

        // User 프록시 초기화 (Lazy Loading 에러 방지)
        if (savedTransaction.getUser() != null) {
            savedTransaction.getUser().getName();
        }

        return savedTransaction;
    }

    /**
     * 거래내역 조회
     */
    public Page<GroupAccountTransaction> getTransactionHistory(
            Long groupAccountId, Pageable pageable) {
        
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_ACCOUNT_NOT_FOUND));

        return transactionRepository.findByGroupAccountOrderByTransactionDateDesc(
                groupAccount, pageable);
    }

    /**
     * 특정 사용자의 거래내역 조회
     */
    public Page<GroupAccountTransaction> getUserTransactionHistory(
            Long groupAccountId, Long userId, Pageable pageable) {
        
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_ACCOUNT_NOT_FOUND));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return transactionRepository.findByGroupAccountAndUserOrderByTransactionDateDesc(
                groupAccount, user, pageable);
    }

    /**
     * 거래 통계 조회
     */
    public TransactionStats getTransactionStats(Long groupAccountId) {
        GroupAccount groupAccount = groupAccountRepository.findById(groupAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_ACCOUNT_NOT_FOUND));

        BigDecimal totalDeposits = transactionRepository.getTotalDeposits(groupAccount);
        BigDecimal totalWithdrawals = transactionRepository.getTotalWithdrawals(groupAccount);
        BigDecimal currentBalance = getCurrentBalance(groupAccount);

        return TransactionStats.builder()
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .currentBalance(currentBalance)
                .netAmount(totalDeposits.subtract(totalWithdrawals))
                .build();
    }

    /**
     * 현재 잔액 조회
     */
    private BigDecimal getCurrentBalance(GroupAccount groupAccount) {
        BigDecimal balance = transactionRepository.getCurrentBalance(groupAccount);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * 모임통장 잔액 업데이트
     */
    @Transactional
    private void updateGroupAccountBalance(GroupAccount groupAccount, BigDecimal newBalance) {
        BigDecimal oldBalance = groupAccount.getCurrentBalance();
        groupAccount.setCurrentBalance(newBalance);
        groupAccount.setLastBalanceUpdate(LocalDateTime.now());
        GroupAccount savedAccount = groupAccountRepository.save(groupAccount);
        
        log.info(" 모임통장 잔액 업데이트 완료 - 계좌: {}, 이전 잔액: {}, 새 잔액: {}, DB 저장된 잔액: {}", 
                groupAccount.getGroupAccountNumber(), 
                oldBalance,
                newBalance,
                savedAccount.getCurrentBalance());
    }

    /**
     *  계좌 및 사용자 연결 계좌 잔액 업데이트
     * @param accountNumber 계좌번호
     * @param amount 금액
     * @param isDeposit true면 증가, false면 차감
     */
    @Transactional
    private void updateSourceAccountBalance(String accountNumber, BigDecimal amount, boolean isDeposit) {
        try {
            HanaBankMockAccount mockAccount = mockAccountRepository.findByAccountNumAndIsActiveTrue(accountNumber)
                    .orElse(null);
            
            if (mockAccount != null) {
                BigDecimal currentBalance = mockAccount.getBalanceAmt();
                BigDecimal newBalance = isDeposit ? 
                        currentBalance.add(amount) : 
                        currentBalance.subtract(amount);
                
                mockAccount.updateBalance(newBalance);
                mockAccountRepository.save(mockAccount);
                
                log.info(" Mock 계좌 잔액 업데이트 - 계좌: {}, {} 금액: {}, 새 잔액: {}", 
                        accountNumber, 
                        isDeposit ? "입금" : "출금",
                        amount, 
                        newBalance);
                
                // 사용자 연결 계좌 잔액 업데이트 (오픈뱅킹 연동 계좌)
                UserBankAccount userBankAccount = userBankAccountRepository.findByAccountNumber(accountNumber)
                        .orElse(null);
                
                if (userBankAccount != null) {
                    userBankAccount.updateBalance(newBalance);
                    userBankAccountRepository.save(userBankAccount);
                    
                    log.info("사용자 연결 계좌 잔액 업데이트 완료 - 사용자: {}, 계좌: {}, 새 잔액: {}", 
                            userBankAccount.getUser().getName(),
                            accountNumber,
                            newBalance);
                } else {
                    log.debug("ℹ 연결된 사용자 계좌 없음 (아직 등록 전) - 계좌번호: {}", accountNumber);
                }
            } else {
                log.warn("Mock 계좌를 찾을 수 없음 - 계좌번호: {}", accountNumber);
            }
        } catch (Exception e) {
            log.error("계좌 잔액 업데이트 실패 - 계좌: {}, 에러: {}", accountNumber, e.getMessage());
            // 실패해도 거래는 진행 (계좌 업데이트는 부가적인 기능)
        }
    }

    /**
     * 거래 통계 DTO
     */
    @lombok.Builder
    @lombok.Getter
    public static class TransactionStats {
        private final BigDecimal totalDeposits;
        private final BigDecimal totalWithdrawals;
        private final BigDecimal currentBalance;
        private final BigDecimal netAmount;
    }
}
