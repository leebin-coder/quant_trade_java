package com.quant.market.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.market.application.dto.BatchCreateStockRequest;
import com.quant.market.application.dto.CreateStockRequest;
import com.quant.market.application.dto.StockDTO;
import com.quant.market.application.dto.StockQueryRequest;
import com.quant.market.application.dto.UpdateStockRequest;
import com.quant.market.domain.model.Stock;
import com.quant.market.domain.model.StockRelation;
import com.quant.market.domain.repository.StockRelationRepository;
import com.quant.market.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final StockRelationRepository stockRelationRepository;

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
        if (request.getListingDate() != null) {
            stock.setListingDate(request.getListingDate());
        }
        if (request.getIndustry() != null) {
            stock.setIndustry(request.getIndustry());
        }
        if (request.getStatus() != null) {
            stock.setStatus(Stock.StockStatus.valueOf(request.getStatus()));
        }
        if (request.getArea() != null) {
            stock.setArea(request.getArea());
        }
        if (request.getFullName() != null) {
            stock.setFullName(request.getFullName());
        }
        if (request.getEnName() != null) {
            stock.setEnName(request.getEnName());
        }
        if (request.getCnSpell() != null) {
            stock.setCnSpell(request.getCnSpell());
        }
        if (request.getMarket() != null) {
            stock.setMarket(request.getMarket());
        }
        if (request.getCurrType() != null) {
            stock.setCurrType(request.getCurrType());
        }
        if (request.getDelistDate() != null) {
            stock.setDelistDate(request.getDelistDate());
        }
        if (request.getIsHs() != null) {
            stock.setIsHs(Stock.IsHs.valueOf(request.getIsHs()));
        }
        if (request.getActName() != null) {
            stock.setActName(request.getActName());
        }
        if (request.getActEntType() != null) {
            stock.setActEntType(request.getActEntType());
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
     * Batch create or update stocks (Async, High Performance)
     *
     * Features:
     * - Asynchronous processing to avoid blocking
     * - Batch upsert (insert or update) based on stock_code
     * - If stock_code exists, updates all fields
     * - If stock_code doesn't exist, inserts new record
     * - Returns summary of results
     *
     * @param requests List of batch create/update requests
     * @return CompletableFuture with batch result summary
     */
    @Async("stockBatchExecutor")
    @Transactional
    public CompletableFuture<BatchCreateResult> batchCreateStocks(List<BatchCreateStockRequest> requests) {
        log.info("Starting batch upsert stocks: {} items", requests.size());
        long startTime = System.currentTimeMillis();

        BatchCreateResult result = new BatchCreateResult();
        result.setTotal(requests.size());

        List<Stock> stocksToUpsert = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            BatchCreateStockRequest request = requests.get(i);
            try {
                // Convert to domain model
                Stock stock = request.toDomain();
                stocksToUpsert.add(stock);

            } catch (Exception e) {
                String errorMsg = String.format("Row %d: %s - %s",
                        i + 1, request.getStockCode(), e.getMessage());
                errors.add(errorMsg);
                log.error("Error processing stock at row {}: {}", i + 1, e.getMessage());
            }
        }

        // Batch upsert all stocks at once (insert or update)
        int affectedRows = 0;
        if (!stocksToUpsert.isEmpty()) {
            try {
                affectedRows = stockRepository.batchUpsert(stocksToUpsert);
                result.setSuccess(affectedRows);
                log.info("Batch upsert affected {} rows", affectedRows);
            } catch (Exception e) {
                log.error("Error during batch upsert", e);
                errors.add("Batch upsert error: " + e.getMessage());
                result.setFailed(stocksToUpsert.size());
            }
        }

        result.setFailed(errors.size());
        result.setErrors(errors);

        long duration = System.currentTimeMillis() - startTime;
        result.setProcessingTimeMs(duration);

        log.info("Batch upsert completed: total={}, success={}, failed={}, time={}ms",
                result.getTotal(), result.getSuccess(), result.getFailed(), duration);

        return CompletableFuture.completedFuture(result);
    }

    /**
     * Query stocks by multiple conditions (with optional pagination)
     * @param request 查询请求
     * @param userId 用户ID（可选，用于设置isFollowed字段）
     */
    @Transactional(readOnly = true)
    public Object queryStocks(StockQueryRequest request, Long userId) {
        log.info("Querying stocks with conditions: {}, userId: {}", request, userId);

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

        // Check if pagination is requested
        if (request.getPage() != null && request.getSize() != null) {
            // Return paginated result
            int page = request.getPage();
            int size = request.getSize();
            String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
            String sortDir = request.getSortDir() != null ? request.getSortDir() : "desc";

            // Manual pagination
            int totalElements = stocks.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalElements);

            List<StockDTO> pagedStocks;
            if (fromIndex >= totalElements) {
                pagedStocks = new ArrayList<>();
            } else {
                // Apply sorting
                stocks.sort((s1, s2) -> {
                    int comparison = 0;
                    switch (sortBy) {
                        case "stockCode":
                            comparison = s1.getStockCode().compareTo(s2.getStockCode());
                            break;
                        case "stockName":
                            comparison = s1.getStockName().compareTo(s2.getStockName());
                            break;
                        case "exchange":
                            comparison = s1.getExchange().compareTo(s2.getExchange());
                            break;
                        case "listingDate":
                            comparison = s1.getListingDate().compareTo(s2.getListingDate());
                            break;
                        case "createdAt":
                        default:
                            comparison = s1.getCreatedAt().compareTo(s2.getCreatedAt());
                            break;
                    }
                    return sortDir.equalsIgnoreCase("asc") ? comparison : -comparison;
                });

                pagedStocks = stocks.subList(fromIndex, toIndex).stream()
                        .map(StockDTO::fromDomain)
                        .collect(Collectors.toList());
            }

            // 批量设置 isFollowed 字段
            setIsFollowedBatch(pagedStocks, userId);

            // Create PageResult
            com.quant.common.response.PageResult<StockDTO> pageResult =
                    com.quant.common.response.PageResult.success(
                            pagedStocks,
                            page,
                            size,
                            (long) totalElements
                    );

            log.info("Returning paginated result: page={}, size={}, total={}", page, size, totalElements);
            return pageResult;
        } else {
            // Return all results without pagination
            List<StockDTO> stockDTOs = stocks.stream()
                    .map(StockDTO::fromDomain)
                    .collect(Collectors.toList());

            // 批量设置 isFollowed 字段
            setIsFollowedBatch(stockDTOs, userId);

            log.info("Returning all {} results without pagination", stockDTOs.size());
            return stockDTOs;
        }
    }

    /**
     * 批量设置股票的 isFollowed 字段
     * @param stockDTOs 股票DTO列表
     * @param userId 用户ID，如果为null则所有股票的isFollowed都设置为false
     */
    private void setIsFollowedBatch(List<StockDTO> stockDTOs, Long userId) {
        if (stockDTOs == null || stockDTOs.isEmpty()) {
            return;
        }

        if (userId == null) {
            // 未登录用户，全部设置为 false
            stockDTOs.forEach(dto -> dto.setIsFollowed(false));
            return;
        }

        // 提取所有股票代码
        List<String> stockCodes = stockDTOs.stream()
                .map(StockDTO::getStockCode)
                .collect(Collectors.toList());

        // 批量查询用户关注的股票代码
        List<String> followedStockCodes = stockRelationRepository.findFollowedStockCodes(
                stockCodes, userId, StockRelation.RefType.STOCKS_USER_FOLLOWED);

        // 转换为Set以便快速查找
        Set<String> followedSet = new HashSet<>(followedStockCodes);

        // 批量设置 isFollowed 字段
        stockDTOs.forEach(dto -> dto.setIsFollowed(followedSet.contains(dto.getStockCode())));
    }

    /**
     * Batch Create/Update Result DTO
     */
    @lombok.Data
    public static class BatchCreateResult {
        private int total;
        private int success;
        private int failed;
        private List<String> errors = new ArrayList<>();
        private long processingTimeMs;

        public String getSummary() {
            return String.format("Total: %d, Success: %d, Failed: %d, Time: %dms",
                    total, success, failed, processingTimeMs);
        }
    }
}
