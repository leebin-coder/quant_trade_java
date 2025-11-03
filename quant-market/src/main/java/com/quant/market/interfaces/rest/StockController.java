package com.quant.market.interfaces.rest;

import com.quant.common.response.PageResult;
import com.quant.common.response.Result;
import com.quant.market.application.dto.BatchCreateStockRequest;
import com.quant.market.application.dto.CreateStockRequest;
import com.quant.market.application.dto.StockDTO;
import com.quant.market.application.dto.StockQueryRequest;
import com.quant.market.application.dto.UpdateStockRequest;
import com.quant.market.application.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Stock REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * Create new stock
     * POST /api/stocks
     */
    @PostMapping
    public Result<StockDTO> createStock(@RequestBody @Valid CreateStockRequest request) {
        log.info("REST request to create stock: {}.{}", request.getExchange(), request.getStockCode());
        StockDTO stock = stockService.createStock(request);
        return Result.success(stock);
    }

    /**
     * Batch create stocks (Async, High Performance)
     * POST /api/stocks/batch
     *
     * Features:
     * - Asynchronous processing (non-blocking)
     * - Automatic exchange detection from stock code
     * - Skips duplicates automatically
     * - Returns immediately with task accepted message
     * - Batch result will be logged server-side
     *
     * Request body example:
     * [
     *   {
     *     "stockCode": "600000",
     *     "stockName": "浦发银行",
     *     "companyName": "上海浦东发展银行股份有限公司",
     *     "listingDate": "1999-11-10",
     *     "industry": "银行"
     *   },
     *   ...
     * ]
     *
     * @param requests List of batch create requests
     * @return Immediate response with task acceptance
     */
    @PostMapping("/batch")
    public Result<Map<String, Object>> batchCreateStocks(
            @RequestBody @Valid List<BatchCreateStockRequest> requests) {
        log.info("REST request to batch create stocks: {} items", requests.size());

        if (requests.isEmpty()) {
            return Result.error(400, "Request list cannot be empty");
        }

        if (requests.size() > 10000) {
            return Result.error(400, "Batch size cannot exceed 10000 items");
        }

        // Start async processing
        CompletableFuture<StockService.BatchCreateResult> future =
                stockService.batchCreateStocks(requests);

        // Register callback for logging results
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Batch create stocks failed", ex);
            } else {
                log.info("Batch create stocks completed: {}", result.getSummary());
                if (!result.getErrors().isEmpty()) {
                    log.warn("Batch create errors: {}", result.getErrors());
                }
                if (!result.getSkippedItems().isEmpty()) {
                    log.info("Batch create skipped items: {}", result.getSkippedItems());
                }
            }
        });

        // Return immediate response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch create task accepted");
        response.put("totalItems", requests.size());
        response.put("status", "Processing");
        response.put("note", "Task is running asynchronously. Check server logs for completion status.");

        return Result.success(response);
    }

    /**
     * Update stock
     * PUT /api/stocks/{id}
     */
    @PutMapping("/{id}")
    public Result<StockDTO> updateStock(@PathVariable("id") Long id, @RequestBody UpdateStockRequest request) {
        log.info("REST request to update stock: {}", id);
        StockDTO stock = stockService.updateStock(id, request);
        return Result.success(stock);
    }

    /**
     * Get stock by ID
     * GET /api/stocks/{id}
     */
    @GetMapping("/{id}")
    public Result<StockDTO> getStockById(@PathVariable("id") Long id) {
        log.info("REST request to get stock: {}", id);
        StockDTO stock = stockService.getStockById(id);
        return Result.success(stock);
    }

    /**
     * Get stock by exchange and code
     * GET /api/stocks/{exchange}/{stockCode}
     */
    @GetMapping("/{exchange}/{stockCode}")
    public Result<StockDTO> getStockByCode(
            @PathVariable("exchange") String exchange,
            @PathVariable("stockCode") String stockCode) {
        log.info("REST request to get stock: {}.{}", exchange, stockCode);
        StockDTO stock = stockService.getStockByCode(exchange.toUpperCase(), stockCode);
        return Result.success(stock);
    }

    /**
     * Get all stocks with pagination
     * GET /api/stocks?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public Result<PageResult<StockDTO>> getStocks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        log.info("REST request to get stocks: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<StockDTO> stockPage = stockService.getStocks(pageable);
        PageResult<StockDTO> pageResult = PageResult.success(
                stockPage.getContent(),
                stockPage.getNumber(),
                stockPage.getSize(),
                stockPage.getTotalElements()
        );

        return (Result<PageResult<StockDTO>>) (Result<?>) pageResult;
    }

    /**
     * Get all stocks (without pagination)
     * GET /api/stocks/all
     */
    @GetMapping("/all")
    public Result<List<StockDTO>> getAllStocks() {
        log.info("REST request to get all stocks");
        List<StockDTO> stocks = stockService.getAllStocks();
        return Result.success(stocks);
    }

    /**
     * Query stocks by multiple conditions (with optional pagination)
     * POST /api/stocks/query
     *
     * Features:
     * - Filter by listing date range (listingDateFrom, listingDateTo)
     * - Fuzzy search by keyword (matches stock code, stock name, or company name)
     * - Multi-select filter by status (LISTED, DELISTED, SUSPENDED)
     * - Multi-select filter by industry
     * - Multi-select filter by exchange (SH, SZ, BJ, HK, US)
     * - Optional pagination (if page and size are provided)
     * - Returns full list if pagination parameters are not provided
     *
     * Request body example (without pagination - returns all):
     * {
     *   "listingDateFrom": "2020-01-01",
     *   "listingDateTo": "2023-12-31",
     *   "keyword": "银行",
     *   "statuses": ["LISTED"],
     *   "industries": ["银行", "保险"],
     *   "exchanges": ["SH", "SZ"]
     * }
     *
     * Request body example (with pagination):
     * {
     *   "keyword": "银行",
     *   "exchanges": ["SH"],
     *   "page": 0,
     *   "size": 20,
     *   "sortBy": "stockCode",
     *   "sortDir": "asc"
     * }
     *
     * @param request Query conditions (with optional pagination)
     * @return List of matching stocks or PageResult if pagination is requested
     */
    @PostMapping("/query")
    public Result<?> queryStocks(@RequestBody StockQueryRequest request) {
        log.info("REST request to query stocks with conditions: {}", request);
        Object result = stockService.queryStocks(request);

        if (result instanceof com.quant.common.response.PageResult) {
            // Return PageResult directly (it already extends Result)
            return (Result<?>) result;
        } else {
            // Return List wrapped in Result
            return Result.success(result);
        }
    }

    /**
     * Get stocks by exchange
     * GET /api/stocks/exchange/{exchange}
     */
    @GetMapping("/exchange/{exchange}")
    public Result<List<StockDTO>> getStocksByExchange(@PathVariable("exchange") String exchange) {
        log.info("REST request to get stocks by exchange: {}", exchange);
        List<StockDTO> stocks = stockService.getStocksByExchange(exchange.toUpperCase());
        return Result.success(stocks);
    }

    /**
     * Get stocks by status
     * GET /api/stocks/status/{status}
     */
    @GetMapping("/status/{status}")
    public Result<List<StockDTO>> getStocksByStatus(@PathVariable("status") String status) {
        log.info("REST request to get stocks by status: {}", status);
        List<StockDTO> stocks = stockService.getStocksByStatus(status.toUpperCase());
        return Result.success(stocks);
    }

    /**
     * Get stocks by industry
     * GET /api/stocks/industry/{industry}
     */
    @GetMapping("/industry/{industry}")
    public Result<List<StockDTO>> getStocksByIndustry(@PathVariable("industry") String industry) {
        log.info("REST request to get stocks by industry: {}", industry);
        List<StockDTO> stocks = stockService.getStocksByIndustry(industry);
        return Result.success(stocks);
    }

    /**
     * Search stocks by name
     * GET /api/stocks/search/name?keyword=xxx
     */
    @GetMapping("/search/name")
    public Result<List<StockDTO>> searchStocksByName(@RequestParam("keyword") String keyword) {
        log.info("REST request to search stocks by name: {}", keyword);
        List<StockDTO> stocks = stockService.searchStocksByName(keyword);
        return Result.success(stocks);
    }

    /**
     * Search stocks by company name
     * GET /api/stocks/search/company?keyword=xxx
     */
    @GetMapping("/search/company")
    public Result<List<StockDTO>> searchStocksByCompany(@RequestParam("keyword") String keyword) {
        log.info("REST request to search stocks by company: {}", keyword);
        List<StockDTO> stocks = stockService.searchStocksByCompany(keyword);
        return Result.success(stocks);
    }

    /**
     * Delete stock
     * DELETE /api/stocks/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteStock(@PathVariable("id") Long id) {
        log.info("REST request to delete stock: {}", id);
        stockService.deleteStock(id);
        return Result.success();
    }

    /**
     * Get stock statistics
     * GET /api/stocks/stats
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStockStats() {
        log.info("REST request to get stock statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", stockService.getStockCount());
        stats.put("sh", stockService.getStockCountByExchange("SH"));
        stats.put("sz", stockService.getStockCountByExchange("SZ"));
        stats.put("bj", stockService.getStockCountByExchange("BJ"));
        stats.put("hk", stockService.getStockCountByExchange("HK"));
        stats.put("us", stockService.getStockCountByExchange("US"));

        return Result.success(stats);
    }
}
