package com.quant.market.interfaces.rest;

import com.quant.common.response.Result;
import com.quant.market.application.dto.TradingCalendarDTO;
import com.quant.market.application.service.TradingCalendarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    /**
     * Get the latest trading day on or before now
     * GET /api/trading-calendar/latest-on-or-before
     *
     * This endpoint uses the current system date as the reference time,
     * and returns the latest available trading day information.
     *
     * Example response:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "tradeDate": "2024-02-08",
     *     "isTradingDay": 1
     *   }
     * }
     *
     * @return Latest trading day before or equal to the current date
     */
    @GetMapping("/latest-on-or-before")
    public Result<TradingCalendarDTO> getLatestTradingDayOnOrBefore() {
        log.info("REST request to get latest trading day on or before current date");
        TradingCalendarDTO calendar = calendarService.getLatestTradingDayOnOrBefore(LocalDate.now());
        return Result.success(calendar);
    }

    /**
     * Check if a given date is a trading day
     * GET /api/trading-calendar/is-trading-day
     *
     * Query parameter:
     * - date: Date to check (required, format: yyyy-MM-dd)
     *
     * Example: GET /api/trading-calendar/is-trading-day?date=2024-01-15
     *
     * Response example:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "date": "2024-01-15",
     *     "isTradingDay": true
     *   }
     * }
     *
     * Note: Returns false if the date is not found in the calendar or is marked as non-trading day
     *
     * @param date Date to check (format: yyyy-MM-dd)
     * @return Whether the date is a trading day
     */
    @GetMapping("/is-trading-day")
    public Result<Map<String, Object>> isTradingDay(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("REST request to check if {} is a trading day", date);

        boolean isTradingDay = calendarService.isTradingDay(date);

        Map<String, Object> response = new HashMap<>();
        response.put("date", date.toString());
        response.put("isTradingDay", isTradingDay);

        log.info("Date {} is {}a trading day", date, isTradingDay ? "" : "NOT ");
        return Result.success(response);
    }
}
