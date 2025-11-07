package com.quant.market.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Update Stock Request DTO
 */
@Data
public class UpdateStockRequest {

    private String stockName;
    private LocalDate listingDate;
    private String industry;
    private String status;
    private String area;
    private String fullName;
    private String enName;
    private String cnSpell;
    private String market;
    private String currType;
    private LocalDate delistDate;
    private String isHs;
    private String actName;
    private String actEntType;
}
