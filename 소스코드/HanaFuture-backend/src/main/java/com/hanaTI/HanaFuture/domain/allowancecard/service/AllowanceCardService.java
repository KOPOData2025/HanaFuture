package com.hanaTI.HanaFuture.domain.allowancecard.service;

import com.hanaTI.HanaFuture.domain.allowancecard.dto.AllowanceCardRequest;
import com.hanaTI.HanaFuture.domain.allowancecard.dto.AllowanceCardResponse;
import com.hanaTI.HanaFuture.domain.allowancecard.dto.CardChargeRequest;
import com.hanaTI.HanaFuture.domain.allowancecard.dto.CardUsageRequest;
import com.hanaTI.HanaFuture.domain.allowancecard.entity.AllowanceCard;
import com.hanaTI.HanaFuture.domain.allowancecard.entity.AllowanceCardStatus;
import com.hanaTI.HanaFuture.domain.allowancecard.repository.AllowanceCardRepository;
import com.hanaTI.HanaFuture.domain.child.entity.Child;
import com.hanaTI.HanaFuture.domain.child.repository.ChildRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AllowanceCardService {
    
    private final AllowanceCardRepository allowanceCardRepository;
    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public AllowanceCardResponse createAllowanceCard(AllowanceCardRequest request) {
        log.info("아이카드 생성 시작: 카드명 {}, 자녀 ID {}", 
                request.getCardName(), request.getChildId());
        
        // 1. 자녀 확인
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 부모 사용자 확인
        User parentUser = userRepository.findById(request.getParentUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 3. 새로운 카드 번호 생성
        String cardNumber = generateCardNumber();
        
        log.info("새 아이카드 번호 생성: {}", cardNumber);
        
        // 4. 아이카드 엔티티 생성
        AllowanceCard allowanceCard = AllowanceCard.builder()
                .cardNumber(cardNumber)
                .cardName(request.getCardName())
                .child(child)
                .parentUser(parentUser)
                .monthlyLimit(request.getMonthlyLimit())
                .dailyLimit(request.getDailyLimit())
                .autoChargeEnabled(request.getAutoChargeEnabled())
                .autoChargeAmount(request.getAutoChargeAmount())
                .autoChargeThreshold(request.getAutoChargeThreshold())
                .chargeSourceType(request.getChargeSourceType())
                .chargeSourceId(request.getChargeSourceId())
                .usageRestrictions(request.getUsageRestrictions())
                .notificationEnabled(request.getNotificationEnabled())
                .lowBalanceAlert(request.getLowBalanceAlert())
                .status(AllowanceCardStatus.ACTIVE)
                .build();
        
        // 5. 데이터베이스 저장
        AllowanceCard savedCard = allowanceCardRepository.save(allowanceCard);
        
        log.info("아이카드 생성 완료: ID {}, 카드번호 {}", 
                savedCard.getId(), savedCard.getCardNumber());
        
        // 6. 응답 DTO 생성
        return convertToResponse(savedCard);
    }
    
    public List<AllowanceCardResponse> getUserAllowanceCards(Long parentUserId) {
        log.info("부모의 아이카드 목록 조회: 부모 ID {}", parentUserId);
        
        // 1. 부모 사용자 확인
        User parentUser = userRepository.findById(parentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 2. 아이카드 목록 조회
        List<AllowanceCard> cards = allowanceCardRepository.findByParentUserAndStatusOrderByCreatedAtDesc(
                parentUser, AllowanceCardStatus.ACTIVE);
        
        log.info("아이카드 목록 조회 완료: {}개", cards.size());
        
        // 3. 응답 DTO 변환
        return cards.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void chargeCard(Long cardId, CardChargeRequest request) {
        log.info("아이카드 충전: 카드 ID {}, 금액 {}", cardId, request.getAmount());
        
        // 1. 아이카드 조회
        AllowanceCard card = allowanceCardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 카드 충전
        card.charge(
                request.getAmount(),
                request.getSourceType(),
                request.getSourceId(),
                request.getDescription()
        );
        
        log.info("아이카드 충전 완료: 카드 ID {}, 새 잔액 {}", 
                cardId, card.getCurrentBalance());
    }
    
    @Transactional
    public boolean useCard(Long cardId, CardUsageRequest request) {
        log.info("아이카드 사용: 카드 ID {}, 금액 {}, 가맹점 {}", 
                cardId, request.getAmount(), request.getMerchantName());
        
        // 1. 아이카드 조회
        AllowanceCard card = allowanceCardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 카드 사용
        boolean success = card.use(
                request.getAmount(),
                request.getMerchantName(),
                request.getCategory()
        );
        
        if (success) {
            log.info("아이카드 사용 완료: 카드 ID {}, 남은 잔액 {}", 
                    cardId, card.getCurrentBalance());
        } else {
            log.warn("아이카드 사용 실패: 카드 ID {}, 잔액 부족 또는 한도 초과", cardId);
        }
        
        return success;
    }
    
    public AllowanceCardResponse getCardDetail(Long cardId) {
        log.info("아이카드 상세 조회: 카드 ID {}", cardId);
        
        // 1. 아이카드 조회
        AllowanceCard card = allowanceCardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 응답 DTO 생성
        return convertToResponse(card);
    }
    
    @Transactional
    public void updateCardSettings(Long cardId, Map<String, Object> settings) {
        log.info("아이카드 설정 변경: 카드 ID {}", cardId);
        
        // 1. 아이카드 조회
        AllowanceCard card = allowanceCardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 설정 업데이트
        BigDecimal monthlyLimit = settings.containsKey("monthlyLimit") ? 
                new BigDecimal(settings.get("monthlyLimit").toString()) : null;
        BigDecimal dailyLimit = settings.containsKey("dailyLimit") ? 
                new BigDecimal(settings.get("dailyLimit").toString()) : null;
        Boolean autoChargeEnabled = settings.containsKey("autoChargeEnabled") ? 
                Boolean.valueOf(settings.get("autoChargeEnabled").toString()) : null;
        BigDecimal autoChargeAmount = settings.containsKey("autoChargeAmount") ? 
                new BigDecimal(settings.get("autoChargeAmount").toString()) : null;
        
        card.updateSettings(monthlyLimit, dailyLimit, autoChargeEnabled, autoChargeAmount);
        
        log.info("아이카드 설정 변경 완료: 카드 ID {}", cardId);
    }
    
    @Transactional
    public void toggleCardStatus(Long cardId) {
        log.info("아이카드 상태 변경: 카드 ID {}", cardId);
        
        // 1. 아이카드 조회
        AllowanceCard card = allowanceCardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        
        // 2. 상태 토글
        AllowanceCardStatus newStatus = card.getStatus() == AllowanceCardStatus.ACTIVE ? 
                AllowanceCardStatus.SUSPENDED : AllowanceCardStatus.ACTIVE;
        
        card.updateStatus(newStatus);
        
        log.info("아이카드 상태 변경 완료: 카드 ID {}, 새 상태 {}", cardId, newStatus);
    }
    
    /**
     * 새로운 카드 번호 생성
     * 가상 카드 번호: 16자리 숫자
     */
    private String generateCardNumber() {
        String prefix = "819"; // 하나은행 카드 코드
        
        // 나머지 13자리 랜덤 생성
        StringBuilder cardNumber = new StringBuilder(prefix);
        for (int i = 0; i < 13; i++) {
            cardNumber.append(new java.util.Random().nextInt(10));
        }
        
        String generatedNumber = cardNumber.toString();
        
        // 중복 확인
        if (allowanceCardRepository.findByCardNumber(generatedNumber).isPresent()) {
            return generateCardNumber(); // 재귀 호출로 중복 해결
        }
        
        return generatedNumber;
    }
    
    /**
     * AllowanceCard 엔티티를 AllowanceCardResponse DTO로 변환
     */
    private AllowanceCardResponse convertToResponse(AllowanceCard card) {
        return AllowanceCardResponse.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .cardName(card.getCardName())
                .childId(card.getChild().getId())
                .childName(card.getChild().getName())
                .parentUserId(card.getParentUser().getId())
                .currentBalance(card.getCurrentBalance())
                .monthlyLimit(card.getMonthlyLimit())
                .dailyLimit(card.getDailyLimit())
                .todayUsage(card.getTodayUsage())
                .monthlyUsage(card.getMonthlyUsage())
                .status(card.getStatus())
                .autoChargeEnabled(card.getAutoChargeEnabled())
                .autoChargeAmount(card.getAutoChargeAmount())
                .autoChargeThreshold(card.getAutoChargeThreshold())
                .chargeSourceType(card.getChargeSourceType())
                .chargeSourceId(card.getChargeSourceId())
                .usageRestrictions(card.getUsageRestrictions())
                .notificationEnabled(card.getNotificationEnabled())
                .lowBalanceAlert(card.getLowBalanceAlert())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }
}










