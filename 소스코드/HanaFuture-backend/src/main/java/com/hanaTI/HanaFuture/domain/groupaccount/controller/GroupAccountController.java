package com.hanaTI.HanaFuture.domain.groupaccount.controller;

import com.hanaTI.HanaFuture.domain.groupaccount.dto.request.GroupAccountRequest;
import com.hanaTI.HanaFuture.domain.groupaccount.dto.response.GroupAccountResponse;
import com.hanaTI.HanaFuture.domain.groupaccount.dto.response.GroupAccountTransactionResponse;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.service.GroupAccountService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanaTI.HanaFuture.domain.groupaccount.service.GroupAccountTransactionService;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "GroupAccount", description = "모임통장 관련 API")
@RestController
@RequestMapping("/api/group-accounts")
@RequiredArgsConstructor
@Slf4j
public class GroupAccountController {
    
    private final GroupAccountService groupAccountService;
    private final GroupAccountTransactionService transactionService;
    
    @Operation(
            summary = "모임통장 개설",
            description = "새로운 모임통장을 개설합니다. 실제 은행 방식으로 새 계좌를 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<GroupAccountResponse>> createGroupAccount(
            @Valid @RequestBody GroupAccountRequest request) {
        
        log.info(" 모임통장 개설 요청 - 이름: {}, 계좌명: {}, 사용자ID: {}", 
                request.getName(), request.getGroupAccountName(), request.getPrimaryUserId());
        
        try {
            GroupAccountResponse response = groupAccountService.createGroupAccount(request);
            
            log.info("모임통장 개설 완료: 계좌번호 {}", response.getAccountNumber());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("모임통장이 성공적으로 개설되었습니다.", response));
                    
        } catch (Exception e) {
            log.error("모임통장 개설 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("모임통장 개설에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "모임통장 조회",
            description = "사용자의 모임통장 목록을 조회합니다."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserGroupAccounts(
            @PathVariable Long userId) {
        
        try {
            log.info("사용자 {}의 모임통장 목록 조회", userId);

            var groupAccounts = groupAccountService.getUserGroupAccounts(userId);
            
            log.info("모임통장 {}개 조회됨", groupAccounts.size());
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", groupAccounts));
            
        } catch (Exception e) {
            log.error("모임통장 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("모임통장 조회에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "모임통장 초대 정보 조회",
            description = "초대 링크로 모임통장 정보를 조회합니다."
    )
    @GetMapping("/{groupAccountId}/invite-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInviteInfo(
            @PathVariable Long groupAccountId) {
        
        try {
            GroupAccount groupAccount = groupAccountService.getGroupAccountById(groupAccountId);
            
            Map<String, Object> inviteInfo = Map.of(
                "id", groupAccount.getId(),
                "name", groupAccount.getName(),
                "purpose", groupAccount.getPurpose().getDescription(),
                "memberCount", groupAccount.getMembers().size(),
                "createdAt", groupAccount.getCreatedAt(),
                "creator", Map.of(
                    "name", groupAccount.getCreator().getName(),
                    "email", groupAccount.getCreator().getEmail()
                )
            );
            
            return ResponseEntity.ok(ApiResponse.success(inviteInfo));
            
        } catch (Exception e) {
            log.error("초대 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("초대 정보를 찾을 수 없습니다."));
        }
    }
    
    @Operation(
            summary = "모임통장 멤버 조회",
            description = "모임통장의 멤버 목록을 조회합니다."
    )
    @GetMapping("/{groupAccountId}/members")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGroupAccountMembers(
            @PathVariable Long groupAccountId) {
        
        try {
            log.info("모임통장 {}의 멤버 목록 조회", groupAccountId);
            
            Map<String, Object> memberInfo = groupAccountService.getGroupAccountMembers(groupAccountId);
            
            return ResponseEntity.ok(ApiResponse.success(memberInfo));
            
        } catch (Exception e) {
            log.error("멤버 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("멤버 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "PENDING 초대 생성",
            description = "초대할 친구 정보를 저장하고 초대 토큰을 생성합니다"
    )
    @PostMapping("/{groupAccountId}/create-pending-invite")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPendingInvite(
            @PathVariable Long groupAccountId,
            @RequestBody Map<String, Object> request) {
        
        try {
            String name = (String) request.get("name");
            String phone = (String) request.get("phone");
            Long inviterId = request.get("inviterId") instanceof Number 
                ? ((Number) request.get("inviterId")).longValue() 
                : Long.valueOf(request.get("inviterId").toString());
            
            log.info(" PENDING 초대 생성 요청 - 모임통장 ID: {}, 초대받을 사람: {}, 전화번호: {}", 
                    groupAccountId, name, phone);
            
            Map<String, String> inviteInfo = groupAccountService.createPendingInvite(
                    groupAccountId, name, phone, inviterId);
            
            return ResponseEntity.ok(ApiResponse.success("초대 정보가 생성되었습니다.", inviteInfo));
            
        } catch (Exception e) {
            log.error("PENDING 초대 생성 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("초대 생성에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "모임통장 초대 수락",
            description = "초대받은 사용자가 모임통장에 참여합니다."
    )
    @PostMapping("/{groupAccountId}/accept-invite")
    public ResponseEntity<ApiResponse<Void>> acceptInvite(
            @PathVariable Long groupAccountId,
            @RequestBody Map<String, Object> request) {
        
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String token = request.get("token").toString();
            
            log.info(" 모임통장 초대 수락 요청 - 모임통장 ID: {}, 사용자 ID: {}", groupAccountId, userId);
            
            groupAccountService.acceptInvite(groupAccountId, userId, token);
            
            return ResponseEntity.ok(ApiResponse.success("모임통장에 성공적으로 참여했습니다."));
            
        } catch (Exception e) {
            log.error("모임통장 초대 수락 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("초대 수락에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "모임통장 멤버 초대 수락 (memberId 기반)",
            description = "알림에서 memberId를 통해 모임통장 초대를 수락합니다."
    )
    @PostMapping("/members/{memberId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInviteByMemberId(
            @PathVariable Long memberId,
            @RequestParam Long userId) {
        
        try {
            log.info(" 모임통장 멤버 초대 수락 요청 - 멤버 ID: {}, 사용자 ID: {}", memberId, userId);
            
            groupAccountService.acceptInviteByMemberId(memberId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("모임통장에 성공적으로 참여했습니다."));
            
        } catch (Exception e) {
            log.error("모임통장 멤버 초대 수락 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("초대 수락에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "멤버 추가를 위한 정보 조회",
            description = "사용자의 가족 목록을 포함한 멤버 추가 정보를 조회합니다."
    )
    @GetMapping("/member-addition-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMemberAdditionInfo(
            @RequestParam Long userId) {
        
        log.info("‍‍‍ 멤버 추가 정보 조회 요청 - 사용자 ID: {}", userId);
        
        Map<String, Object> info = groupAccountService.getMemberAdditionInfo(userId);
        
        return ResponseEntity.ok(ApiResponse.success(info));
    }
    
    @Operation(
            summary = "모임통장 입금",
            description = "연결된 개인 계좌에서 모임통장으로 입금합니다."
    )
    @PostMapping("/{groupAccountId}/deposit")
    public ResponseEntity<ApiResponse<GroupAccountTransactionResponse>> depositToGroupAccount(
            @PathVariable Long groupAccountId,
            @RequestBody DepositRequest request) {
        
        try {
            log.info(" 모임통장 입금 요청 - 모임통장 ID: {}, 사용자 ID: {}, 금액: {}", 
                    groupAccountId, request.getUserId(), request.getAmount());
            
            GroupAccountTransaction transaction = transactionService.depositToGroupAccount(
                    groupAccountId,
                    request.getUserId(),
                    request.getAmount(),
                    request.getPassword(),
                    request.getSourceAccountNumber(),
                    request.getSourceBankName(),
                    request.getDescription()
            );
            
            GroupAccountTransactionResponse response = GroupAccountTransactionResponse.from(transaction);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            log.error("모임통장 입금 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("입금에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "모임통장 출금",
            description = "모임통장에서 개인 계좌로 출금합니다."
    )
    @PostMapping("/{groupAccountId}/withdraw")
    public ResponseEntity<ApiResponse<GroupAccountTransactionResponse>> withdrawFromGroupAccount(
            @PathVariable Long groupAccountId,
            @RequestBody WithdrawRequest request) {
        
        try {
            log.info(" 모임통장 출금 요청 - 모임통장 ID: {}, 사용자 ID: {}, 금액: {}", 
                    groupAccountId, request.getUserId(), request.getAmount());
            
            GroupAccountTransaction transaction = transactionService.withdrawFromGroupAccount(
                    groupAccountId,
                    request.getUserId(),
                    request.getAmount(),
                    request.getPassword(),
                    request.getTargetAccountNumber(),
                    request.getTargetBankName(),
                    request.getDescription()
            );
            
            GroupAccountTransactionResponse response = GroupAccountTransactionResponse.from(transaction);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            log.error("모임통장 출금 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("출금에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "모임통장 삭제",
            description = "모임통장을 삭제합니다. 생성자만 삭제할 수 있습니다."
    )
    @DeleteMapping("/{groupAccountId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroupAccount(
            @PathVariable Long groupAccountId,
            @RequestParam Long userId) {
        
        try {
            log.info(" 모임통장 삭제 요청 - 모임통장 ID: {}, 사용자 ID: {}", groupAccountId, userId);
            
            groupAccountService.deleteGroupAccount(groupAccountId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("모임통장이 성공적으로 삭제되었습니다."));
            
        } catch (Exception e) {
            log.error("모임통장 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("모임통장 삭제에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "거래내역 조회",
            description = "모임통장의 거래내역을 조회합니다."
    )
    @GetMapping("/{groupAccountId}/transactions")
    public ResponseEntity<ApiResponse<Page<GroupAccountTransactionResponse>>> getTransactionHistory(
            @PathVariable Long groupAccountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupAccountTransaction> transactions = transactionService.getTransactionHistory(
                    groupAccountId, pageable);
            
            // 엔티티를 DTO로 변환
            Page<GroupAccountTransactionResponse> transactionResponses = transactions.map(
                    GroupAccountTransactionResponse::from
            );
            
            return ResponseEntity.ok(ApiResponse.success(transactionResponses));
            
        } catch (Exception e) {
            log.error("거래내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래내역 조회에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "거래 통계 조회",
            description = "모임통장의 거래 통계를 조회합니다."
    )
    @GetMapping("/{groupAccountId}/stats")
    public ResponseEntity<ApiResponse<GroupAccountTransactionService.TransactionStats>> getTransactionStats(
            @PathVariable Long groupAccountId) {
        
        try {
            GroupAccountTransactionService.TransactionStats stats = 
                    transactionService.getTransactionStats(groupAccountId);
            
            return ResponseEntity.ok(ApiResponse.success(stats));
            
        } catch (Exception e) {
            log.error("거래 통계 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("거래 통계 조회에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "모임통장 상세 정보 조회",
            description = "실시간 잔액을 포함한 모임통장 상세 정보를 조회합니다."
    )
    @GetMapping("/{groupAccountId}/details")
    public ResponseEntity<ApiResponse<GroupAccountDetailResponse>> getGroupAccountDetails(
            @PathVariable Long groupAccountId) {
        
        try {
            // 모임통장 조회
            GroupAccount groupAccount = groupAccountService.getGroupAccountById(groupAccountId);
            
            // 거래 통계 조회
            GroupAccountTransactionService.TransactionStats stats = 
                    transactionService.getTransactionStats(groupAccountId);
            
            // 응답 생성
            GroupAccountDetailResponse response = GroupAccountDetailResponse.builder()
                    .id(groupAccount.getId())
                    .name(groupAccount.getName())
                    .accountName(groupAccount.getGroupAccountName())
                    .accountNumber(groupAccount.getGroupAccountNumber())
                    .currentBalance(stats.getCurrentBalance())
                    .totalDeposits(stats.getTotalDeposits())
                    .totalWithdrawals(stats.getTotalWithdrawals())
                    .lastBalanceUpdate(groupAccount.getLastBalanceUpdate())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            log.error("모임통장 상세 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("모임통장 정보 조회에 실패했습니다."));
        }
    }
    
    // Response DTOs
    @lombok.Getter
    @lombok.Builder
    public static class GroupAccountDetailResponse {
        private Long id;
        private String name;
        private String accountName;
        private String accountNumber;
        private BigDecimal currentBalance;
        private BigDecimal totalDeposits;
        private BigDecimal totalWithdrawals;
        private java.time.LocalDateTime lastBalanceUpdate;
    }
    
    // Request DTOs
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    public static class DepositRequest {
        private Long userId;
        private BigDecimal amount;
        private String password;
        private String sourceAccountNumber;
        private String sourceBankName;
        private String description;
    }
    
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    public static class WithdrawRequest {
        private Long userId;
        private BigDecimal amount;
        private String password;
        private String targetAccountNumber;
        private String targetBankName;
        private String description;
    }
}
