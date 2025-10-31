package com.quant.market.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.market.application.dto.BatchCreateStockRequest;
import com.quant.market.application.dto.CreateStockRequest;
import com.quant.market.application.dto.StockDTO;
import com.quant.market.application.dto.StockQueryRequest;
import com.quant.market.application.dto.UpdateStockRequest;
import com.quant.market.domain.model.Stock;
import com.quant.market.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Stock Application Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /**
     * Create new stock
     */
    @Transactional
    public StockDTO createStock(CreateStockRequest request) {
        log.info("Creating stock: {}.{}", request.getExchange(), request.getStockCode());

        // Check if stock already exists
        Stock.Exchange exchange = Stock.Exchange.valueOf(request.getExchange());
        if (stockRepository.existsByExchangeAndStockCode(exchange, request.getStockCode())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR.getCode(),
                "Stock already exists: " + request.getExchange() + "." + request.getStockCode());
        }

        Stock stock = request.toDomain();
        Stock saved = stockRepository.save(stock);

        log.info("Stock created successfully: {}", saved.getId());
        return StockDTO.fromDomain(saved);
    }

    /**
     * Update stock
     */
    @Transactional
    public StockDTO updateStock(Long id, UpdateStockRequest request) {
        log.info("Updating stock: {}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "Stock not found: " + id));

        // Update fields
        if (request.getStockName() != null) {
            stock.setStockName(request.getStockName());
        }
        if (request.getCompanyName() != null) {
            stock.setCompanyName(request.getCompanyName());
        }
        if (request.getListingDate() != null) {
            stock.setListingDate(request.getListingDate());
        }
        if (request.getIndustry() != null) {
            stock.setIndustry(request.getIndustry());
        }
        if (request.getStatus() != null) {
            stock.setStatus(Stock.StockStatus.valueOf(request.getStatus()));
        }

        Stock updated = stockRepository.save(stock);

        log.info("Stock updated successfully: {}", id);
        return StockDTO.fromDomain(updated);
    }

    /**
     * Get stock by ID
     */
    @Transactional(readOnly = true)
    public StockDTO getStockById(Long id) {
        log.info("Getting stock by ID: {}", id);

        return stockRepository.findById(id)
                .map(StockDTO::fromDomain)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "Stock not found: " + id));
    }

    /**
     * Get stock by exchange and code
     */
    @Transactional(readOnly = true)
    public StockDTO getStockByCode(String exchange, String stockCode) {
        log.info("Getting stock: {}.{}", exchange, stockCode);

        Stock.Exchange exchangeEnum = Stock.Exchange.valueOf(exchange);
        return stockRepository.findByExchangeAndStockCode(exchangeEnum, stockCode)
                .map(StockDTO::fromDomain)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(),
                    "Stock not found: " + exchange + "." + stockCode));
    }

    /**
     * Get all stocks
     */
    @Transactional(readOnly = true)
    public List<StockDTO> getAllStocks() {
        log.info("Getting all stocks");

        return stockRepository.findAll().stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get stocks with pagination
     */
    @Transactional(readOnly = true)
    public Page<StockDTO> getStocks(Pageable pageable) {
        log.info("Getting stocks with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return stockRepository.findAll(pageable)
                .map(StockDTO::fromDomain);
    }

    /**
     * Get stocks by exchange
     */
    @Transactional(readOnly = true)
    public List<StockDTO> getStocksByExchange(String exchange) {
        log.info("Getting stocks by exchange: {}", exchange);

        Stock.Exchange exchangeEnum = Stock.Exchange.valueOf(exchange);
        return stockRepository.findByExchange(exchangeEnum).stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get stocks by status
     */
    @Transactional(readOnly = true)
    public List<StockDTO> getStocksByStatus(String status) {
        log.info("Getting stocks by status: {}", status);

        Stock.StockStatus statusEnum = Stock.StockStatus.valueOf(status);
        return stockRepository.findByStatus(statusEnum).stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Search stocks by name
     */
    @Transactional(readOnly = true)
    public List<StockDTO> searchStocksByName(String keyword) {
        log.info("Searching stocks by name: {}", keyword);

        return stockRepository.findByStockNameContaining(keyword).stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Search stocks by company name
     */
    @Transactional(readOnly = true)
    public List<StockDTO> searchStocksByCompany(String keyword) {
        log.info("Searching stocks by company: {}", keyword);

        return stockRepository.findByCompanyNameContaining(keyword).stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get stocks by industry
     */
    @Transactional(readOnly = true)
    public List<StockDTO> getStocksByIndustry(String industry) {
        log.info("Getting stocks by industry: {}", industry);

        return stockRepository.findByIndustry(industry).stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Delete stock
     */
    @Transactional
    public void deleteStock(Long id) {
        log.info("Deleting stock: {}", id);

        if (!stockRepository.findById(id).isPresent()) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "Stock not found: " + id);
        }

        stockRepository.deleteById(id);
        log.info("Stock deleted successfully: {}", id);
    }

    /**
     * Get stock count
     */
    @Transactional(readOnly = true)
    public long getStockCount() {
        return stockRepository.count();
    }

    /**
     * Get stock count by exchange
     */
    @Transactional(readOnly = true)
    public long getStockCountByExchange(String exchange) {
        Stock.Exchange exchangeEnum = Stock.Exchange.valueOf(exchange);
        return stockRepository.countByExchange(exchangeEnum);
    }

    /**
     * Batch create stocks (Async, High Performance)
     *
     * Features:
     * - Asynchronous processing to avoid blocking
     * - Batch insert for better performance
     * - Automatic exchange detection from stock code
     * - Skip duplicates instead of failing
     * - Returns summary of results
     *
     * @param requests List of batch create requests
     * @return CompletableFuture with batch result summary
     */
    @Async("stockBatchExecutor")
    @Transactional
    public CompletableFuture<BatchCreateResult> batchCreateStocks(List<BatchCreateStockRequest> requests) {
        log.info("Starting batch create stocks: {} items", requests.size());
        long startTime = System.currentTimeMillis();

        BatchCreateResult result = new BatchCreateResult();
        result.setTotal(requests.size());

        List<Stock> stocksToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        // Get all existing stock codes grouped by exchange for fast lookup
        Map<String, List<String>> existingStocks = stockRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        stock -> stock.getExchange().name(),
                        Collectors.mapping(Stock::getStockCode, Collectors.toList())
                ));

        for (int i = 0; i < requests.size(); i++) {
            BatchCreateStockRequest request = requests.get(i);
            try {
                // Auto-detect exchange from stock code
                Stock.Exchange exchange = Stock.Exchange.fromStockCode(request.getStockCode());

                // Check if stock already exists (skip duplicates)
                List<String> codesInExchange = existingStocks.getOrDefault(exchange.name(), new ArrayList<>());
                if (codesInExchange.contains(request.getStockCode())) {
                    String skipMsg = String.format("Row %d: Stock already exists: %s.%s",
                            i + 1, exchange.name(), request.getStockCode());
                    skipped.add(skipMsg);
                    log.debug(skipMsg);
                    continue;
                }

                // Convert to domain model
                Stock stock = request.toDomain();
                stocksToSave.add(stock);

                // Add to existing stocks map to detect duplicates within the batch
                codesInExchange.add(request.getStockCode());

            } catch (Exception e) {
                String errorMsg = String.format("Row %d: %s - %s",
                        i + 1, request.getStockCode(), e.getMessage());
                errors.add(errorMsg);
                log.error("Error processing stock at row {}: {}", i + 1, e.getMessage());
            }
        }

        // Batch save all stocks at once
        if (!stocksToSave.isEmpty()) {
            try {
                List<Stock> savedStocks = stockRepository.saveAll(stocksToSave);
                result.setSuccess(savedStocks.size());
                log.info("Batch saved {} stocks", savedStocks.size());
            } catch (Exception e) {
                log.error("Error during batch save", e);
                errors.add("Batch save error: " + e.getMessage());
            }
        }

        result.setFailed(errors.size());
        result.setSkipped(skipped.size());
        result.setErrors(errors);
        result.setSkippedItems(skipped);

        long duration = System.currentTimeMillis() - startTime;
        result.setProcessingTimeMs(duration);

        log.info("Batch create completed: total={}, success={}, failed={}, skipped={}, time={}ms",
                result.getTotal(), result.getSuccess(), result.getFailed(), result.getSkipped(), duration);

        return CompletableFuture.completedFuture(result);
    }

    /**
     * Query stocks by multiple conditions
     */
    @Transactional(readOnly = true)
    public List<StockDTO> queryStocks(StockQueryRequest request) {
        log.info("Querying stocks with conditions: {}", request);

        // Convert string lists to enums
        List<Stock.StockStatus> statuses = null;
        if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
            statuses = request.getStatuses().stream()
                    .map(Stock.StockStatus::valueOf)
                    .collect(Collectors.toList());
        }

        List<Stock.Exchange> exchanges = null;
        if (request.getExchanges() != null && !request.getExchanges().isEmpty()) {
            exchanges = request.getExchanges().stream()
                    .map(Stock.Exchange::valueOf)
                    .collect(Collectors.toList());
        }

        List<Stock> stocks = stockRepository.queryStocks(
                request.getListingDateFrom(),
                request.getListingDateTo(),
                request.getKeyword(),
                statuses,
                request.getIndustries(),
                exchanges
        );

        log.info("Found {} stocks matching the query conditions", stocks.size());

        return stocks.stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Batch Create Result DTO
     */
    @lombok.Data
    public static class BatchCreateResult {
        private int total;
        private int success;
        private int failed;
        private int skipped;
        private List<String> errors = new ArrayList<>();
        private List<String> skippedItems = new ArrayList<>();
        private long processingTimeMs;

        public String getSummary() {
            return String.format("Total: %d, Success: %d, Failed: %d, Skipped: %d, Time: %dms",
                    total, success, failed, skipped, processingTimeMs);
        }
    }
}
