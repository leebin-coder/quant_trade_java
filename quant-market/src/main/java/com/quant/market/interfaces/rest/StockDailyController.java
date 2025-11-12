package com.quant.market.interfaces.rest;

import com.quant.common.response.Result;
import com.quant.market.application.dto.BatchCreateDailyRequest;
import com.quant.market.application.dto.DailyQueryRequest;
import com.quant.market.application.dto.StockDailyDTO;
import com.quant.market.application.service.StockDailyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stock Daily REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/stock-daily")
@RequiredArgsConstructor
public class StockDailyController {

    private final StockDailyService dailyService;

    /**
     * Query daily data with filters
     * POST /api/stock-daily/query
     *
     * Features:
     * - Stock code is required
     * - Date filters are optional (startDate, endDate)
     * - Supports both ascending and descending sort order
     * - Default sort order is descending (newest first)
     *
     * Request body examples:
     *
     * 1. Query all data for a stock (newest first):
     * {
     *   "stockCode": "600000"
     * }
     *
     * 2. Query with date range (oldest first):
     * {
     *   "stockCode": "600000",
     *   "startDate": "2024-01-01",
     *   "endDate": "2024-12-31",
     *   "sortOrder": "asc"
     * }
     *
     * 3. Query from a start date (newest first):
     * {
     *   "stockCode": "600000",
     *   "startDate": "2024-06-01",
     *   "sortOrder": "desc"
     * }
     *
     * 4. Query up to an end date:
     * {
     *   "stockCode": "600000",
     *   "endDate": "2024-06-30"
     * }
     *
     * @param request Query request
     * @return List of daily data
     */
    @PostMapping("/query")
    public Result<List<StockDailyDTO>> queryDailyData(@RequestBody @Valid DailyQueryRequest request) {
        log.info("REST request to query daily data: stockCode={}, startDate={}, endDate={}, sortOrder={}",
                request.getStockCode(), request.getStartDate(), request.getEndDate(), request.getSortOrder());

        List<StockDailyDTO> dailyData = dailyService.queryDailyData(request);
        return Result.success(dailyData);
    }

    /**
     * Batch insert daily data
     * POST /api/stock-daily/batch
     *
     * Features:
     * - Maximum 1000 items per batch
     * - Automatically skips duplicates (same stock_code + trade_date)
     * - Returns detailed result with insert/skip counts
     *
     * Request body example:
     * [
     *   {
     *     "stockCode": "600000",
     *     "tradeDate": "2024-01-02",
     *     "openPrice": 8.50,
     *     "highPrice": 8.68,
     *     "lowPrice": 8.45,
     *     "closePrice": 8.62,
     *     "preClose": 8.48,
     *     "changeAmount": 0.14,
     *     "pctChange": 1.65,
     *     "volume": 5234567.00,
     *     "amount": 449876.50
     *   },
     *   {
     *     "stockCode": "600000",
     *     "tradeDate": "2024-01-03",
     *     "openPrice": 8.65,
     *     "highPrice": 8.78,
     *     "lowPrice": 8.60,
     *     "closePrice": 8.75,
     *     "preClose": 8.62,
     *     "changeAmount": 0.13,
     *     "pctChange": 1.51,
     *     "volume": 4876543.00,
     *     "amount": 425678.30
     *   }
     * ]
     *
     * @param requests List of daily data (max 1000 items)
     * @return Batch insert result with statistics
     */
    @PostMapping("/batch")
    public Result<Map<String, Object>> batchInsertDailyData(
            @RequestBody @Valid List<BatchCreateDailyRequest> requests) {
        log.info("REST request to batch insert daily data: {} items", requests.size());

        if (requests.isEmpty()) {
            return Result.error(400, "Request list cannot be empty");
        }

        // Process batch insert
        StockDailyService.BatchInsertResult result = dailyService.batchInsertDailyData(requests);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());
        response.put("inserted", result.getInserted());
        response.put("skipped", result.getSkipped());
        response.put("failed", result.getFailed());
        response.put("processingTimeMs", result.getProcessingTimeMs());
        response.put("summary", result.getSummary());

        if (!result.getErrors().isEmpty()) {
            response.put("errors", result.getErrors());
            log.warn("Batch insert errors: {}", result.getErrors());
        }

        if (!result.getSkippedItems().isEmpty()) {
            response.put("skippedItems", result.getSkippedItems());
            log.debug("Batch insert skipped items: {}", result.getSkippedItems());
        }

        log.info("Batch insert completed: {}", result.getSummary());
        return Result.success(response);
    }

    /**
     * Get daily data statistics
     * GET /api/stock-daily/stats
     *
     * @return Statistics about stored daily data
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDailyStats() {
        log.info("REST request to get daily data statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", dailyService.getDailyDataCount());

        return Result.success(stats);
    }

    /**
     * Get daily data count by stock code
     * GET /api/stock-daily/stats/{stockCode}
     *
     * @param stockCode Stock code
     * @return Record count for the stock
     */
    @GetMapping("/stats/{stockCode}")
    public Result<Map<String, Object>> getDailyStatsByStockCode(@PathVariable("stockCode") String stockCode) {
        log.info("REST request to get daily data statistics for stock: {}", stockCode);

        Map<String, Object> stats = new HashMap<>();
        stats.put("stockCode", stockCode);
        stats.put("recordCount", dailyService.getDailyDataCountByStockCode(stockCode));

        return Result.success(stats);
    }

    /**
     * Get the latest trade date
     * GET /api/stock-daily/latest-date
     *
     * Returns the most recent trade date in the database as a string.
     * - If no data exists in the table, returns null
     * - If data exists, returns the latest trade date in "yyyy-MM-dd" format
     *
     * Response example when data exists:
     * "2024-12-31"
     *
     * Response example when no data exists:
     * null
     *
     * @return Latest trade date string or null if no data exists
     */
    @GetMapping("/latest-date")
    public Result<String> getLatestTradeDate() {
        log.info("REST request to get latest trade date");

        java.time.LocalDate latestDate = dailyService.getLatestTradeDate();
        String dateString = latestDate != null ? latestDate.toString() : null;

        log.info("Latest trade date: {}", dateString);
        return Result.success(dateString);
    }
}
