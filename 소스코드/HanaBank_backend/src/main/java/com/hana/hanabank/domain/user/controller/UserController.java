package com.hana.hanabank.domain.user.controller;

import com.hana.hanabank.domain.user.entity.User;
import com.hana.hanabank.domain.user.service.UserService;
import com.hana.hanabank.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "테스트용 사용자 생성", description = "테스트를 위한 사용자 데이터를 생성합니다.")
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<User>> createTestUser(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String userCi = (String) request.get("userCi");
            String userNum = (String) request.get("userNum");
            String username = (String) request.get("username");
            String phoneNumber = (String) request.get("phoneNumber");
            String email = (String) request.get("email");
            
            User user = userService.createTestUser(userId, userCi, userNum, username, phoneNumber, email);
            
            log.info("테스트 사용자 생성 성공: {}", username);
            return ResponseEntity.ok(ApiResponse.success("테스트 사용자가 생성되었습니다.", user));
            
        } catch (Exception e) {
            log.error("테스트 사용자 생성 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("테스트 사용자 생성에 실패했습니다."));
        }
    }
    
    @Operation(summary = "사용자 조회", description = "CI로 사용자를 조회합니다.")
    @GetMapping("/ci/{userCi}")
    public ResponseEntity<ApiResponse<User>> getUserByCi(@PathVariable String userCi) {
        try {
            User user = userService.getUserByCi(userCi);
            if (user == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
            }
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            log.error("사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("사용자 조회에 실패했습니다."));
        }
    }
}














