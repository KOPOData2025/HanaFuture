package com.hanaTI.HanaFuture.domain.child.dto;

import com.hanaTI.HanaFuture.domain.child.entity.AllowanceCycle;
import com.hanaTI.HanaFuture.domain.child.entity.ChildGender;
import com.hanaTI.HanaFuture.domain.child.entity.ChildStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "자녀 정보 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildResponse {
    
    @Schema(description = "자녀 ID", example = "1")
    private Long id;
    
    @Schema(description = "자녀 이름", example = "김철수")
    private String name;
    
    @Schema(description = "자녀 생년월일", example = "2015-03-15")
    private LocalDate birthDate;
    
    @Schema(description = "자녀 나이", example = "9")
    private Integer age;
    
    @Schema(description = "자녀 성별", example = "MALE")
    private ChildGender gender;
    
    @Schema(description = "학교명", example = "서울초등학교")
    private String schoolName;
    
    @Schema(description = "학년", example = "3")
    private Integer grade;
    
    @Schema(description = "반", example = "2")
    private Integer classNumber;
    
    @Schema(description = "학급 정보", example = "3학년 2반")
    private String classInfo;
    
    @Schema(description = "부모 사용자 ID", example = "1")
    private Long parentUserId;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
    
    @Schema(description = "닉네임", example = "철수")
    private String nickname;
    
    @Schema(description = "주간 용돈", example = "10000")
    private BigDecimal weeklyAllowance;
    
    @Schema(description = "월간 용돈", example = "50000")
    private BigDecimal monthlyAllowance;
    
    @Schema(description = "용돈 지급 주기", example = "WEEKLY")
    private AllowanceCycle allowanceCycle;
    
    @Schema(description = "자녀 상태", example = "ACTIVE")
    private ChildStatus status;
    
    @Schema(description = "금융 교육 레벨", example = "1")
    private Integer financialEducationLevel;
    
    @Schema(description = "적립 포인트", example = "1500")
    private BigDecimal rewardPoints;
    
    @Schema(description = "등록일", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일", example = "2024-01-20T14:20:00")
    private LocalDateTime updatedAt;
}










