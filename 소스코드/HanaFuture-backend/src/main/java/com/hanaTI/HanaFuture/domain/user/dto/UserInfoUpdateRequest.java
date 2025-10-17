package com.hanaTI.HanaFuture.domain.user.dto;

import com.hanaTI.HanaFuture.domain.user.entity.Gender;
import com.hanaTI.HanaFuture.domain.user.entity.EmploymentStatus;
import com.hanaTI.HanaFuture.domain.user.entity.MaritalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserInfoUpdateRequest {
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
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
}




















