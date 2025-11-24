package com.quant.market.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.market.domain.model.Stock;
import com.quant.market.domain.model.StockRelation;
import com.quant.market.domain.repository.StockRelationRepository;
import com.quant.market.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stock Relation Application Service
 * 股票关联应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockRelationService {

    private final StockRelationRepository stockRelationRepository;
    private final StockRepository stockRepository;

    /**
     * 添加关注（用户关注股票）
     * @param userId 用户ID
     * @param stockCode 股票代码
     * @return 创建的关联关系
     */
    @Transactional
    public StockRelation followStock(Long userId, String stockCode) {
        log.info("User {} is following stock: {}", userId, stockCode);

        // 检查是否已经关注
        if (stockRelationRepository.existsByStockCodeAndRefIdAndRefType(
                stockCode, userId, StockRelation.RefType.STOCKS_USER_FOLLOWED)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR.getCode(),
                    "Already following this stock: " + stockCode);
        }

        StockRelation relation = StockRelation.builder()
                .stockCode(stockCode)
                .refId(userId)
                .refType(StockRelation.RefType.STOCKS_USER_FOLLOWED)
                .build();

        StockRelation saved = stockRelationRepository.save(relation);
        log.info("Stock relation created successfully: {}", saved.getId());
        return saved;
    }

    /**
     * 取消关注（用户取消关注股票）
     * @param userId 用户ID
     * @param stockCode 股票代码
     */
    @Transactional
    public void unfollowStock(Long userId, String stockCode) {
        log.info("User {} is unfollowing stock: {}", userId, stockCode);

        // 检查关联关系是否存在
        if (!stockRelationRepository.existsByStockCodeAndRefIdAndRefType(
                stockCode, userId, StockRelation.RefType.STOCKS_USER_FOLLOWED)) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(),
                    "Stock relation not found for user: " + userId + " and stock: " + stockCode);
        }

        stockRelationRepository.deleteByStockCodeAndRefIdAndRefType(
                stockCode, userId, StockRelation.RefType.STOCKS_USER_FOLLOWED);
        log.info("Stock relation deleted successfully for user: {} and stock: {}", userId, stockCode);
    }

    /**
     * 获取用户的关注列表（用户关注的所有股票）
     * @param userId 用户ID
     * @return 用户关注的股票列表
     */
    public List<StockRelation> getUserFollowedStocks(Long userId) {
        log.info("Getting followed stocks for user: {}", userId);
        return stockRelationRepository.findByRefIdAndRefType(
                userId, StockRelation.RefType.STOCKS_USER_FOLLOWED);
    }

    /**
     * 检查用户是否关注了某个股票
     * @param userId 用户ID
     * @param stockCode 股票代码
     * @return 是否已关注
     */
    public boolean isFollowing(Long userId, String stockCode) {
        return stockRelationRepository.existsByStockCodeAndRefIdAndRefType(
                stockCode, userId, StockRelation.RefType.STOCKS_USER_FOLLOWED);
    }

    /**
     * 获取用户关注的股票数量
     * @param userId 用户ID
     * @return 关注数量
     */
    public long countUserFollowedStocks(Long userId) {
        return stockRelationRepository.countByRefIdAndRefType(
                userId, StockRelation.RefType.STOCKS_USER_FOLLOWED);
    }

    /**
     * 获取用户关注的股票详细信息（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 股票详细信息分页结果
     */
    public Page<Stock> getUserFollowedStocksWithDetails(Long userId, Pageable pageable) {
        log.info("Getting followed stocks with details for user: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        // 1. 获取用户关注的所有股票关联关系
        List<StockRelation> relations = stockRelationRepository.findByRefIdAndRefType(
                userId, StockRelation.RefType.STOCKS_USER_FOLLOWED);

        if (relations.isEmpty()) {
            log.info("User {} has no followed stocks", userId);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 2. 提取股票代码列表
        List<String> stockCodes = relations.stream()
                .map(StockRelation::getStockCode)
                .collect(Collectors.toList());

        // 3. 批量查询股票信息
        List<Stock> allStocks = stockRepository.findByStockCodeIn(stockCodes);

        // 4. 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allStocks.size());

        List<Stock> pagedStocks;
        if (start > allStocks.size()) {
            pagedStocks = Collections.emptyList();
        } else {
            pagedStocks = allStocks.subList(start, end);
        }

        log.info("Found {} followed stocks for user {}, returning page with {} items",
                allStocks.size(), userId, pagedStocks.size());

        return new PageImpl<>(pagedStocks, pageable, allStocks.size());
    }
}
