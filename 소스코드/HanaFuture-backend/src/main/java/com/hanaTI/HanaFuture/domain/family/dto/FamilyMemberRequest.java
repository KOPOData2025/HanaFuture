package com.hanaTI.HanaFuture.domain.family.dto;

import com.hanaTI.HanaFuture.domain.family.entity.FamilyRelationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMemberRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    private String phoneNumber;

    private FamilyRelationType relationType; // Optional - 기본값 OTHER 사용

    private LocalDateTime birthDate;

    private String memo;
}

