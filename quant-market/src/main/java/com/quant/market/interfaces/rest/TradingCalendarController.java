package com.quant.market.interfaces.rest;

import com.quant.common.response.Result;
import com.quant.market.application.dto.TradingCalendarDTO;
import com.quant.market.application.service.TradingCalendarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trading Calendar REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/trading-calendar")
@RequiredArgsConstructor
public class TradingCalendarController {

    private final TradingCalendarService calendarService;

    /**
     * Get latest trading day
     * GET /api/trading-calendar/latest
     *
     * Returns the latest trading day information
     *
     * Response example:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "id": 1,
     *     "tradeDate": "2024-12-31",
     *     "isTradingDay": 1,
     *     "createdAt": "2024-01-01 12:00:00",
     *     "updatedAt": "2024-01-01 12:00:00"
     *   }
     * }
     *
     * @return Latest trading day
     */
    @GetMapping("/latest")
    public Result<TradingCalendarDTO> getLatestTradingDay() {
        log.info("REST request to get latest trading day");

        TradingCalendarDTO calendar = calendarService.getLatestTradingDay();
        return Result.success(calendar);
    }

    /**
     * Query trading calendar by year
     * GET /api/trading-calendar/year/{year}
     *
     * Returns all trading calendar data for the specified year
     *
     * URL parameter:
     * - year: Year (required, 1900-2100)
     *
     * Example: GET /api/trading-calendar/year/2020
     *
     * Response example:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": [
     *     {
     *       "id": 1,
     *       "tradeDate": "2020-01-02",
     *       "isTradingDay": 1,
     *       "createdAt": "2024-01-01 12:00:00",
     *       "updatedAt": "2024-01-01 12:00:00"
     *     },
     *     {
     *       "id": 2,
     *       "tradeDate": "2020-01-03",
     *       "isTradingDay": 1,
     *       "createdAt": "2024-01-01 12:00:00",
     *       "updatedAt": "2024-01-01 12:00:00"
     *     }
     *   ]
     * }
     *
     * @param year Year (required)
     * @return List of trading calendar data for the year
     */
    @GetMapping("/year/{year}")
    public Result<List<TradingCalendarDTO>> getByYear(
            @PathVariable("year") @Min(1900) @Max(2100) int year) {
        log.info("REST request to get trading calendar for year: {}", year);

        List<TradingCalendarDTO> calendars = calendarService.getByYear(year);
        return Result.success(calendars);
    }

    /**
     * Batch insert or update trading calendar
     * POST /api/trading-calendar/batch
     *
     * Features:
     * - Maximum 1000 items per batch
     * - If trade_date exists, update; otherwise insert
     * - Returns detailed result with affected count
     *
     * Request body example:
     * [
     *   {
     *     "tradeDate": "2020-01-02",
     *     "isTradingDay": 1
     *   },
     *   {
     *     "tradeDate": "2020-01-03",
     *     "isTradingDay": 1
     *   },
     *   {
     *     "tradeDate": "2020-01-04",
     *     "isTradingDay": 0
     *   }
     * ]
     *
     * Response example:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "total": 3,
     *     "affected": 3,
     *     "failed": 0,
     *     "processingTimeMs": 125,
     *     "summary": "Total: 3, Affected: 3, Failed: 0, Time: 125ms"
     *   }
     * }
     *
     * @param requests List of trading calendar data (max 1000 items)
     * @return Batch upsert result with statistics
     */
    @PostMapping("/batch")
    public Result<Map<String, Object>> batchUpsertCalendar(
            @RequestBody @Valid List<TradingCalendarDTO> requests) {
        log.info("REST request to batch upsert trading calendar: {} items", requests.size());

        if (requests.isEmpty()) {
            return Result.error(400, "Request list cannot be empty");
        }

        if (requests.size() > 1000) {
            return Result.error(400, "Batch size cannot exceed 1000 items. Received: " + requests.size());
        }

        // Process batch upsert
        TradingCalendarService.BatchUpsertResult result = calendarService.batchUpsertCalendar(requests);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());
        response.put("affected", result.getAffected());
        response.put("failed", result.getFailed());
        response.put("processingTimeMs", result.getProcessingTimeMs());
        response.put("summary", result.getSummary());

        if (!result.getErrors().isEmpty()) {
            response.put("errors", result.getErrors());
            log.warn("Batch upsert errors: {}", result.getErrors());
        }

        log.info("Batch upsert completed: {}", result.getSummary());
        return Result.success(response);
    }

    /**
     * Get trading calendar statistics
     * GET /api/trading-calendar/stats
     *
     * Response example:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "totalRecords": 3650
     *   }
     * }
     *
     * @return Statistics about stored trading calendar data
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCalendarStats() {
        log.info("REST request to get trading calendar statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", calendarService.getCalendarCount());

        return Result.success(stats);
    }
}
