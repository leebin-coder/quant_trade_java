package com.quant.market.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Stock Query Request
 * Support multiple filtering conditions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQueryRequest {

    /**
     * Listing date range - start date
     */
    private LocalDate listingDateFrom;

    /**
     * Listing date range - end date
     */
    private LocalDate listingDateTo;

    /**
     * Fuzzy search keyword (match stock code, stock name, or company name)
     */
    private String keyword;

    /**
     * Stock status list (multi-select)
     * Available values: LISTED, DELISTED, SUSPENDED
     */
    private List<String> statuses;

    /**
     * Industry list (multi-select)
     */
    private List<String> industries;

    /**
     * Exchange list (multi-select)
     * Available values: SH, SZ, BJ, HK, US
     */
    private List<String> exchanges;

    /**
     * Page number (0-based), null means no pagination (return all)
     */
    private Integer page;

    /**
     * Page size, null means no pagination (return all)
     */
    private Integer size;

    /**
     * Sort by field, default: createdAt
     */
    private String sortBy;

    /**
     * Sort direction (asc/desc), default: desc
     */
    private String sortDir;
}
