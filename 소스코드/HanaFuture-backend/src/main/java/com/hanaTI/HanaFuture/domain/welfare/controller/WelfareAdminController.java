package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.welfare.service.WelfareBenefitService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Tag(name = "Welfare Admin", description = "복지 혜택 관리자 API")
@RestController
@RequestMapping("/api/welfare/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class WelfareAdminController {
    
    private final WelfareBenefitService welfareBenefitService;
    
    @Operation(
            summary = "중앙부처 복지서비스 동기화",
            description = "중앙부처 복지서비스 데이터를 동기화"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "동기화 시작됨"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @PostMapping("/sync/central")
    public ResponseEntity<ApiResponse<String>> syncCentralWelfareServices(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        // 비동기로 동기화 실행
        CompletableFuture.runAsync(() -> {
            try {
                log.info("중앙부처 복지서비스 동기화 시작 - 요청자: {}", userDetails.getUsername());
                welfareBenefitService.syncCentralWelfareServices();
                log.info("중앙부처 복지서비스 동기화 완료 - 요청자: {}", userDetails.getUsername());
            } catch (Exception e) {
                log.error("중앙부처 복지서비스 동기화 실패 - 요청자: {}", userDetails.getUsername(), e);
            }
        });
        
        return ResponseEntity.ok(ApiResponse.success("중앙부처 복지서비스 동기화가 시작되었습니다.", "SYNC_STARTED"));
    }
    
    @Operation(
            summary = "지자체 복지서비스 동기화",
            description = "특정 지역의 지자체 복지서비스 데이터를 동기화합니다. (관리자 전용)"
    )
    @PostMapping("/sync/local")
    public ResponseEntity<ApiResponse<String>> syncLocalWelfareServices(
            @Parameter(description = "시도코드", example = "11") @RequestParam String ctpvCd,
            @Parameter(description = "시군구코드", example = "11680") @RequestParam(required = false) String sggCd,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        // 비동기로 동기화 실행
        CompletableFuture.runAsync(() -> {
            try {
                log.info("지자체 복지서비스 동기화 시작 - 시도코드: {}, 시군구코드: {}, 요청자: {}", 
                        ctpvCd, sggCd, userDetails.getUsername());
                welfareBenefitService.syncLocalWelfareServices(ctpvCd, sggCd);
                log.info("지자체 복지서비스 동기화 완료 - 시도코드: {}, 시군구코드: {}, 요청자: {}", 
                        ctpvCd, sggCd, userDetails.getUsername());
            } catch (Exception e) {
                log.error("지자체 복지서비스 동기화 실패 - 시도코드: {}, 시군구코드: {}, 요청자: {}", 
                        ctpvCd, sggCd, userDetails.getUsername(), e);
            }
        });
        
        String region = sggCd != null ? ctpvCd + "/" + sggCd : ctpvCd;
        return ResponseEntity.ok(ApiResponse.success(
                region + " 지역의 지자체 복지서비스 동기화가 시작되었습니다.", "SYNC_STARTED"));
    }
    
    @Operation(
            summary = "전체 복지서비스 동기화",
            description = "중앙부처와 주요 지자체 복지서비스를 일괄 동기화합니다. (관리자 전용)"
    )
    @PostMapping("/sync/all")
    public ResponseEntity<ApiResponse<String>> syncAllWelfareServices(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        // 비동기로 전체 동기화 실행
        CompletableFuture.runAsync(() -> {
            try {
                log.info("전체 복지서비스 동기화 시작 - 요청자: {}", userDetails.getUsername());
                
                // 1. 중앙부처 동기화
                welfareBenefitService.syncCentralWelfareServices();
                
                // 2. 주요 지자체 동기화 (서울, 부산, 대구, 인천, 광주, 대전, 울산 등)
                String[] majorCities = {"11", "26", "27", "28", "29", "30", "31"};
                for (String cityCode : majorCities) {
                    try {
                        welfareBenefitService.syncLocalWelfareServices(cityCode, null);
                        Thread.sleep(1000); // API 호출 간격 조절
                    } catch (Exception e) {
                        log.error("지자체 동기화 실패 - 시도코드: {}", cityCode, e);
                    }
                }
                
                log.info("전체 복지서비스 동기화 완료 - 요청자: {}", userDetails.getUsername());
            } catch (Exception e) {
                log.error("전체 복지서비스 동기화 실패 - 요청자: {}", userDetails.getUsername(), e);
            }
        });
        
        return ResponseEntity.ok(ApiResponse.success("전체 복지서비스 동기화가 시작되었습니다.", "SYNC_STARTED"));
    }
    
    @Operation(
            summary = "기존 데이터 보강",
            description = "기존 복지 혜택 데이터의 NULL 값들을 보강합니다. (관리자 전용)"
    )
    @PostMapping("/enrich-data")
    public ResponseEntity<ApiResponse<String>> enrichExistingData(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        // 비동기로 데이터 보강 실행
        CompletableFuture.runAsync(() -> {
            try {
                log.info("기존 데이터 보강 작업 시작 - 요청자: {}", userDetails.getUsername());
                welfareBenefitService.enrichExistingData();
                log.info("기존 데이터 보강 작업 완료 - 요청자: {}", userDetails.getUsername());
            } catch (Exception e) {
                log.error("기존 데이터 보강 작업 실패 - 요청자: {}", userDetails.getUsername(), e);
            }
        });
        
        return ResponseEntity.ok(ApiResponse.success("기존 데이터 보강 작업이 시작되었습니다.", "ENRICHMENT_STARTED"));
    }
}
