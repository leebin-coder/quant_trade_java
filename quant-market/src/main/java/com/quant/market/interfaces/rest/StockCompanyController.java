package com.quant.market.interfaces.rest;

import com.quant.common.response.Result;
import com.quant.market.application.dto.BatchUpsertCompanyRequest;
import com.quant.market.application.dto.StockCompanyDTO;
import com.quant.market.application.service.StockCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stock Company REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/stock-companies")
@RequiredArgsConstructor
public class StockCompanyController {

    private final StockCompanyService companyService;

    /**
     * Get company by stock code
     * GET /api/stock-companies/{stockCode}
     *
     * Example: GET /api/stock-companies/600000
     *
     * @param stockCode Stock code (e.g., 600000, 000001)
     * @return Company information
     */
    @GetMapping("/{stockCode}")
    public Result<StockCompanyDTO> getCompanyByStockCode(@PathVariable("stockCode") String stockCode) {
        log.info("REST request to get company by stock code: {}", stockCode);
        StockCompanyDTO company = companyService.getByStockCode(stockCode);
        return Result.success(company);
    }

    /**
     * Batch upsert companies (Insert or Update)
     * POST /api/stock-companies/batch-upsert
     *
     * Features:
     * - Maximum 1000 items per batch
     * - Automatically determines insert or update based on unique fields
     * - Unique fields: stockCode, comName, comId, chairman, exchange
     * - Returns detailed result with insert/update counts
     *
     * Request body example:
     * [
     *   {
     *     "stockCode": "600000",
     *     "comName": "上海浦东发展银行股份有限公司",
     *     "comId": "91310000100010609G",
     *     "exchange": "SSE",
     *     "chairman": "郑杨",
     *     "manager": "潘卫东",
     *     "secretary": "张文",
     *     "regCapital": 2935480.0952,
     *     "setupDate": "1992-10-19",
     *     "province": "上海市",
     *     "city": "上海市",
     *     "introduction": "上海浦东发展银行股份有限公司（以下简称：浦发银行）...",
     *     "website": "http://www.spdb.com.cn",
     *     "email": "csr@spdb.com.cn",
     *     "office": "上海市中山东一路12号",
     *     "employees": 58647,
     *     "mainBusiness": "吸收公众存款；发放短期、中期和长期贷款...",
     *     "businessScope": "吸收公众存款；发放短期、中期和长期贷款..."
     *   },
     *   {
     *     "stockCode": "000001",
     *     "comName": "平安银行股份有限公司",
     *     "comId": "91440300100018536E",
     *     "exchange": "SZSE",
     *     "chairman": "谢永林",
     *     "manager": "胡跃飞",
     *     "secretary": "周强",
     *     "regCapital": 1943561.6104,
     *     "setupDate": "1987-12-22",
     *     "province": "广东省",
     *     "city": "深圳市",
     *     "website": "http://bank.pingan.com",
     *     "employees": 36220
     *   }
     * ]
     *
     * @param requests List of company data (max 1000 items)
     * @return Batch upsert result with statistics
     */
    @PostMapping("/batch-upsert")
    public Result<Map<String, Object>> batchUpsertCompanies(
            @RequestBody @Valid List<BatchUpsertCompanyRequest> requests) {
        log.info("REST request to batch upsert companies: {} items", requests.size());

        if (requests.isEmpty()) {
            return Result.error(400, "Request list cannot be empty");
        }

        if (requests.size() > 1000) {
            return Result.error(400, "Batch size cannot exceed 1000 items. Received: " + requests.size());
        }

        // Process batch upsert
        StockCompanyService.BatchUpsertResult result = companyService.batchUpsertCompanies(requests);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());
        response.put("inserted", result.getInserted());
        response.put("updated", result.getUpdated());
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
     * Get all companies
     * GET /api/stock-companies
     *
     * @return List of all companies
     */
    @GetMapping
    public Result<List<StockCompanyDTO>> getAllCompanies() {
        log.info("REST request to get all companies");
        List<StockCompanyDTO> companies = companyService.getAllCompanies();
        return Result.success(companies);
    }

    /**
     * Get companies by exchange
     * GET /api/stock-companies/exchange/{exchange}
     *
     * @param exchange Exchange code (SSE, SZSE, BSE, HKEX)
     * @return List of companies in the exchange
     */
    @GetMapping("/exchange/{exchange}")
    public Result<List<StockCompanyDTO>> getCompaniesByExchange(@PathVariable("exchange") String exchange) {
        log.info("REST request to get companies by exchange: {}", exchange);
        List<StockCompanyDTO> companies = companyService.getCompaniesByExchange(exchange.toUpperCase());
        return Result.success(companies);
    }

    /**
     * Get company statistics
     * GET /api/stock-companies/stats
     *
     * @return Statistics about stored companies
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCompanyStats() {
        log.info("REST request to get company statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", companyService.getCompanyCount());

        return Result.success(stats);
    }
}
