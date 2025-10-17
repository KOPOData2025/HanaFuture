package com.hanaTI.HanaFuture.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "C003", "엔티티를 찾을 수 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "서버 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C006", "잘못된 타입 값입니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C007", "접근이 거부되었습니다."),

    // 인증 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH000", "인증이 필요합니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH001", "인증에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "만료된 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH004", "접근 권한이 없습니다."),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "AUTH005", "권한이 부족합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH008", "유효하지 않은 리프레시 토큰입니다."),
    
    // 회원가입/로그인 관련 에러
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH006", "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH007", "이메일 또는 비밀번호가 올바르지 않습니다."),
    
    // 휴대폰 인증 관련 에러
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "VERIFY001", "인증번호가 올바르지 않습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "VERIFY002", "인증번호가 만료되었습니다."),
    VERIFICATION_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VERIFY003", "인증번호 발송에 실패했습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "잘못된 비밀번호입니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U004", "이미 존재하는 사용자입니다."),

    // 모임통장 관련 에러
    GROUP_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "모임통장을 찾을 수 없습니다."),
    ALREADY_GROUP_MEMBER(HttpStatus.CONFLICT, "G002", "이미 모임통장의 멤버입니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "G003", "잔액이 부족합니다."),
    INVALID_TRANSACTION_AMOUNT(HttpStatus.BAD_REQUEST, "G004", "유효하지 않은 거래 금액입니다."),
    DUPLICATE_REQUEST(HttpStatus.CONFLICT, "G005", "중복된 요청입니다."),
    INVALID_GROUP_ACCOUNT_PASSWORD(HttpStatus.BAD_REQUEST, "G006", "모임통장 비밀번호가 일치하지 않습니다."),

    // 자산 연동 관련 에러
    ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "AS001", "자산을 찾을 수 없습니다."),
    ASSET_ALREADY_CONNECTED(HttpStatus.CONFLICT, "AS002", "이미 연결된 자산입니다."),
    ASSET_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "AS003", "자산 연결에 실패했습니다."),
    ASSET_NOT_CONNECTED(HttpStatus.BAD_REQUEST, "AS004", "연결되지 않은 자산입니다."),
    ASSET_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AS005", "자산 동기화에 실패했습니다."),

    // 복지혜택 관련 에러
    WELFARE_BENEFIT_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "복지혜택을 찾을 수 없습니다."),
    WELFARE_APPLICATION_FAILED(HttpStatus.BAD_REQUEST, "W002", "복지혜택 신청에 실패했습니다."),

    // 즐겨찾기 관련 에러
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BM001", "즐겨찾기를 찾을 수 없습니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BM002", "이미 즐겨찾기에 추가된 항목입니다."),
    BOOKMARK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BM003", "즐겨찾기에 대한 접근 권한이 없습니다."),

    // 은행 계좌 관련 에러
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "BA001", "계좌를 찾을 수 없습니다."),
    ACCOUNT_ALREADY_LINKED(HttpStatus.CONFLICT, "BA002", "이미 연동된 계좌입니다."),
    INVALID_ACCOUNT(HttpStatus.BAD_REQUEST, "BA003", "유효하지 않은 계좌입니다."),
    ACCOUNT_VALIDATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BA004", "계좌 유효성 검증에 실패했습니다."),
    ACCOUNT_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BA005", "계좌 동기화에 실패했습니다."),
    ACCOUNT_HAS_BALANCE(HttpStatus.BAD_REQUEST, "BA006", "계좌에 잔액이 있어 해지할 수 없습니다."),

    // 외부 API 관련 에러
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "외부 API 호출에 실패했습니다."),
    BANKING_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "은행 API 호출에 실패했습니다."),
    WELFARE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E003", "복지 API 호출에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}