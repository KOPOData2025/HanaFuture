package com.hanaTI.HanaFuture.domain.asset.controller;

import com.hanaTI.HanaFuture.domain.asset.dto.*;
import com.hanaTI.HanaFuture.domain.asset.service.AssetIntegrationService;
import com.hanaTI.HanaFuture.global.common.ApiResponse;
import com.hanaTI.HanaFuture.global.security.CurrentUser;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Asset Integration", description = "자산 연동 관리 API")
public class AssetIntegrationController {

    private final AssetIntegrationService assetIntegrationService;

    @Operation(summary = "지원 은행 목록 조회", description = "연동 가능한 은행 목록을 조회합니다.")
    @GetMapping("/supported-banks")
    public ApiResponse<List<java.util.Map<String, String>>> getSupportedBanks() {
        // 하나은행만 지원
        List<java.util.Map<String, String>> banks = List.of(
            java.util.Map.of(
                "bankCode", "081",
                "bankName", "하나은행",
                "website", "https://www.kebhana.com"
            )
        );
        return ApiResponse.success(banks);
    }

    @Operation(summary = "내 자산 목록 조회", description = "사용자가 연결한 자산 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<ConnectedAssetResponse>> getMyAssets(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        List<ConnectedAssetResponse> assets = assetIntegrationService.getUserAssets(currentUser.getId());
        return ApiResponse.success(assets);
    }

    @Operation(summary = "자산 요약 정보 조회", description = "사용자의 전체 자산 요약 정보를 조회합니다.")
    @GetMapping("/summary")
    public ApiResponse<AssetSummaryResponse> getAssetSummary(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        AssetSummaryResponse summary = assetIntegrationService.getAssetSummary(currentUser.getId());
        return ApiResponse.success(summary);
    }

    @Operation(summary = "새 계좌 연결", description = "새로운 은행 계좌를 연결합니다.")
    @PostMapping("/connect")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ConnectedAssetResponse> connectAsset(
            @Valid @RequestBody ConnectAssetRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        ConnectedAssetResponse asset = assetIntegrationService.connectAsset(request, currentUser.getId());
        return ApiResponse.success(asset);
    }

    @Operation(summary = "자산 동기화", description = "특정 자산의 정보를 동기화합니다.")
    @PostMapping("/{assetId}/sync")
    public ApiResponse<Void> syncAsset(
            @PathVariable Long assetId,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        assetIntegrationService.syncAsset(assetId, currentUser.getId());
        return ApiResponse.success();
    }

    @Operation(summary = "전체 자산 동기화", description = "사용자의 모든 자산을 동기화합니다.")
    @PostMapping("/sync-all")
    public ApiResponse<Void> syncAllAssets(
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        assetIntegrationService.syncAllAssets(currentUser.getId());
        return ApiResponse.success();
    }

    @Operation(summary = "자산 설정 업데이트", description = "자산의 설정을 업데이트합니다.")
    @PutMapping("/{assetId}/settings")
    public ApiResponse<ConnectedAssetResponse> updateAssetSettings(
            @PathVariable Long assetId,
            @Valid @RequestBody UpdateAssetSettingsRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        ConnectedAssetResponse asset = assetIntegrationService.updateAssetSettings(
                assetId, request, currentUser.getId());
        return ApiResponse.success(asset);
    }

    @Operation(summary = "자산 연결 해제", description = "연결된 자산을 해제합니다.")
    @DeleteMapping("/{assetId}")
    public ApiResponse<Void> disconnectAsset(
            @PathVariable Long assetId,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        
        assetIntegrationService.disconnectAsset(assetId, currentUser.getId());
        return ApiResponse.success();
    }

    @Operation(summary = "자산 거래내역 조회", description = "특정 자산의 거래내역을 조회합니다.")
    @GetMapping("/{assetId}/transactions")
    public ApiResponse<Page<ConnectedAssetResponse.AssetTransactionSummary>> getAssetTransactions(
            @PathVariable Long assetId,
            @Parameter(hidden = true) @CurrentUser User currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ConnectedAssetResponse.AssetTransactionSummary> transactions = 
                assetIntegrationService.getAssetTransactions(assetId, currentUser.getId(), pageable);
        
        return ApiResponse.success(transactions);
    }
}
