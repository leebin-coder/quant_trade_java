package com.quant.market.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.market.application.dto.TradingCalendarDTO;
import com.quant.market.infrastructure.persistence.entity.TradingCalendarEntity;
import com.quant.market.infrastructure.persistence.repository.TradingCalendarBatchRepository;
import com.quant.market.infrastructure.persistence.repository.TradingCalendarJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Trading Calendar Application Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradingCalendarService {

    private final TradingCalendarJpaRepository jpaRepository;
    private final TradingCalendarBatchRepository batchRepository;

    /**
     * Query latest trading day
     *
     * @return Latest trading day
     */
    @Transactional(readOnly = true)
    public TradingCalendarDTO getLatestTradingDay() {
        log.info("Querying latest trading day");

        TradingCalendarEntity entity = jpaRepository.findLatestTradingDay()
                .orElseThrow(() -> new BusinessException(
                        ResultCode.NOT_FOUND.getCode(),
                        "No trading calendar data found"));

        log.info("Found latest trading day: {}", entity.getTradeDate());
        return TradingCalendarDTO.fromEntity(entity);
    }

    /**
     * Query trading calendar by year
     *
     * @param year Year (required)
     * @return List of trading calendar data for the year
     */
    @Transactional(readOnly = true)
    public List<TradingCalendarDTO> getByYear(int year) {
        log.info("Querying trading calendar for year: {}", year);

        List<TradingCalendarEntity> entities = jpaRepository.findByYear(year);

        log.info("Found {} trading calendar records for year {}", entities.size(), year);

        return entities.stream()
                .map(TradingCalendarDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Batch upsert trading calendar data
     * If trade_date exists, update; otherwise insert
     * Maximum 1000 items per batch
     *
     * @param requests List of trading calendar data to upsert
     * @return Batch upsert result
     */
    @Transactional
    public BatchUpsertResult batchUpsertCalendar(List<TradingCalendarDTO> requests) {
        log.info("Starting batch upsert trading calendar: {} items", requests.size());

        // Validate batch size
        if (requests.size() > 1000) {
            throw new BusinessException(
                    ResultCode.VALIDATION_ERROR.getCode(),
                    "Batch size cannot exceed 1000 items. Received: " + requests.size());
        }

        long startTime = System.currentTimeMillis();
        BatchUpsertResult result = new BatchUpsertResult();
        result.setTotal(requests.size());

        List<String> errors = new ArrayList<>();
        List<TradingCalendarEntity> validEntities = new ArrayList<>();

        // Validate and convert to entities
        for (int i = 0; i < requests.size(); i++) {
            TradingCalendarDTO request = requests.get(i);
            try {
                TradingCalendarEntity entity = request.toEntity();
                validEntities.add(entity);
            } catch (Exception e) {
                String errorMsg = String.format("Row %d: %s - %s",
                        i + 1, request.getTradeDate(), e.getMessage());
                errors.add(errorMsg);
                log.error("Error processing trading calendar at row {}: {}", i + 1, e.getMessage());
            }
        }

        // Perform batch upsert using native SQL
        int affectedCount = 0;
        if (!validEntities.isEmpty()) {
            try {
                affectedCount = batchRepository.batchUpsert(validEntities);
                log.info("Batch upserted {} trading calendar records", affectedCount);
            } catch (Exception e) {
                log.error("Error during batch upsert", e);
                errors.add("Batch upsert error: " + e.getMessage());
            }
        }

        result.setAffected(affectedCount);
        result.setFailed(errors.size());
        result.setErrors(errors);

        long duration = System.currentTimeMillis() - startTime;
        result.setProcessingTimeMs(duration);

        log.info("Batch upsert completed: total={}, affected={}, failed={}, time={}ms",
                result.getTotal(), result.getAffected(), result.getFailed(), duration);

        return result;
    }

    /**
     * Get trading calendar count
     */
    @Transactional(readOnly = true)
    public long getCalendarCount() {
        return jpaRepository.count();
    }

    /**
     * Batch Upsert Result DTO
     */
    @lombok.Data
    public static class BatchUpsertResult {
        private int total;
        private int affected;
        private int failed;
        private List<String> errors = new ArrayList<>();
        private long processingTimeMs;

        public String getSummary() {
            return String.format("Total: %d, Affected: %d, Failed: %d, Time: %dms",
                    total, affected, failed, processingTimeMs);
        }
    }
}
