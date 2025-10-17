package com.hanaTI.HanaFuture.domain.allowancecard.controller;

import com.hanaTI.HanaFuture.domain.allowancecard.dto.AllowanceCardRequest;
import com.hanaTI.HanaFuture.domain.allowancecard.dto.AllowanceCardResponse;
import com.hanaTI.HanaFuture.domain.allowancecard.dto.CardChargeRequest;
import com.hanaTI.HanaFuture.domain.allowancecard.dto.CardUsageRequest;
import com.hanaTI.HanaFuture.domain.allowancecard.service.AllowanceCardService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "AllowanceCard", description = "아이카드 관련 API")
@RestController
@RequestMapping("/api/allowance-cards")
@RequiredArgsConstructor
@Slf4j
public class AllowanceCardController {
    
    private final AllowanceCardService allowanceCardService;
    
    @Operation(
            summary = "아이카드 생성",
            description = "새로운 아이카드를 생성합니다. 선불식 카드로 계좌 개설 없이 충전하여 사용"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<AllowanceCardResponse>> createAllowanceCard(
            @Valid @RequestBody AllowanceCardRequest request) {
        
        log.info("아이카드 생성 요청: 카드명 {}, 자녀 ID {}", 
                request.getCardName(), request.getChildId());
        
        try {
            AllowanceCardResponse response = allowanceCardService.createAllowanceCard(request);
            
            log.info("아이카드 생성 완료: 카드번호 {}", response.getCardNumber());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("아이카드가 성공적으로 생성되었습니다.", response));
                    
        } catch (Exception e) {
            log.error("아이카드 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이카드 생성에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "사용자 아이카드 조회",
            description = "부모 사용자의 모든 아이카드를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<AllowanceCardResponse>>> getUserAllowanceCards(
            @RequestParam Long parentUserId) {
        
        try {
            List<AllowanceCardResponse> cards = allowanceCardService.getUserAllowanceCards(parentUserId);
            
            return ResponseEntity.ok(
                    ApiResponse.success("아이카드 조회 완료", cards));
                    
        } catch (Exception e) {
            log.error("아이카드 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이카드 조회에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "아이카드 충전",
            description = "아이카드에 용돈을 충전합니다. 부모 계좌나 모임통장에서 출금"
    )
    @PostMapping("/{cardId}/charge")
    public ResponseEntity<ApiResponse<Object>> chargeAllowanceCard(
            @PathVariable Long cardId,
            @Valid @RequestBody CardChargeRequest request) {
        
        log.info("아이카드 충전 요청: 카드 ID {}, 금액 {}", cardId, request.getAmount());
        
        try {
            allowanceCardService.chargeCard(cardId, request);
            
            return ResponseEntity.ok(
                    ApiResponse.success("충전이 완료되었습니다.", null));
                    
        } catch (Exception e) {
            log.error("아이카드 충전 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("충전에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "아이카드 사용",
            description = "아이카드로 결제합니다. (가맹점에서 호출)"
    )
    @PostMapping("/{cardId}/use")
    public ResponseEntity<ApiResponse<Object>> useAllowanceCard(
            @PathVariable Long cardId,
            @Valid @RequestBody CardUsageRequest request) {
        
        log.info("아이카드 사용 요청: 카드 ID {}, 금액 {}, 가맹점 {}", 
                cardId, request.getAmount(), request.getMerchantName());
        
        try {
            boolean success = allowanceCardService.useCard(cardId, request);
            
            if (success) {
                return ResponseEntity.ok(
                        ApiResponse.success("결제가 완료되었습니다.", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("잔액이 부족하거나 사용 한도를 초과했습니다."));
            }
                    
        } catch (Exception e) {
            log.error("아이카드 사용 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("결제에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "아이카드 상세 조회",
            description = "특정 아이카드의 상세 정보와 사용 내역을 조회합니다."
    )
    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<AllowanceCardResponse>> getAllowanceCardDetail(
            @PathVariable Long cardId) {
        
        try {
            AllowanceCardResponse card = allowanceCardService.getCardDetail(cardId);
            
            return ResponseEntity.ok(
                    ApiResponse.success("아이카드 상세 조회 완료", card));
                    
        } catch (Exception e) {
            log.error("아이카드 상세 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이카드 조회에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "아이카드 설정 변경",
            description = "아이카드의 한도, 자동충전 등 설정을 변경합니다."
    )
    @PutMapping("/{cardId}/settings")
    public ResponseEntity<ApiResponse<Object>> updateCardSettings(
            @PathVariable Long cardId,
            @RequestBody Map<String, Object> settings) {
        
        try {
            allowanceCardService.updateCardSettings(cardId, settings);
            
            return ResponseEntity.ok(
                    ApiResponse.success("설정이 변경되었습니다.", null));
                    
        } catch (Exception e) {
            log.error("아이카드 설정 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("설정 변경에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "아이카드 일시정지/해제",
            description = "아이카드를 일시정지하거나 해제합니다."
    )
    @PostMapping("/{cardId}/toggle-status")
    public ResponseEntity<ApiResponse<Object>> toggleCardStatus(
            @PathVariable Long cardId) {
        
        try {
            allowanceCardService.toggleCardStatus(cardId);
            
            return ResponseEntity.ok(
                    ApiResponse.success("카드 상태가 변경되었습니다.", null));
                    
        } catch (Exception e) {
            log.error("아이카드 상태 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("상태 변경에 실패했습니다."));
        }
    }
}
