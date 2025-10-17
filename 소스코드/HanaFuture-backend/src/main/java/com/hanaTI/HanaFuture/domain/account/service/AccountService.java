package com.hanaTI.HanaFuture.domain.account.service;

import com.hanaTI.HanaFuture.domain.account.dto.*;
import com.hanaTI.HanaFuture.domain.account.entity.Account;
import com.hanaTI.HanaFuture.domain.account.repository.AccountRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
// import com.hanaTI.HanaFuture.domain.banking.service.HanaBankApiClient;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    
    /**
     * 사용자의 계좌 요약 정보 조회
     */
    public AccountSummaryResponse getAccountSummary(String email) {
        User user = getUserByEmail(email);
        
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrue(user.getId());
        BigDecimal totalBalance = accountRepository.getTotalBalanceByUserId(user.getId());
        Long accountCount = accountRepository.countByUserIdAndIsActiveTrue(user.getId());
        
        List<AccountResponse> accountResponses = accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
        
        return AccountSummaryResponse.builder()
                .totalBalance(totalBalance)
                .accountCount(accountCount)
                .accounts(accountResponses)
                .build();
    }
    
    /**
     * 사용자의 모든 계좌 조회
     */
    public List<AccountResponse> getUserAccounts(String email) {
        User user = getUserByEmail(email);
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrue(user.getId());
        
        return accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 계좌 상세 조회
     */
    public AccountResponse getAccount(String email, Long accountId) {
        User user = getUserByEmail(email);
        Account account = getAccountByIdAndUser(accountId, user);
        
        return AccountResponse.from(account);
    }
    
    /**
     * 계좌 생성
     */
    @Transactional
    public AccountResponse createAccount(String email, CreateAccountRequest request) {
        User user = getUserByEmail(email);
        
        // 계좌번호 생성 (실제로는 은행 시스템과 연동해야 함)
        String accountNumber = generateAccountNumber();
        
        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountName(request.getAccountName())
                .accountType(request.getAccountType())
                .bankName(request.getBankName())
                .description(request.getDescription())
                .balance(BigDecimal.ZERO)
                .isActive(true)
                .build();
        
        Account savedAccount = accountRepository.save(account);
        log.info("새 계좌 생성: {} - {}", savedAccount.getAccountNumber(), savedAccount.getAccountName());
        
        return AccountResponse.from(savedAccount);
    }

    
    /**
     * 계좌 정보 수정
     */
    @Transactional
    public AccountResponse updateAccount(String email, Long accountId, CreateAccountRequest request) {
        User user = getUserByEmail(email);
        Account account = getAccountByIdAndUser(accountId, user);
        
        account.updateAccountInfo(request.getAccountName(), request.getDescription());
        
        log.info("계좌 정보 수정: {} - {}", account.getAccountNumber(), account.getAccountName());
        
        return AccountResponse.from(account);
    }
    
    /**
     * 계좌 비활성화
     */
    @Transactional
    public void deactivateAccount(String email, Long accountId) {
        User user = getUserByEmail(email);
        Account account = getAccountByIdAndUser(accountId, user);
        
        // 잔액이 있는 계좌는 비활성화할 수 없음
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new CustomException(ErrorCode.ACCOUNT_HAS_BALANCE);
        }
        
        account.deactivate();
        log.info("계좌 비활성화: {} - {}", account.getAccountNumber(), account.getAccountName());
    }
    
    // === Private Methods ===
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
    
    private Account getAccountByIdAndUser(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
        
        if (!account.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        
        return account;
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        String middle = String.format("%03d", random.nextInt(1000));
        String last = String.format("%06d", random.nextInt(1000000));
        String accountNumber = "110-" + middle + "-" + last;
        
        // 중복 확인
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            middle = String.format("%03d", random.nextInt(1000));
            last = String.format("%06d", random.nextInt(1000000));
            accountNumber = "110-" + middle + "-" + last;
        }
        
        return accountNumber;
    }
}

