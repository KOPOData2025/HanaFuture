package com.hanaTI.HanaFuture.domain.hanabank.service;

import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HanaBankService {

    private final HanaBankMockAccountRepository hanaBankMockAccountRepository;

    public Map<String, Object> getUserAccountsByName(String userName) {
        try {

            List<HanaBankMockAccount> accounts = hanaBankMockAccountRepository
                    .findByUserNameAndIsActiveTrue(userName);
            
            // 응답 데이터 구성
            List<Map<String, Object>> accountList = accounts.stream()
                    .map(account -> {
                        Map<String, Object> accountData = new HashMap<>();
                        accountData.put("accountNum", account.getAccountNum());
                        accountData.put("bankName", account.getBankName());
                        accountData.put("bankCode", account.getBankCode());
                        accountData.put("productName", account.getProductName());
                        accountData.put("balanceAmt", account.getBalanceAmt());
                        accountData.put("accountType", account.getAccountType());
                        accountData.put("fintechUseNum", account.getFintechUseNum());
                        accountData.put("isActive", account.getIsActive());
                        return accountData;
                    })
                    .collect(Collectors.toList());
            
            log.info("조회된 계좌 수: {}", accountList.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "계좌 조회 성공");
            
            Map<String, Object> data = new HashMap<>();
            data.put("accounts", accountList);
            data.put("totalCount", accountList.size());
            
            response.put("data", data);
            
            return response;
            
        } catch (Exception e) {
            log.error("이름으로 계좌 조회 실패 - userName: {}", userName, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "계좌 조회 실패: " + e.getMessage());
            errorResponse.put("data", new ArrayList<>());
            return errorResponse;
        }
    }

    /**
     * 내부 데이터베이스에서 하나은행 계좌 조회
     */
    public Map<String, Object> getUserAccounts(String userCi, String userName, String userNum) {
        try {
            log.info("내부 DB에서 하나은행 계좌 조회 - userCi: {}, userName: {}", userCi, userName);
            
            // 하나은행 Mock 계좌 조회
            List<HanaBankMockAccount> accounts = hanaBankMockAccountRepository
                    .findByUserCiAndIsActiveTrue(userCi);
            
            // 응답 데이터 구성
            List<Map<String, Object>> accountList = accounts.stream()
                    .map(account -> {
                        Map<String, Object> accountData = new HashMap<>();
                        accountData.put("accountNum", account.getAccountNum());
                        accountData.put("bankName", account.getBankName());
                        accountData.put("bankCode", account.getBankCode());
                        accountData.put("productName", account.getProductName());
                        accountData.put("balanceAmt", account.getBalanceAmt());
                        accountData.put("accountType", account.getAccountType());
                        accountData.put("fintechUseNum", account.getFintechUseNum());
                        accountData.put("isActive", account.getIsActive());
                        return accountData;
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("accounts", accountList);
            responseData.put("totalCount", accountList.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "하나은행 계좌 조회 성공");
            response.put("data", responseData);
            
            log.info("내부 DB 하나은행 계좌 조회 완료 - userCi: {}, 계좌 수: {}", userCi, accountList.size());
            return response;
            
        } catch (Exception e) {
            log.error("내부 DB 하나은행 계좌 조회 실패 - userCi: {}", userCi, e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    public Map<String, Object> getAccountDetail(String accountNum, String userCi) {
        try {
            log.info("내부 DB에서 하나은행 계좌 상세 조회 - accountNum: {}", accountNum);
            
            Optional<HanaBankMockAccount> accountOpt = hanaBankMockAccountRepository
                    .findByAccountNumAndIsActiveTrue(accountNum);
            
            if (accountOpt.isEmpty()) {
                throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
            }
            
            HanaBankMockAccount account = accountOpt.get();
            
            // 사용자 권한 확인
            if (!userCi.equals(account.getUserCi())) {
                throw new CustomException(ErrorCode.ACCESS_DENIED);
            }
            
            Map<String, Object> accountData = new HashMap<>();
            accountData.put("accountNum", account.getAccountNum());
            accountData.put("bankName", account.getBankName());
            accountData.put("bankCode", account.getBankCode());
            accountData.put("productName", account.getProductName());
            accountData.put("balanceAmt", account.getBalanceAmt());
            accountData.put("accountType", account.getAccountType());
            accountData.put("fintechUseNum", account.getFintechUseNum());
            accountData.put("userName", account.getUserName());
            accountData.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "계좌 상세 조회 성공");
            response.put("data", accountData);
            
            log.info("내부 DB 하나은행 계좌 상세 조회 완료 - accountNum: {}", accountNum);
            return response;
            
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("내부 DB 하나은행 계좌 상세 조회 실패 - accountNum: {}", accountNum, e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    public Map<String, Object> getAccountTransactions(String accountNum, String userCi, 
                                                     String startDate, String endDate) {
        try {
            log.info("내부 DB에서 하나은행 거래내역 조회 - accountNum: {} ({} ~ {})", accountNum, startDate, endDate);
            
            // 계좌 존재 확인
            Optional<HanaBankMockAccount> accountOpt = hanaBankMockAccountRepository
                    .findByAccountNumAndIsActiveTrue(accountNum);
            
            if (accountOpt.isEmpty()) {
                throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
            }
            
            HanaBankMockAccount account = accountOpt.get();
            
            // 사용자 권한 확인
            if (!userCi.equals(account.getUserCi())) {
                throw new CustomException(ErrorCode.ACCESS_DENIED);
            }

            List<Map<String, Object>> transactions = generateMockTransactions(account, startDate, endDate);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("accountNum", accountNum);
            responseData.put("transactions", transactions);
            responseData.put("totalCount", transactions.size());
            responseData.put("startDate", startDate);
            responseData.put("endDate", endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "거래내역 조회 성공");
            response.put("data", responseData);
            
            log.info("내부 DB 하나은행 거래내역 조회 완료 - accountNum: {}, 거래 수: {}", accountNum, transactions.size());
            return response;
            
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("내부 DB 하나은행 거래내역 조회 실패 - accountNum: {}", accountNum, e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    private List<Map<String, Object>> generateMockTransactions(HanaBankMockAccount account, 
                                                              String startDate, String endDate) {
        List<Map<String, Object>> transactions = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        BigDecimal currentBalance = account.getBalanceAmt();
        
        for (int i = 0; i < 10; i++) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("transactionId", "TXN_" + account.getAccountNum() + "_" + (System.currentTimeMillis() + i));
            transaction.put("transactionDate", start.plusDays(i % (int) (end.toEpochDay() - start.toEpochDay() + 1)));
            transaction.put("transactionTime", "14:30:00");
            
            // 랜덤하게 입금/출금 결정
            boolean isDeposit = i % 3 == 0;
            BigDecimal amount = BigDecimal.valueOf(50000 + (i * 10000));
            
            transaction.put("transactionType", isDeposit ? "입금" : "출금");
            transaction.put("amount", amount);
            transaction.put("balanceAfter", currentBalance);
            transaction.put("description", isDeposit ? "급여입금" : "생활비출금");
            transaction.put("counterparty", isDeposit ? "회사" : "ATM");
            transaction.put("branchName", "하나은행 강남지점");
            
            transactions.add(transaction);
            
            // 잔액 업데이트 (역순으로 계산)
            if (isDeposit) {
                currentBalance = currentBalance.subtract(amount);
            } else {
                currentBalance = currentBalance.add(amount);
            }
        }
        
        // 최신 거래가 먼저 오도록 정렬
        Collections.reverse(transactions);
        
        return transactions;
    }
}