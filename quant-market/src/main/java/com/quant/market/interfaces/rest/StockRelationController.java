package com.quant.market.interfaces.rest;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.PageResult;
import com.quant.common.response.Result;
import com.quant.common.response.ResultCode;
import com.quant.common.security.JwtTokenUtil;
import com.quant.market.application.dto.FollowStockRequest;
import com.quant.market.application.dto.StockDTO;
import com.quant.market.application.dto.StockRelationDTO;
import com.quant.market.application.service.StockRelationService;
import com.quant.market.domain.model.Stock;
import com.quant.market.domain.model.StockRelation;
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
import java.util.stream.Collectors;

/**
 * Stock Relation REST Controller
 * 股票关联关系控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/stock-relations")
@RequiredArgsConstructor
public class StockRelationController {

    private final StockRelationService stockRelationService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 从请求头中获取当前登录用户ID
     */
    private Long getCurrentUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Invalid token");
        }
        return userId;
    }

    /**
     * 添加关注（用户关注股票）
     * POST /api/stock-relations/follow
     *
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Request body:
     * {
     *   "stockCode": "600000"
     * }
     */
    @PostMapping("/follow")
    public Result<StockRelationDTO> followStock(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid FollowStockRequest request) {
        Long userId = getCurrentUserId(authHeader);
        log.info("REST request to follow stock: userId={}, stockCode={}", userId, request.getStockCode());

        StockRelation relation = stockRelationService.followStock(userId, request.getStockCode());

        return Result.success(StockRelationDTO.fromDomain(relation));
    }

    /**
     * 取消关注（用户取消关注股票）
     * DELETE /api/stock-relations/follow
     *
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Request params:
     * - stockCode: 股票代码
     */
    @DeleteMapping("/follow")
    public Result<Void> unfollowStock(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String stockCode) {
        Long userId = getCurrentUserId(authHeader);
        log.info("REST request to unfollow stock: userId={}, stockCode={}", userId, stockCode);

        stockRelationService.unfollowStock(userId, stockCode);

        return Result.success();
    }

    /**
     * 获取当前用户关注的股票列表（返回股票详细信息，支持分页）
     * GET /api/stock-relations/followed-stocks
     *
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Request params:
     * - page: 页码，从0开始，默认0
     * - size: 每页数量，默认10
     * - sort: 排序字段，默认按创建时间倒序，例如: createdAt,desc
     *
     * Response:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "content": [
     *       {
     *         "id": 1,
     *         "exchange": "SSE",
     *         "stockCode": "600000",
     *         "stockName": "浦发银行",
     *         "listingDate": "1999-11-10",
     *         "industry": "银行",
     *         "status": "L",
     *         "area": "上海",
     *         ...
     *       }
     *     ],
     *     "pageNumber": 0,
     *     "pageSize": 10,
     *     "totalElements": 25,
     *     "totalPages": 3,
     *     "last": false
     *   }
     * }
     */
    @GetMapping("/followed-stocks")
    public PageResult<StockDTO> getFollowedStocks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        Long userId = getCurrentUserId(authHeader);
        log.info("REST request to get followed stocks for user: {}, page: {}, size: {}", userId, page, size);

        // 解析排序参数
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        // 获取关注的股票详细信息（分页）
        Page<Stock> stockPage = stockRelationService.getUserFollowedStocksWithDetails(userId, pageable);

        // 转换为 DTO
        List<StockDTO> stockDTOList = stockPage.getContent().stream()
                .map(StockDTO::fromDomain)
                .collect(Collectors.toList());

        // 构建分页结果
        return PageResult.success(
                stockDTOList,
                stockPage.getNumber(),
                stockPage.getSize(),
                stockPage.getTotalElements()
        );
    }

    /**
     * 检查当前用户是否关注了某个股票
     * GET /api/stock-relations/is-following
     *
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Request params:
     * - stockCode: 股票代码
     *
     * Response:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "isFollowing": true
     *   }
     * }
     */
    @GetMapping("/is-following")
    public Result<Map<String, Boolean>> isFollowing(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String stockCode) {
        Long userId = getCurrentUserId(authHeader);
        log.info("REST request to check if user {} is following stock {}", userId, stockCode);

        boolean isFollowing = stockRelationService.isFollowing(userId, stockCode);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFollowing", isFollowing);

        return Result.success(result);
    }

    /**
     * 获取当前用户关注的股票数量
     * GET /api/stock-relations/followed-stocks/count
     *
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Response:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "count": 5
     *   }
     * }
     */
    @GetMapping("/followed-stocks/count")
    public Result<Map<String, Long>> getFollowedStocksCount(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        log.info("REST request to get followed stocks count for user: {}", userId);

        long count = stockRelationService.countUserFollowedStocks(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);

        return Result.success(result);
    }
}
