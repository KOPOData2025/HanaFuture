package com.hanaTI.HanaFuture.domain.user.entity;

/**
 * 사용자 은행 계좌 상태
 */
public enum UserBankAccountStatus {
    
    /**
     * 활성 - 정상 사용 가능
     */
    ACTIVE("활성"),
    
    /**
     * 비활성 - 사용자가 비활성화
     */
    INACTIVE("비활성"),
    
    /**
     * 동기화 오류 - 은행 API 연동 실패
     */
    SYNC_ERROR("동기화 오류"),
    
    /**
     * 계좌 폐쇄 - 은행에서 계좌가 폐쇄됨
     */
    CLOSED("계좌 폐쇄"),
    
    /**
     * 연동 해제 - 사용자가 연동 해제
     */
    UNLINKED("연동 해제");
    
    private final String description;
    
    UserBankAccountStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
