package com.hanaTI.HanaFuture.domain.child.controller;

import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "Child", description = "자녀 관리 API")
@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
@Slf4j
public class ChildController {
    
    @Operation(
        summary = "사용자 자녀 목록 조회", 
        description = "사용자가 등록한 자녀 목록을 조회합니다."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserChildren(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("사용자 {}의 자녀 목록 조회", userId);
        
        try {
            List<Map<String, Object>> children = createMockChildrenData(userId);
            
            log.info("자녀 {}명 조회됨", children.size());
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", children));
                    
        } catch (Exception e) {
            log.error("자녀 목록 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀 목록 조회에 실패했습니다."));
        }
    }
    
    @Operation(
        summary = "자녀 등록", 
        description = "새로운 자녀를 등록합니다."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> registerChild(
            @RequestBody Map<String, Object> request) {
        
        log.info("자녀 등록 요청: {}", request);
        
        try {
            Map<String, Object> newChild = new HashMap<>();
            newChild.put("id", System.currentTimeMillis());
            newChild.put("userId", request.get("userId"));
            newChild.put("name", request.get("name"));
            newChild.put("birthDate", request.get("birthDate"));
            newChild.put("gender", request.get("gender"));
            newChild.put("relationship", request.get("relationship")); // 아들, 딸, 기타
            newChild.put("school", request.get("school"));
            newChild.put("grade", request.get("grade"));
            newChild.put("registeredAt", LocalDateTime.now().toString());
            newChild.put("isActive", true);
            
            // 나이 계산
            if (request.get("birthDate") != null) {
                LocalDate birthDate = LocalDate.parse(request.get("birthDate").toString());
                int age = LocalDate.now().getYear() - birthDate.getYear();
                newChild.put("age", age);
            }
            
            log.info("자녀 등록 완료: {}", newChild);
            
            return ResponseEntity.ok(ApiResponse.success("자녀가 등록되었습니다.", newChild));
                    
        } catch (Exception e) {
            log.error("자녀 등록 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀 등록에 실패했습니다."));
        }
    }
    
    @Operation(
            summary = "자녀 정보 수정",
        description = "등록된 자녀 정보를 수정합니다."
    )
    @PutMapping("/{childId}")
    public ResponseEntity<ApiResponse<Object>> updateChild(
            @Parameter(description = "자녀 ID") @PathVariable Long childId,
            @RequestBody Map<String, Object> request) {
        
        log.info("자녀 {} 정보 수정 요청: {}", childId, request);
        
        try {
            Map<String, Object> updatedChild = new HashMap<>();
            updatedChild.put("id", childId);
            updatedChild.put("userId", request.get("userId"));
            updatedChild.put("name", request.get("name"));
            updatedChild.put("birthDate", request.get("birthDate"));
            updatedChild.put("gender", request.get("gender"));
            updatedChild.put("relationship", request.get("relationship"));
            updatedChild.put("school", request.get("school"));
            updatedChild.put("grade", request.get("grade"));
            updatedChild.put("updatedAt", LocalDateTime.now().toString());
            
            // 나이 재계산
            if (request.get("birthDate") != null) {
                LocalDate birthDate = LocalDate.parse(request.get("birthDate").toString());
                int age = LocalDate.now().getYear() - birthDate.getYear();
                updatedChild.put("age", age);
            }
            
            log.info("자녀 정보 수정 완료: {}", updatedChild);
            
            return ResponseEntity.ok(ApiResponse.success("자녀 정보가 수정되었습니다.", updatedChild));
                    
        } catch (Exception e) {
            log.error("자녀 정보 수정 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀 정보 수정에 실패했습니다."));
        }
    }

    @Operation(
        summary = "자녀 삭제", 
        description = "등록된 자녀를 삭제합니다."
    )
    @DeleteMapping("/{childId}")
    public ResponseEntity<ApiResponse<Object>> deleteChild(
            @Parameter(description = "자녀 ID") @PathVariable Long childId) {
        
        log.info("자녀 {} 삭제 요청", childId);
        
        try {
            Map<String, Object> result = Map.of(
                "childId", childId,
                "deletedAt", LocalDateTime.now().toString()
            );
            
            log.info("자녀 삭제 완료: {}", result);
            
            return ResponseEntity.ok(ApiResponse.success("자녀가 삭제되었습니다.", result));
            
        } catch (Exception e) {
            log.error("자녀 삭제 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀 삭제에 실패했습니다."));
        }
    }

    @Operation(
        summary = "자녀 상세 조회", 
        description = "특정 자녀의 상세 정보를 조회합니다."
    )
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<Object>> getChildDetail(
            @Parameter(description = "자녀 ID") @PathVariable Long childId) {
        
        log.info("자녀 {} 상세 조회", childId);
        
        try {
            Map<String, Object> child = createMockChildDetail(childId);
            
            if (child.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("자녀를 찾을 수 없습니다."));
            }
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", child));
            
        } catch (Exception e) {
            log.error("자녀 상세 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀 상세 조회에 실패했습니다."));
        }
    }
    
    @Operation(
        summary = "자녀별 아이카드 목록 조회", 
        description = "특정 자녀가 보유한 아이카드 목록을 조회합니다."
    )
    @GetMapping("/{childId}/cards")
    public ResponseEntity<ApiResponse<Object>> getChildCards(
            @Parameter(description = "자녀 ID") @PathVariable Long childId) {
        
        log.info("자녀 {}의 아이카드 목록 조회", childId);
        
        try {
            List<Map<String, Object>> cards = createMockChildCards(childId);
            
            return ResponseEntity.ok(ApiResponse.success("조회 완료", cards));
                    
        } catch (Exception e) {
            log.error("자녀 아이카드 조회 실패", e);
            return ResponseEntity.ok(ApiResponse.error("자녀 아이카드 조회에 실패했습니다."));
        }
    }

    /**
     * Mock 자녀 데이터 생성 (실제로는 DB에서 조회)
     */
    private List<Map<String, Object>> createMockChildrenData(Long userId) {
        List<Map<String, Object>> children = new ArrayList<>();
        
        if (userId == 1L) {
            Map<String, Object> child1 = new HashMap<>();
            child1.put("id", 1L);
            child1.put("userId", userId);
            child1.put("name", "하나미");
            child1.put("birthDate", "2016-03-15");
            child1.put("age", 8);
            child1.put("gender", "FEMALE");
            child1.put("relationship", "딸");
            child1.put("school", "하나초등학교");
            child1.put("grade", 2);
            child1.put("registeredAt", "2024-01-10T09:00:00");
            child1.put("isActive", true);
            children.add(child1);
            
            Map<String, Object> child2 = new HashMap<>();
            child2.put("id", 2L);
            child2.put("userId", userId);
            child2.put("name", "하나준");
            child2.put("birthDate", "2012-08-22");
            child2.put("age", 12);
            child2.put("gender", "MALE");
            child2.put("relationship", "아들");
            child2.put("school", "하나중학교");
            child2.put("grade", 1);
            child2.put("registeredAt", "2024-01-10T09:00:00");
            child2.put("isActive", true);
            children.add(child2);
        }
        
        return children;
    }

    private Map<String, Object> createMockChildDetail(Long childId) {
        Map<String, Object> child = new HashMap<>();
        
        if (childId == 1L) {
            child.put("id", 1L);
            child.put("userId", 1L);
            child.put("name", "하나미");
            child.put("birthDate", "2016-03-15");
            child.put("age", 8);
            child.put("gender", "FEMALE");
            child.put("relationship", "딸");
            child.put("school", "하나초등학교");
            child.put("grade", 2);
            child.put("hobbies", Arrays.asList("그림그리기", "피아노"));
            child.put("allergies", Arrays.asList("견과류"));
            child.put("emergencyContact", "010-1234-5678");
            child.put("registeredAt", "2024-01-10T09:00:00");
            child.put("isActive", true);
        } else if (childId == 2L) {
            child.put("id", 2L);
            child.put("userId", 1L);
            child.put("name", "하나준");
            child.put("birthDate", "2012-08-22");
            child.put("age", 12);
            child.put("gender", "MALE");
            child.put("relationship", "아들");
            child.put("school", "하나중학교");
            child.put("grade", 1);
            child.put("hobbies", Arrays.asList("축구", "게임"));
            child.put("allergies", new ArrayList<>());
            child.put("emergencyContact", "010-1234-5678");
            child.put("registeredAt", "2024-01-10T09:00:00");
            child.put("isActive", true);
        }
        
        return child;
    }

    private List<Map<String, Object>> createMockChildCards(Long childId) {
        List<Map<String, Object>> cards = new ArrayList<>();
        
        if (childId == 1L) { // 하나미
            Map<String, Object> card = new HashMap<>();
            card.put("id", 1L);
            card.put("childId", childId);
            card.put("cardNumber", "5432-****-****-1234");
            card.put("monthlyLimit", 100000);
            card.put("balance", 45000);
            card.put("status", "ACTIVE");
            card.put("issuedAt", "2024-01-10T09:00:00");
            cards.add(card);
        } else if (childId == 2L) { // 하나준
            Map<String, Object> card = new HashMap<>();
            card.put("id", 2L);
            card.put("childId", childId);
            card.put("cardNumber", "5432-****-****-5678");
            card.put("monthlyLimit", 150000);
            card.put("balance", 78000);
            card.put("status", "ACTIVE");
            card.put("issuedAt", "2024-02-05T11:00:00");
            cards.add(card);
        }
        
        return cards;
    }
}