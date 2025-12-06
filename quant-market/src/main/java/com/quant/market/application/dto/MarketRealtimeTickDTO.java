package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for realtime tick data queried from ClickHouse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketRealtimeTickDTO {

    private String tsCode;
    private String name;
    private Double trade;
    private Double price;
    private Double open;
    private Double high;
    private Double low;
    private Double preClose;
    private Double bid;
    private Double ask;
    private Double volume;
    private Double amount;
    private Double b1V;
    private Double b1P;
    private Double b2V;
    private Double b2P;
    private Double b3V;
    private Double b3P;
    private Double b4V;
    private Double b4P;
    private Double b5V;
    private Double b5P;
    private Double a1V;
    private Double a1P;
    private Double a2V;
    private Double a2P;
    private Double a3V;
    private Double a3P;
    private Double a4V;
    private Double a4P;
    private Double a5V;
    private Double a5P;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime time;
    private String source;
    private String rawJson;
}
