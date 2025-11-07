package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Daily Query Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyQueryRequest {

    /**
     * Stock code (required)
     */
    @NotBlank(message = "Stock code is required")
    private String stockCode;

    /**
     * Start date (optional)
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * End date (optional)
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Sort order (optional)
     * asc = ascending (oldest first)
     * desc = descending (newest first)
     * Default: desc
     */
    private String sortOrder = "desc";

    /**
     * Check if sort order is ascending
     */
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(sortOrder);
    }
}
