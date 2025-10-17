package com.hanaTI.HanaFuture.domain.savings.controller;

import com.hanaTI.HanaFuture.domain.savings.dto.SavingGoalRequest;
import com.hanaTI.HanaFuture.domain.savings.dto.SavingGoalResponse;
import com.hanaTI.HanaFuture.domain.savings.service.SavingsService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Savings", description = "함께 적금 관련 API")
@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
@Slf4j
public class SavingsController {
    
    private final SavingsService savingsService;
    
    @Operation(
        summary = "사용자 적금 목록 조회", 
        description = "사용자가 가입한 함께 적금 목록을 조회합니다."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<SavingGoalResponse>>> getUserSavings(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("사용자 {}의 함께 적금 목록 조회", userId);
        
        try {
            List<SavingGoalResponse> savings = savingsService.getUserSavingGoals(userId);
            
            log.info("함께 적금 {}개 조회됨", savings.size());
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", savings));
                    
        } catch (Exception e) {
            log.error("함께 적금 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("함께 적금 조회에 실패했습니다."));
        }
    }
    
    @Operation(
        summary = "함께 적금 생성", 
        description = "새로운 함께 적금을 생성합니다."
    )
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SavingGoalResponse>> createSavings(
            @RequestBody SavingGoalRequest request) {
        
        log.info("함께 적금 생성 요청: 사용자 ID {}, 목표명 {}", 
                request.getUserId(), request.getGoalName());
        
        try {
            SavingGoalResponse response = savingsService.createSavingGoal(request);
            
            log.info("함께 적금 생성 완료: ID {}", response.getId());
            
            return ResponseEntity.ok(ApiResponse.success("함께 적금이 생성되었습니다.", response));
                    
        } catch (Exception e) {
            log.error("함께 적금 생성 실패", e);
            return ResponseEntity.ok(ApiResponse.error("함께 적금 생성에 실패했습니다."));
        }
    }
    
    @Operation(
        summary = "적금 목표 조회", 
        description = "사용자의 적금 목표 목록을 조회합니다."
    )
    @GetMapping("/goals")
    public ResponseEntity<ApiResponse<List<SavingGoalResponse>>> getSavingGoals(
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        
        log.info("사용자 {}의 적금 목표 조회", userId);
        
        try {
            List<SavingGoalResponse> goals = savingsService.getUserSavingGoals(userId);
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", goals));
                    
        } catch (Exception e) {
            log.error("적금 목표 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("적금 목표 조회에 실패했습니다."));
        }
    }
}