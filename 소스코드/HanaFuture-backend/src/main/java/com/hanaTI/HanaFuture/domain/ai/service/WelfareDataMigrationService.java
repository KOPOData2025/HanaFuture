package com.hanaTI.HanaFuture.domain.ai.service;

import com.hanaTI.HanaFuture.domain.welfare.entity.HanaFutureWelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.entity.WelfareBenefit;
import com.hanaTI.HanaFuture.domain.welfare.repository.HanaFutureWelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.repository.WelfareBenefitRepository;
import com.hanaTI.HanaFuture.domain.welfare.dto.WelfareBenefitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 필터링된 복지 데이터를 하나퓨처 전용 테이블로 이관하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareDataMigrationService {

    private final WelfareBenefitRepository originalRepository;
    private final HanaFutureWelfareBenefitRepository hanaFutureRepository;
    private final AIWelfareFilterService aiWelfareFilterService;

    /**
     * AI 필터링된 데이터를 하나퓨처 테이블로 이관
     */
    public void migrateAIFilteredData() {
        log.info(" AI 필터링된 복지 데이터 이관 시작...");

        try {
            // 1. 기존 하나퓨처 테이블 데이터 확인
            long existingCount = hanaFutureRepository.count();
            if (existingCount > 0) {
                log.info("기존 하나퓨처 복지 데이터 {}개 존재. 삭제 후 재생성합니다.", existingCount);
                deleteAllHanaFutureData();
            }

            // 2. 원본 데이터 조회 (활성 상태만)
            List<WelfareBenefit> allOriginalBenefits = originalRepository.findAll().stream()
                    .filter(benefit -> benefit.getIsActive() != null && benefit.getIsActive())
                    .collect(Collectors.toList());
            log.info(" 원본 복지 데이터: {}개", allOriginalBenefits.size());

            if (allOriginalBenefits.isEmpty()) {
                log.warn("원본 복지 데이터가 없습니다. 이관을 중단합니다.");
                return;
            }

            // 3. AI 필터링 실행
            List<WelfareBenefitResponse> originalResponses = allOriginalBenefits.stream()
                    .map(WelfareBenefitResponse::from)
                    .collect(Collectors.toList());

            log.info(" AI 필터링 시작...");
            List<WelfareBenefitResponse> filteredResponses = aiWelfareFilterService
                    .filterWelfareBenefitsWithAI(originalResponses);

            log.info("AI 필터링 완료: {}개 → {}개", allOriginalBenefits.size(), filteredResponses.size());

            // 4. 필터링된 데이터를 하나퓨처 테이블에 저장 (트랜잭션 분리)
            int savedCount = saveBenefitsInTransaction(allOriginalBenefits, filteredResponses);

            // 5. 이관 결과 요약
            long finalCount = hanaFutureRepository.countByIsActiveTrue();
            log.info(" 데이터 이관 완료!");
            log.info(" 이관 결과:");
            log.info("   - 원본 데이터: {}개", allOriginalBenefits.size());
            log.info("   - AI 필터링 결과: {}개", filteredResponses.size());
            log.info("   - 실제 저장: {}개", savedCount);
            log.info("   - 하나퓨처 테이블 최종: {}개", finalCount);

            // 6. 서비스 타입별 통계
            long centralCount = hanaFutureRepository.countByServiceTypeAndIsActiveTrue("CENTRAL");
            long localCount = hanaFutureRepository.countByServiceTypeAndIsActiveTrue("LOCAL");
            log.info(" 서비스 타입별 통계:");
            log.info("   - 중앙정부: {}개", centralCount);
            log.info("   - 지자체: {}개", localCount);

        } catch (Exception e) {
            log.error("데이터 이관 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("데이터 이관 실패", e);
        }
    }

    /**
     * 데이터 삭제 (트랜잭션 분리)
     */
    @Transactional
    private void deleteAllHanaFutureData() {
        hanaFutureRepository.deleteAll();
    }

    /**
     * 필터링된 데이터 저장 (트랜잭션 분리)
     */
    @Transactional
    private int saveBenefitsInTransaction(List<WelfareBenefit> allOriginalBenefits, 
                                          List<WelfareBenefitResponse> filteredResponses) {
        int savedCount = 0;

        for (WelfareBenefitResponse filteredResponse : filteredResponses) {
            try {
                // 원본 데이터 찾기
                WelfareBenefit originalBenefit = allOriginalBenefits.stream()
                        .filter(b -> b.getId().equals(filteredResponse.getId()))
                        .findFirst()
                        .orElse(null);

                if (originalBenefit == null) {
                    log.warn("원본 데이터를 찾을 수 없습니다: ID {}", filteredResponse.getId());
                    continue;
                }

                // 중복 확인
                if (hanaFutureRepository.existsByOriginalWelfareId(originalBenefit.getId())) {
                    log.debug("⏭ 이미 존재하는 데이터: {}", originalBenefit.getServiceName());
                    continue;
                }

                // 하나퓨처 복지 데이터 생성
                HanaFutureWelfareBenefit hanaFutureBenefit = HanaFutureWelfareBenefit
                        .fromOriginal(originalBenefit, "AI 필터링으로 선별된 하나퓨처 맞춤 혜택");

                // AI 관련도 점수 설정 (임시로 1.0, 추후 개선 가능)
                hanaFutureBenefit.updateRelevanceScore(BigDecimal.valueOf(1.00));

                // 저장
                hanaFutureRepository.save(hanaFutureBenefit);
                savedCount++;

                if (savedCount % 10 == 0) {
                    log.info(" 저장 진행률: {}/{}", savedCount, filteredResponses.size());
                }

            } catch (Exception e) {
                log.error("데이터 저장 실패: {} - {}", filteredResponse.getServiceName(), e.getMessage());
            }
        }

        return savedCount;
    }

    /**
     * 하나퓨처 테이블 통계 조회
     */
    @Transactional(readOnly = true)
    public void printHanaFutureWelfareStats() {
        long totalCount = hanaFutureRepository.countByIsActiveTrue();
        long centralCount = hanaFutureRepository.countByServiceTypeAndIsActiveTrue("CENTRAL");
        long localCount = hanaFutureRepository.countByServiceTypeAndIsActiveTrue("LOCAL");

        log.info(" 하나퓨처 복지 데이터 현황:");
        log.info("   - 전체: {}개", totalCount);
        log.info("   - 중앙정부: {}개", centralCount);
        log.info("   - 지자체: {}개", localCount);
    }

    /**
     * 특정 우선순위 범위의 데이터 업데이트
     */
    @Transactional
    public void updatePriorities() {
        log.info(" 우선순위 업데이트 시작...");
        
        List<HanaFutureWelfareBenefit> allBenefits = hanaFutureRepository.findAll();

        // 우선순위 기능은 새로운 엔티티 구조에서 제거됨
        log.info("하나퓨처 복지 데이터 확인 완료: {}개", allBenefits.size());
    }
}
