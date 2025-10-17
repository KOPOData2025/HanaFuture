package com.hanaTI.HanaFuture.domain.openbanking.controller;

import com.hanaTI.HanaFuture.domain.auth.dto.request.IdentityVerificationRequest;
import com.hanaTI.HanaFuture.domain.auth.dto.response.IdentityVerificationResponse;
import com.hanaTI.HanaFuture.domain.openbanking.service.OpenBankingService;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.domain.banking.mock.entity.HanaBankMockAccount;
import com.hanaTI.HanaFuture.domain.banking.mock.repository.HanaBankMockAccountRepository;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/openbanking")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "오픈뱅킹", description = "오픈뱅킹 연동 API")
public class OpenBankingController {
    
    private final OpenBankingService openBankingService;
    private final UserRepository userRepository;
    private final HanaBankMockAccountRepository hanaBankMockAccountRepository;
    
    /**
     * 본인 인증 및 CI 생성
     */
    @Operation(summary = "본인 인증", description = "주민등록번호와 휴대폰 인증을 통한 본인 인증")
    @PostMapping("/identity-verification")
    public ResponseEntity<ApiResponse<IdentityVerificationResponse>> verifyIdentity(
            @Valid @RequestBody IdentityVerificationRequest request) {
        
        try {
            log.info("본인 인증 요청: 이름 {}, 휴대폰 {}", request.getName(), request.getPhoneNumber());
            
            // 1. SMS 인증 (실제로는 별도의 SMS 인증 단계가 필요)

            // 2. 주민등록번호를 CI로 변환
            String userCi = openBankingService.generateCiFromResidentNumber(request.getResidentNumber());
            
            // 3. 인증 결과 반환
            IdentityVerificationResponse response = IdentityVerificationResponse.builder()
                    .verificationId("VERIFY_" + System.currentTimeMillis())
                    .userCi(userCi)
                    .verificationSuccess(true)
                    .message("본인 인증이 완료되었습니다.")
                    .build();
            
            log.info("본인 인증 완료: CI {}", userCi.substring(0, 8) + "...");
            
            return ResponseEntity.ok(
                    ApiResponse.success("본인 인증이 완료되었습니다.", response)
            );
            
        } catch (Exception e) {
            log.error("본인 인증 중 오류", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("본인 인증 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 계좌 조회 (이메일 기반)
     */
    @Operation(summary = "계좌 조회", description = "사용자 이메일로 계좌를 조회합니다")
    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserAccounts(
            @RequestBody Map<String, Object> request) {
        
        try {
            String userEmail = (String) request.get("userEmail");
            String userCi = (String) request.get("userCi");
            String userName = (String) request.get("userName");
            String bankCode = (String) request.get("bankCode");
            String bankName = (String) request.get("bankName");
            
            log.info(" 계좌 조회 요청: 이메일={}, CI={}, 이름={}, 은행={}", userEmail, userCi, userName, bankName);
            
            // 1. 이메일로 사용자 조회
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                log.error("사용자를 찾을 수 없습니다: {}", userEmail);
                return ResponseEntity.ok(
                    ApiResponse.error("사용자를 찾을 수 없습니다.")
                );
            }
            
            log.info("사용자 조회 성공: DB이름='{}', 요청이름='{}', 이메일='{}'", 
                    user.getName(), userName, user.getEmail());

            String searchName = user.getName(); // DB에서 가져온 실제 사용자 이름 사용
            log.info(" Mock 계좌 조회 시도: 검색이름='{}'", searchName);
            
            // 먼저 모든 계좌를 조회해서 상태 확인
            List<HanaBankMockAccount> allUserAccounts = hanaBankMockAccountRepository
                    .findByUserName(searchName);
            log.info(" 사용자 '{}'의 전체 Mock 계좌 수: {}", searchName, allUserAccounts.size());

            List<HanaBankMockAccount> allMockAccounts = hanaBankMockAccountRepository.findAll();
            log.info(" 전체 Mock 계좌 테이블 총 계좌 수: {}", allMockAccounts.size());

            for (HanaBankMockAccount account : allMockAccounts) {
                log.info(" Mock 계좌: user_name='{}', account_num='{}', active={}", 
                        account.getUserName(), account.getAccountNum(), account.getIsActive());
            }
            
            // 각 계좌의 활성 상태 로깅
            for (HanaBankMockAccount account : allUserAccounts) {
                log.info("매칭된 계좌: {} - 활성상태: {}", account.getAccountNum(), account.getIsActive());
            }

            List<HanaBankMockAccount> accounts = allUserAccounts.stream()
                    .filter(account -> account.getIsActive() == null || account.getIsActive())
                    .toList();
            
            log.info("조회된 활성 계좌 수: {}", accounts.size());

            long totalAccounts = hanaBankMockAccountRepository.count();
            log.info("전체 하나은행 Mock 계좌 수: {}", totalAccounts);

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
            responseData.put("resList", accountList);
            responseData.put("totalCount", accountList.size());
            
            return ResponseEntity.ok(
                ApiResponse.success("계좌 조회가 완료되었습니다.", responseData)
            );
            
        } catch (Exception e) {
            log.error("계좌 조회 중 오류", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌 조회 중 오류가 발생했습니다."));
        }
    }
}
