package com.hanaTI.HanaFuture.domain.verification.service;

import com.hanaTI.HanaFuture.domain.verification.dto.PhoneVerificationRequest;
import com.hanaTI.HanaFuture.domain.verification.dto.PhoneVerificationResponse;
import com.hanaTI.HanaFuture.domain.verification.dto.VerificationCodeRequest;
import com.hanaTI.HanaFuture.domain.verification.entity.PhoneVerification;
import com.hanaTI.HanaFuture.domain.verification.repository.PhoneVerificationRepository;
import com.hanaTI.HanaFuture.global.config.CoolSmsProperties;
import com.hanaTI.HanaFuture.global.exception.CustomException;
import com.hanaTI.HanaFuture.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class PhoneVerificationService {

    private final PhoneVerificationRepository phoneVerificationRepository;
    private final CoolSmsProperties coolSmsProperties;
    private final DefaultMessageService messageService;

    public PhoneVerificationService(PhoneVerificationRepository phoneVerificationRepository, 
                                  CoolSmsProperties coolSmsProperties) {
        this.phoneVerificationRepository = phoneVerificationRepository;
        this.coolSmsProperties = coolSmsProperties;
        
        log.info("CoolSMS 초기화 중...");
        log.info("API Key: {}", coolSmsProperties.getApiKey());
        log.info("발신번호: {}", coolSmsProperties.getFromNumber());
        
        try {
            this.messageService = NurigoApp.INSTANCE.initialize(
                    coolSmsProperties.getApiKey(),
                    coolSmsProperties.getApiSecret(),
                    "https://api.coolsms.co.kr"
            );
            log.info("CoolSMS 초기화 완료");
        } catch (Exception e) {
            log.error("CoolSMS 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("CoolSMS 초기화 실패", e);
        }
    }

    /**
     * 휴대폰 인증번호 발송
     */
    public PhoneVerificationResponse sendVerificationCode(PhoneVerificationRequest request) {
        try {
            // 기존 미인증 코드 삭제
            phoneVerificationRepository.deleteUnverifiedByPhoneNumber(request.getPhoneNumber());

            // 인증번호 생성
            String verificationCode = generateVerificationCode();
            LocalDateTime expiryTime = LocalDateTime.now()
                    .plusMinutes(coolSmsProperties.getVerification().getExpiryMinutes());

            // 인증 정보 저장
            PhoneVerification verification = PhoneVerification.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .verificationCode(verificationCode)
                    .expiryTime(expiryTime)
                    .purpose(request.getPurpose())
                    .build();

            phoneVerificationRepository.save(verification);

            // SMS 발송
            boolean sent = sendSms(request.getPhoneNumber(), verificationCode, request.getPurpose());

            if (sent) {
                return PhoneVerificationResponse.success(
                        request.getPhoneNumber(),
                        expiryTime,
                        coolSmsProperties.getVerification().getExpiryMinutes()
                );
            } else {
                return PhoneVerificationResponse.failure(
                        request.getPhoneNumber(),
                        "문자 발송에 실패했습니다."
                );
            }

        } catch (Exception e) {
            log.error("휴대폰 인증번호 발송 실패: {}", e.getMessage(), e);
            return PhoneVerificationResponse.failure(
                    request.getPhoneNumber(),
                    "인증번호 발송 중 오류가 발생했습니다."
            );
        }
    }

    /**
     * 인증번호 확인
     */
    @Transactional
    public boolean verifyCode(VerificationCodeRequest request) {
        // 테스트 모드인 경우 고정 인증번호 허용
        if (coolSmsProperties.getVerification().isTestMode()) {
            String testCode = coolSmsProperties.getVerification().getTestCode();
            if (testCode.equals(request.getVerificationCode())) {
                log.info("[테스트 모드] 고정 인증번호로 인증 성공 - 수신번호: {}, 입력코드: {}", 
                        request.getPhoneNumber(), request.getVerificationCode());
                return true;
            }
        }
        
        Optional<PhoneVerification> verificationOpt = phoneVerificationRepository
                .findByPhoneNumberAndVerificationCodeAndVerifiedFalse(
                        request.getPhoneNumber(),
                        request.getVerificationCode()
                );

        if (verificationOpt.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        PhoneVerification verification = verificationOpt.get();

        if (verification.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        // 인증 완료 처리
        verification.markAsVerified();
        phoneVerificationRepository.save(verification);

        return true;
    }

    /**
     * SMS 발송
     */
    private boolean sendSms(String phoneNumber, String verificationCode, String purpose) {
        // 테스트 모드인 경우 실제 SMS 발송하지 않음
        if (coolSmsProperties.getVerification().isTestMode()) {
            log.info(" [테스트 모드] SMS 발송 시뮬레이션 - 수신번호: {}, 인증번호: {}", phoneNumber, verificationCode);
            log.info(" 테스트용 고정 인증번호: {}", coolSmsProperties.getVerification().getTestCode());
            return true; // 테스트 모드에서는 항상 성공으로 처리
        }
        
        try {
            String content = createSmsContent(verificationCode, purpose);
            
            log.info("CoolSMS API 호출 시작 - 수신번호: {}, 발신번호: {}", phoneNumber, coolSmsProperties.getFromNumber());
            
            Message message = new Message();
            message.setFrom(coolSmsProperties.getFromNumber());
            message.setTo(phoneNumber);
            message.setText(content);

            SingleMessageSentResponse response = messageService.sendOne(
                    new SingleMessageSendingRequest(message)
            );

            log.info("SMS 발송 결과 - 수신번호: {}, 상태: {}, 메시지ID: {}, 상태메시지: {}", 
                    phoneNumber, response.getStatusCode(), response.getMessageId(), response.getStatusMessage());

            return "2000".equals(response.getStatusCode()); // 성공 코드

        } catch (Exception e) {
            log.error("SMS 발송 실패 - 수신번호: {}, 오류: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 개발 모드 확인
     */
    private boolean isDevelopmentMode() {
        // 실제 CoolSMS API 키를 사용하는 경우는 실제 SMS 발송
        // 기본값이나 테스트용 키인 경우만 개발 모드로 처리
        return coolSmsProperties.getApiKey().equals("NCSDNHZ4IMAPY5X9") || 
               coolSmsProperties.getApiKey().equals("test_api_key") ||
               coolSmsProperties.getFromNumber().equals("01012345678");
    }

    /**
     * SMS 내용 생성
     */
    private String createSmsContent(String verificationCode, String purpose) {
        String serviceName = getServiceName(purpose);
        return String.format(
                "[하나Future] %s 인증번호는 [%s]입니다. %d분 내에 입력해주세요.",
                serviceName,
                verificationCode,
                coolSmsProperties.getVerification().getExpiryMinutes()
        );
    }

    /**
     * 서비스명 반환
     */
    private String getServiceName(String purpose) {
        switch (purpose != null ? purpose : "") {
            case "MYDATA_AUTH":
                return "마이데이터 연동";
            case "SIGNUP":
                return "회원가입";
            case "PASSWORD_RESET":
                return "비밀번호 재설정";
            default:
                return "본인인증";
        }
    }

    /**
     * 인증번호 생성
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 만료된 인증 정보 정리
     */
    @Transactional
    public void cleanupExpiredVerifications() {
        phoneVerificationRepository.deleteExpiredVerifications(LocalDateTime.now());
    }
}
