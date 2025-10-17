package com.hanaTI.HanaFuture.domain.welfare.controller;

import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitDetailResponse;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareSearchRequest;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareType;
import com.hanaTI.HanaFuture.domain.welfare.service.WelfareBenefitService;
import com.hanaTI.HanaFuture.domain.welfare.service.WelfareMemoryCacheService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Welfare", description = "복지 혜택 관련 API")
@RestController
@RequestMapping("/api/welfare")
@RequiredArgsConstructor
public class WelfareController {
    
    private final WelfareBenefitService welfareBenefitService;
    private final WelfareMemoryCacheService welfareCacheService;
    
    @Operation(
            summary = "복지 혜택 검색",
            description = "다양한 조건으로 복지 혜택을 검색합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            )
    })
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> searchWelfareBenefits(
            @RequestBody WelfareSearchRequest request) {
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.searchBenefits(request);
        return ResponseEntity.ok(ApiResponse.success("복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "복지 혜택 상세 조회",
            description = "특정 복지 혜택의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WelfareBenefitDetailResponse>> getWelfareBenefitDetail(
            @Parameter(description = "복지 혜택 ID") @PathVariable Long id) {
        
        WelfareBenefitDetailResponse benefit = welfareBenefitService.getBenefitDetail(id);
        return ResponseEntity.ok(ApiResponse.success("복지 혜택 상세 정보를 조회했습니다.", benefit));
    }

    @Operation(
            summary = "복지 혜택 즐겨찾기 토글",
            description = "복지 혜택의 즐겨찾기 상태를 토글합니다."
    )
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Boolean>> toggleBookmark(
            @Parameter(description = "복지 혜택 ID") @PathVariable Long id) {
        
        boolean isBookmarked = welfareBenefitService.toggleBookmark(id);
        return ResponseEntity.ok(ApiResponse.success("즐겨찾기 상태가 변경되었습니다.", isBookmarked));
    }
    
    @Operation(
            summary = "전체 복지 혜택 조회",
            description = "모든 복지 혜택을 페이지네이션으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getAllWelfareBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향", example = "desc") @RequestParam(defaultValue = "desc") String direction) {
        
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSort(sort);
        request.setDirection(direction);
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.searchBenefits(request);
        return ResponseEntity.ok(ApiResponse.success("전체 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "서비스 유형별 복지 혜택 조회",
            description = "중앙정부 또는 지자체 복지 혜택을 조회합니다."
    )
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getWelfareBenefitsByType(
            @Parameter(description = "서비스 유형", example = "CENTRAL") @PathVariable WelfareType serviceType,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setServiceType(serviceType);
        request.setPage(page);
        request.setSize(size);
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.searchBenefits(request);
        return ResponseEntity.ok(ApiResponse.success(
                serviceType.getDisplayName() + " 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "지역별 복지 혜택 조회",
            description = "특정 지역의 복지 혜택을 조회합니다."
    )
    @GetMapping("/region")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getWelfareBenefitsByRegion(
            @Parameter(description = "시도명", example = "서울특별시") @RequestParam String sidoName,
            @Parameter(description = "시군구명") @RequestParam(required = false) String sigunguName,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setServiceType(WelfareType.LOCAL); // 지역별 조회는 지자체만
        request.setSidoName(sidoName);
        request.setSigunguName(sigunguName);
        request.setPage(page);
        request.setSize(size);
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.searchBenefits(request);
        String regionName = sigunguName != null ? sidoName + " " + sigunguName : sidoName;
        return ResponseEntity.ok(ApiResponse.success(regionName + " 지역의 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "생애주기별 복지 혜택 조회",
            description = "특정 생애주기의 복지 혜택을 조회합니다."
    )
    @GetMapping("/lifecycle/{lifeCycle}")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getWelfareBenefitsByLifeCycle(
            @Parameter(description = "생애주기", example = "임신출산") @PathVariable String lifeCycle,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setLifeCycle(lifeCycle);
        request.setPage(page);
        request.setSize(size);
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.searchBenefits(request);
        return ResponseEntity.ok(ApiResponse.success(lifeCycle + " 관련 복지 혜택을 조회했습니다.", benefits));
    }
    
    
    @Operation(
            summary = "키워드로 복지 혜택 검색",
            description = "키워드로 복지 혜택을 검색합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> searchWelfareBenefitsByKeyword(
            @Parameter(description = "검색 키워드", example = "출산") @RequestParam String keyword,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        WelfareSearchRequest request = new WelfareSearchRequest();
        request.setKeyword(keyword);
        request.setPage(page);
        request.setSize(size);
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.searchBenefits(request);
        return ResponseEntity.ok(ApiResponse.success("'" + keyword + "' 검색 결과입니다.", benefits));
    }
    
    @Operation(
            summary = "지원금액 순 복지 혜택 조회",
            description = "지원금액이 높은 순으로 복지 혜택을 조회합니다."
    )
    @GetMapping("/by-amount")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getWelfareBenefitsByAmount(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.getBenefitsByAmount(page, size);
        return ResponseEntity.ok(ApiResponse.success("지원금액 순 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "최근 업데이트된 복지 혜택 조회",
            description = "최근에 업데이트된 복지 혜택을 조회합니다."
    )
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getRecentWelfareBenefits(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = welfareBenefitService.getRecentBenefits(page, size);
        return ResponseEntity.ok(ApiResponse.success("최근 업데이트된 복지 혜택을 조회했습니다.", benefits));
    }
    
    @Operation(
            summary = "하나퓨처 맞춤 복지 혜택 빠른 조회 (메모리 캐싱)",
            description = "하나퓨처가 추천하는 131개 맞춤 복지 혜택을 메모리에서 빠르게 조회합니다. DB 조회 없이 빠른 응답을 제공합니다."
    )
    @GetMapping("/hana-future/fast")
    public ResponseEntity<ApiResponse<Page<WelfareBenefitResponse>>> getHanaFutureBenefitsFast(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        Page<WelfareBenefitResponse> benefits = welfareCacheService.getFastPaging(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                "하나퓨처 맞춤 복지 혜택을 조회했습니다. (총 " + welfareCacheService.getCachedCount() + "개)", 
                benefits));
    }
}
