package com.hanaTI.HanaFuture.global.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CI(고객식별정보) 생성 유틸리티
 * 주민등록번호를 SHA-256으로 해시하여 CI를 생성
 */
@Component
public class CIGenerator {
    
    /**
     * 주민등록번호로부터 CI를 생성
     * 
     * @param residentNumber 주민등록번호
     * @return SHA-256 해시된 CI 값
     */
    public static String generateCI(String residentNumber) {
        if (residentNumber == null || residentNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("주민등록번호는 필수입니다.");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(residentNumber.getBytes(StandardCharsets.UTF_8));
            
            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
    
    /**
     * CI 값이 유효한지 검증
     * @param ci CI 값
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValidCI(String ci) {
        if (ci == null || ci.trim().isEmpty()) {
            return false;
        }
        
        // SHA-256 해시는 64자리 16진수 문자열
        return ci.length() == 64 && ci.matches("^[a-fA-F0-9]+$");
    }
    
    /**
     * 주민등록번호 형식 검증
     * 
     * @param residentNumber 주민등록번호
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValidResidentNumber(String residentNumber) {
        if (residentNumber == null || residentNumber.trim().isEmpty()) {
            return false;
        }
        
        // 13자리 숫자 또는 6자리-7자리 형식
        String cleaned = residentNumber.replace("-", "");
        return cleaned.length() == 13 && cleaned.matches("^[0-9]+$");
    }
}








