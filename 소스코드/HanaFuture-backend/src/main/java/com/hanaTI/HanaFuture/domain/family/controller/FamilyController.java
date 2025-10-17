package com.hanaTI.HanaFuture.domain.family.controller;

import com.hanaTI.HanaFuture.domain.family.dto.FamilyMemberRequest;
import com.hanaTI.HanaFuture.domain.family.dto.FamilyMemberResponse;
import com.hanaTI.HanaFuture.domain.family.service.FamilyService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Family", description = "가족 관리 API")
@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
@Slf4j
public class FamilyController {

    private final FamilyService familyService;

    @Operation(summary = "가족 멤버 추가", description = "새로운 가족 멤버를 추가합니다.")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse<FamilyMemberResponse>> addFamilyMember(
            @RequestParam Long userId,
            @Valid @RequestBody FamilyMemberRequest request) {

        log.info("‍‍‍ 가족 멤버 추가 요청 - 사용자 ID: {}, 멤버 이름: {}", userId, request.getName());

        FamilyMemberResponse response = familyService.addFamilyMember(userId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "내 가족 목록 조회", description = "로그인한 사용자의 가족 목록을 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<FamilyMemberResponse>>> getMyFamily(
            @RequestParam Long userId) {

        log.info("‍‍‍ 가족 목록 조회 - 사용자 ID: {}", userId);

        List<FamilyMemberResponse> familyMembers = familyService.getMyFamily(userId);

        return ResponseEntity.ok(ApiResponse.success(familyMembers));
    }

    @Operation(summary = "가족 멤버 상세 조회", description = "특정 가족 멤버의 상세 정보를 조회합니다.")
    @GetMapping("/members/{familyMemberId}")
    public ResponseEntity<ApiResponse<FamilyMemberResponse>> getFamilyMember(
            @RequestParam Long userId,
            @PathVariable Long familyMemberId) {

        log.info("‍‍‍ 가족 멤버 상세 조회 - 사용자 ID: {}, 멤버 ID: {}", userId, familyMemberId);

        FamilyMemberResponse response = familyService.getFamilyMember(userId, familyMemberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가족 멤버 정보 수정", description = "가족 멤버의 정보를 수정합니다.")
    @PutMapping("/members/{familyMemberId}")
    public ResponseEntity<ApiResponse<FamilyMemberResponse>> updateFamilyMember(
            @RequestParam Long userId,
            @PathVariable Long familyMemberId,
            @Valid @RequestBody FamilyMemberRequest request) {

        log.info(" 가족 멤버 정보 수정 - 사용자 ID: {}, 멤버 ID: {}", userId, familyMemberId);

        FamilyMemberResponse response = familyService.updateFamilyMember(userId, familyMemberId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가족 멤버 삭제", description = "가족 멤버를 삭제합니다.")
    @DeleteMapping("/members/{familyMemberId}")
    public ResponseEntity<ApiResponse<Void>> deleteFamilyMember(
            @RequestParam Long userId,
            @PathVariable Long familyMemberId) {

        log.info(" 가족 멤버 삭제 - 사용자 ID: {}, 멤버 ID: {}", userId, familyMemberId);

        familyService.deleteFamilyMember(userId, familyMemberId);

        return ResponseEntity.ok(ApiResponse.success("가족 멤버가 삭제되었습니다."));
    }

    @Operation(summary = "가족 초대 수락", description = "가족 초대를 수락합니다.")
    @PostMapping("/members/{familyMemberId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFamilyInvite(
            @RequestParam Long userId,
            @PathVariable Long familyMemberId) {

        log.info("가족 초대 수락 - 사용자 ID: {}, 멤버 ID: {}", userId, familyMemberId);

        familyService.acceptFamilyInvite(userId, familyMemberId);

        return ResponseEntity.ok(ApiResponse.success("가족 초대를 수락했습니다."));
    }
}

