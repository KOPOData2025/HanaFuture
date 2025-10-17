package com.hanaTI.HanaFuture.domain.childcard.controller;

import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "ChildCard", description = "아이카드 관련 API")
@RestController
@RequestMapping("/api/child-cards")
@RequiredArgsConstructor
@Slf4j
public class ChildCardController {

    @Operation(
        summary = "사용자 아이카드 목록 조회", 
        description = "사용자가 발급한 아이카드 목록을 조회합니다."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserChildCards(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        log.info("사용자 {}의 아이카드 목록 조회", userId);

        try {
            List<Map<String, Object>> childCards = createMockChildCardData(userId);
            
            log.info("아이카드 {}개 조회됨", childCards.size());
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", childCards));
            
        } catch (Exception e) {
            log.error("아이카드 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("아이카드 조회에 실패했습니다."));
        }
    }

    @Operation(
        summary = "아이카드 발급", 
        description = "등록된 자녀에게 새로운 아이카드를 발급합니다."
    )
    @PostMapping("/issue")
    public ResponseEntity<ApiResponse<Object>> issueChildCard(
            @RequestBody Map<String, Object> request) {
        
        log.info("아이카드 발급 요청: {}", request);
        
        try {
            Map<String, Object> newCard = new HashMap<>();
            newCard.put("id", System.currentTimeMillis());
            newCard.put("childId", request.get("childId")); // 자녀 ID 연결
            newCard.put("userId", request.get("userId"));
            newCard.put("cardNumber", generateCardNumber());
            newCard.put("childName", request.get("childName"));
            newCard.put("childAge", request.get("childAge"));
            newCard.put("monthlyLimit", request.get("monthlyLimit"));
            newCard.put("dailyLimit", request.get("dailyLimit"));
            newCard.put("balance", BigDecimal.ZERO);
            newCard.put("status", "ACTIVE");
            newCard.put("cardType", request.getOrDefault("cardType", "PREPAID")); // 선불카드
            newCard.put("parentalControl", request.getOrDefault("parentalControl", true));
            newCard.put("issuedAt", LocalDateTime.now().toString());
            
            log.info("아이카드 발급 완료: {}", newCard);
            
            return ResponseEntity.ok(ApiResponse.success("아이카드가 발급되었습니다.", newCard));
            
        } catch (Exception e) {
            log.error("아이카드 발급 실패", e);
            return ResponseEntity.ok(ApiResponse.error("아이카드 발급에 실패했습니다."));
        }
    }

    @Operation(
        summary = "자녀별 아이카드 조회", 
        description = "특정 자녀의 아이카드를 조회합니다."
    )
    @GetMapping("/child/{childId}")
    public ResponseEntity<ApiResponse<Object>> getCardsByChild(
            @Parameter(description = "자녀 ID") @PathVariable Long childId) {
        
        log.info("자녀 {}의 아이카드 조회", childId);
        
        try {
            List<Map<String, Object>> cards = createMockCardsByChild(childId);
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", cards));
            
        } catch (Exception e) {
            log.error("자녀별 아이카드 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀별 아이카드 조회에 실패했습니다."));
        }
    }

    @Operation(
        summary = "아이카드 용돈 충전", 
        description = "아이카드에 용돈을 충전합니다."
    )
    @PostMapping("/{cardId}/charge")
    public ResponseEntity<ApiResponse<Object>> chargeAllowance(
            @Parameter(description = "카드 ID") @PathVariable Long cardId,
            @RequestBody Map<String, Object> request) {
        
        log.info("아이카드 {} 용돈 충전 요청: {}", cardId, request);
        
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            Map<String, Object> result = Map.of(
                "cardId", cardId,
                "chargedAmount", amount,
                "newBalance", amount.add(BigDecimal.valueOf(50000)),
                "chargedAt", LocalDateTime.now().toString()
            );
            
            log.info("아이카드 용돈 충전 완료: {}", result);
            
            return ResponseEntity.ok(ApiResponse.success("용돈 충전이 완료되었습니다.", result));
            
        } catch (Exception e) {
            log.error("아이카드 용돈 충전 실패", e);
            return ResponseEntity.ok(ApiResponse.error("용돈 충전에 실패했습니다."));
        }
    }

    @Operation(
        summary = "아이카드 사용내역 조회", 
        description = "아이카드의 사용내역을 조회합니다."
    )
    @GetMapping("/{cardId}/transactions")
    public ResponseEntity<ApiResponse<Object>> getCardTransactions(
            @Parameter(description = "카드 ID") @PathVariable Long cardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("아이카드 {} 사용내역 조회 (page: {}, size: {})", cardId, page, size);
        
        try {
            List<Map<String, Object>> transactions = createMockTransactionData(cardId);
            
            Map<String, Object> result = Map.of(
                "transactions", transactions,
                "totalCount", transactions.size(),
                "page", page,
                "size", size
            );
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", result));
            
        } catch (Exception e) {
            log.error("아이카드 사용내역 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("사용내역 조회에 실패했습니다."));
        }
    }

    private List<Map<String, Object>> createMockCardsByChild(Long childId) {
        List<Map<String, Object>> cards = new ArrayList<>();
        
        if (childId == 1L) { // 하나미
            Map<String, Object> card = new HashMap<>();
            card.put("id", 1L);
            card.put("childId", childId);
            card.put("cardNumber", "5432-****-****-1234");
            card.put("childName", "하나미");
            card.put("childAge", 8);
            card.put("monthlyLimit", BigDecimal.valueOf(100000));
            card.put("dailyLimit", BigDecimal.valueOf(10000));
            card.put("balance", BigDecimal.valueOf(45000));
            card.put("status", "ACTIVE");
            card.put("cardType", "PREPAID");
            card.put("parentalControl", true);
            card.put("issuedAt", "2024-01-10T09:00:00");
            cards.add(card);
        } else if (childId == 2L) { // 하나준
            Map<String, Object> card = new HashMap<>();
            card.put("id", 2L);
            card.put("childId", childId);
            card.put("cardNumber", "5432-****-****-5678");
            card.put("childName", "하나준");
            card.put("childAge", 12);
            card.put("monthlyLimit", BigDecimal.valueOf(150000));
            card.put("dailyLimit", BigDecimal.valueOf(15000));
            card.put("balance", BigDecimal.valueOf(78000));
            card.put("status", "ACTIVE");
            card.put("cardType", "PREPAID");
            card.put("parentalControl", true);
            card.put("issuedAt", "2024-02-05T11:00:00");
            cards.add(card);
        }
        
        return cards;
    }

    private List<Map<String, Object>> createMockChildCardData(Long userId) {
        List<Map<String, Object>> childCards = new ArrayList<>();
        
        if (userId == 1L) { // 이하나 사용자
            childCards.add(Map.of(
                "id", 1L,
                "cardNumber", "5432-****-****-1234",
                "childName", "하나미",
                "childAge", 8,
                "monthlyLimit", BigDecimal.valueOf(100000),
                "balance", BigDecimal.valueOf(45000),
                "status", "ACTIVE",
                "issuedAt", "2024-01-10T09:00:00",
                "lastUsedAt", "2024-03-15T16:30:00"
            ));
            
            childCards.add(Map.of(
                "id", 2L,
                "cardNumber", "5432-****-****-5678",
                "childName", "하나준",
                "childAge", 12,
                "monthlyLimit", BigDecimal.valueOf(150000),
                "balance", BigDecimal.valueOf(78000),
                "status", "ACTIVE",
                "issuedAt", "2024-02-05T11:00:00",
                "lastUsedAt", "2024-03-16T14:20:00"
            ));
        }
        
        return childCards;
    }

    private List<Map<String, Object>> createMockTransactionData(Long cardId) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        transactions.add(Map.of(
            "id", 1L,
            "amount", BigDecimal.valueOf(5000),
            "merchantName", "편의점 GS25",
            "category", "FOOD",
            "transactionAt", "2024-03-16T14:20:00",
            "type", "PAYMENT"
        ));
        
        transactions.add(Map.of(
            "id", 2L,
            "amount", BigDecimal.valueOf(20000),
            "merchantName", "용돈 충전",
            "category", "ALLOWANCE",
            "transactionAt", "2024-03-15T09:00:00",
            "type", "CHARGE"
        ));
        
        return transactions;
    }

    private String generateCardNumber() {
        return String.format("5432-%04d-%04d-%04d", 
            (int)(Math.random() * 10000),
            (int)(Math.random() * 10000),
            (int)(Math.random() * 10000)
        );
    }
}
