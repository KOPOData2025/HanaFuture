package com.hanafuture.openbanking.global.util;

import java.security.SecureRandom;
import java.util.UUID;

public class UserGeneratorUtil {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * 대문자 1개 + 숫자 9개로 구성된 10글자 user_seq_no 생성
     */
    public static String generateUserSeqNo() {
        StringBuilder sb = new StringBuilder();
        
        // 첫 번째 자리: 대문자 1개
        int upperIndex = SECURE_RANDOM.nextInt(UPPERCASE_LETTERS.length());
        sb.append(UPPERCASE_LETTERS.charAt(upperIndex));
        
        // 나머지 9자리: 숫자
        for (int i = 0; i < 9; i++) {
            int numberIndex = SECURE_RANDOM.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(numberIndex));
        }
        
        return sb.toString();
    }
    
    /**
     * 고유한 UUID 생성 (code용)
     */
    public static String generateUserCode() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 고유한 UUID 생성 (auth_code용) - 하이픈 제거
     */
    public static String generateAuthCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * fintech_use_num 생성 (대문자 + 숫자 16자리)
     */
    public static String generateFintechUseNum() {
        StringBuilder sb = new StringBuilder();
        
        // 첫 4자리: 대문자
        for (int i = 0; i < 4; i++) {
            int upperIndex = SECURE_RANDOM.nextInt(UPPERCASE_LETTERS.length());
            sb.append(UPPERCASE_LETTERS.charAt(upperIndex));
        }
        
        // 나머지 12자리: 숫자
        for (int i = 0; i < 12; i++) {
            int numberIndex = SECURE_RANDOM.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(numberIndex));
        }
        
        return sb.toString();
    }
    
    /**
     * 주민등록번호 유효성 검사
     */
    public static boolean isValidResidentNumber(String residentNumber) {
        if (residentNumber == null || residentNumber.length() != 13) {
            return false;
        }
        
        // 숫자로만 구성되어야 함
        return residentNumber.matches("\\d{13}");
    }
} 