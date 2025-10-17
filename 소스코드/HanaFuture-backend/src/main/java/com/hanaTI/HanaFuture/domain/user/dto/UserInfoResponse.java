package com.hanaTI.HanaFuture.domain.user.dto;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.entity.Gender;
import com.hanaTI.HanaFuture.domain.user.entity.EmploymentStatus;
import com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserInfoResponse {
    
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    
    // 개인 정보
    private LocalDateTime birthDate;
    private Gender gender;
    private String residenceSido;
    private String residenceSigungu;
    private Long income;
    private EmploymentStatus employmentStatus;
    private MaritalStatus maritalStatus;
    
    // 가족 정보
    private Boolean hasChildren;
    private Integer numberOfChildren;
    private List<Integer> childrenAges;
    
    // 관심사
    private List<String> interests;
    
    // 메타 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .residenceSido(user.getResidenceSido())
                .residenceSigungu(user.getResidenceSigungu())
                .income(user.getIncome())
                .employmentStatus(user.getEmploymentStatus())
                .maritalStatus(user.getMaritalStatus())
                .hasChildren(user.getHasChildren())
                .numberOfChildren(user.getNumberOfChildren())
                .childrenAges(user.getChildrenAges())
                .interests(user.getInterests())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}




















