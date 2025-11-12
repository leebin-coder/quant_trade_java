package com.quant.market.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.market.application.dto.BatchCreateDailyRequest;
import com.quant.market.application.dto.DailyQueryRequest;
import com.quant.market.application.dto.StockDailyDTO;
import com.quant.market.domain.model.StockDaily;
import com.quant.market.domain.repository.StockDailyRepository;
import com.quant.market.infrastructure.persistence.entity.StockDailyEntity;
import com.quant.market.infrastructure.persistence.repository.StockDailyBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stock Daily Application Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockDailyService {

    private final StockDailyRepository dailyRepository;
    private final StockDailyBatchRepository batchRepository;

    /**
     * Query daily data with filters
     *
     * @param request Query request with stock code (required) and date filters (optional)
     * @return List of daily data
     */
    @Transactional(readOnly = true)
    public List<StockDailyDTO> queryDailyData(DailyQueryRequest request) {
        log.info("Querying daily data: stockCode={}, startDate={}, endDate={}, sortOrder={}",
                request.getStockCode(), request.getStartDate(), request.getEndDate(), request.getSortOrder());

        List<StockDaily> dailyList = dailyRepository.queryDailyData(
                request.getStockCode(),
                request.getStartDate(),
                request.getEndDate(),
                request.isAscending()
        );

        log.info("Found {} daily records for stock code: {}", dailyList.size(), request.getStockCode());

        return dailyList.stream()
                .map(StockDailyDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Batch insert daily data - Optimized Version
     * Maximum 1000 items per batch
     * Duplicates (same stock_code + trade_date) will be skipped automatically by database
     *
     * Uses PostgreSQL ON CONFLICT DO NOTHING for high-performance batch operations:
     * - Single SQL statement for all records
     * - Automatic duplicate detection by database
     * - Much faster than checking existence individually
     *
     * @param requests List of daily data to insert
     * @return Batch insert result
     */
    @Transactional
    public BatchInsertResult batchInsertDailyData(List<BatchCreateDailyRequest> requests) {
        log.info("Starting batch insert daily data: {} items", requests.size());

        // Validate batch size
        if (requests.size() > 1000) {
            throw new BusinessException(
                    ResultCode.VALIDATION_ERROR.getCode(),
                    "Batch size cannot exceed 1000 items. Received: " + requests.size());
        }

        long startTime = System.currentTimeMillis();
        BatchInsertResult result = new BatchInsertResult();
        result.setTotal(requests.size());

        List<String> errors = new ArrayList<>();
        List<StockDailyEntity> validEntities = new ArrayList<>();

        // Validate and convert to entities
        for (int i = 0; i < requests.size(); i++) {
            BatchCreateDailyRequest request = requests.get(i);
            try {
                StockDaily domain = request.toDomain();
                StockDailyEntity entity = StockDailyEntity.fromDomain(domain);
                validEntities.add(entity);
            } catch (Exception e) {
                String errorMsg = String.format("Row %d: %s on %s - %s",
                        i + 1, request.getStockCode(), request.getTradeDate(), e.getMessage());
                errors.add(errorMsg);
                log.error("Error processing daily data at row {}: {}", i + 1, e.getMessage());
            }
        }

        // Perform batch insert using native SQL
        int insertedCount = 0;
        if (!validEntities.isEmpty()) {
            try {
                insertedCount = batchRepository.batchInsert(validEntities);
                log.info("Batch inserted {} daily records", insertedCount);
            } catch (Exception e) {
                log.error("Error during batch insert", e);
                errors.add("Batch insert error: " + e.getMessage());
            }
        }

        int skippedCount = validEntities.size() - insertedCount;
        result.setInserted(insertedCount);
        result.setSkipped(skippedCount);
        result.setFailed(errors.size());
        result.setErrors(errors);
        result.setSkippedItems(new ArrayList<>()); // Not tracked individually in batch mode

        long duration = System.currentTimeMillis() - startTime;
        result.setProcessingTimeMs(duration);

        log.info("Batch insert completed: total={}, inserted={}, skipped={}, failed={}, time={}ms",
                result.getTotal(), result.getInserted(), result.getSkipped(), result.getFailed(), duration);

        return result;
    }

    /**
     * Get daily data count
     */
    @Transactional(readOnly = true)
    public long getDailyDataCount() {
        return dailyRepository.count();
    }

    /**
     * Get daily data count by stock code
     */
    @Transactional(readOnly = true)
    public long getDailyDataCountByStockCode(String stockCode) {
        return dailyRepository.countByStockCode(stockCode);
    }

    /**
     * Get the latest trade date in the database
     *
     * @return Latest trade date, or null if no data exists
     */
    @Transactional(readOnly = true)
    public java.time.LocalDate getLatestTradeDate() {
        return dailyRepository.findLatestTradeDate();
    }

    /**
     * Batch Insert Result DTO
     */
    @lombok.Data
    public static class BatchInsertResult {
        private int total;
        private int inserted;
        private int skipped;
        private int failed;
        private List<String> errors = new ArrayList<>();
        private List<String> skippedItems = new ArrayList<>();
        private long processingTimeMs;

        public String getSummary() {
            return String.format("Total: %d, Inserted: %d, Skipped: %d, Failed: %d, Time: %dms",
                    total, inserted, skipped, failed, processingTimeMs);
        }
    }
}
