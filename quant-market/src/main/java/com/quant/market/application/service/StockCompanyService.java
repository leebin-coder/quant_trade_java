package com.quant.market.application.service;

import com.quant.common.exception.BusinessException;
import com.quant.common.response.ResultCode;
import com.quant.market.application.dto.BatchUpsertCompanyRequest;
import com.quant.market.application.dto.StockCompanyDTO;
import com.quant.market.domain.model.StockCompany;
import com.quant.market.domain.repository.StockCompanyRepository;
import com.quant.market.infrastructure.persistence.entity.StockCompanyEntity;
import com.quant.market.infrastructure.persistence.repository.StockCompanyBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stock Company Application Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockCompanyService {

    private final StockCompanyRepository companyRepository;
    private final StockCompanyBatchRepository batchRepository;

    /**
     * Get company by stock code
     */
    @Transactional(readOnly = true)
    public StockCompanyDTO getByStockCode(String stockCode) {
        log.info("Getting company by stock code: {}", stockCode);

        return companyRepository.findByStockCode(stockCode)
                .map(StockCompanyDTO::fromDomain)
                .orElseThrow(() -> new BusinessException(
                        ResultCode.NOT_FOUND.getCode(),
                        "Company not found for stock code: " + stockCode));
    }

    /**
     * Batch upsert companies (Insert or Update) - Optimized Version
     * Maximum 1000 items per batch
     *
     * Uses PostgreSQL ON CONFLICT for high-performance batch operations:
     * - Single SQL statement for all records
     * - Automatic insert/update decision by database
     * - Much faster than individual queries
     *
     * Uniqueness is determined by 5 fields:
     * - stock_code (股票代码)
     * - com_name (公司全称)
     * - com_id (统一社会信用代码)
     * - chairman (法人代表)
     * - exchange (交易所代码)
     */
    @Transactional
    public BatchUpsertResult batchUpsertCompanies(List<BatchUpsertCompanyRequest> requests) {
        log.info("Starting batch upsert companies: {} items", requests.size());

        // Validate batch size
        if (requests.size() > 1000) {
            throw new BusinessException(
                    ResultCode.VALIDATION_ERROR.getCode(),
                    "Batch size cannot exceed 1000 items. Received: " + requests.size());
        }

        long startTime = System.currentTimeMillis();
        BatchUpsertResult result = new BatchUpsertResult();
        result.setTotal(requests.size());

        List<String> errors = new ArrayList<>();
        List<StockCompanyEntity> validEntities = new ArrayList<>();

        // Validate and convert to entities
        for (int i = 0; i < requests.size(); i++) {
            BatchUpsertCompanyRequest request = requests.get(i);
            try {
                StockCompany domain = request.toDomain();
                StockCompanyEntity entity = StockCompanyEntity.fromDomain(domain);
                validEntities.add(entity);
            } catch (Exception e) {
                String errorMsg = String.format("Row %d: %s.%s - %s",
                        i + 1, request.getExchange(), request.getStockCode(), e.getMessage());
                errors.add(errorMsg);
                log.error("Error processing company at row {}: {}", i + 1, e.getMessage());
            }
        }

        // Perform batch upsert using native SQL
        int affectedRows = 0;
        if (!validEntities.isEmpty()) {
            try {
                affectedRows = batchRepository.batchUpsert(validEntities);
                log.info("Batch upsert affected {} rows", affectedRows);
            } catch (Exception e) {
                log.error("Error during batch upsert", e);
                errors.add("Batch upsert error: " + e.getMessage());
            }
        }

        // Note: We can't distinguish between inserts and updates with ON CONFLICT
        // So we report all successful operations as "upserted"
        result.setInserted(affectedRows); // Actually means "upserted"
        result.setUpdated(0); // Not tracked separately in batch mode
        result.setFailed(errors.size());
        result.setErrors(errors);

        long duration = System.currentTimeMillis() - startTime;
        result.setProcessingTimeMs(duration);

        log.info("Batch upsert completed: total={}, upserted={}, failed={}, time={}ms",
                result.getTotal(), affectedRows, result.getFailed(), duration);

        return result;
    }

    /**
     * Get all companies
     */
    @Transactional(readOnly = true)
    public List<StockCompanyDTO> getAllCompanies() {
        log.info("Getting all companies");

        return companyRepository.findAll().stream()
                .map(StockCompanyDTO::fromDomain)
                .toList();
    }

    /**
     * Get companies by exchange
     */
    @Transactional(readOnly = true)
    public List<StockCompanyDTO> getCompaniesByExchange(String exchange) {
        log.info("Getting companies by exchange: {}", exchange);

        return companyRepository.findByExchange(exchange).stream()
                .map(StockCompanyDTO::fromDomain)
                .toList();
    }

    /**
     * Get company count
     */
    @Transactional(readOnly = true)
    public long getCompanyCount() {
        return companyRepository.count();
    }

    /**
     * Batch Upsert Result DTO
     */
    @lombok.Data
    public static class BatchUpsertResult {
        private int total;
        private int inserted;
        private int updated;
        private int failed;
        private List<String> errors = new ArrayList<>();
        private long processingTimeMs;

        public String getSummary() {
            return String.format("Total: %d, Inserted: %d, Updated: %d, Failed: %d, Time: %dms",
                    total, inserted, updated, failed, processingTimeMs);
        }
    }
}
