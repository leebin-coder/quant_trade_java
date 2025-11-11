package com.quant.market.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Trading Calendar JPA Entity
 * 交易日历表
 */
@Data
@Entity
@Table(name = "t_trading_calendar",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_trading_calendar_date",
           columnNames = {"trade_date"}
       ))
public class TradingCalendarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "is_trading_day", nullable = false, columnDefinition = "SMALLINT")
    private Short isTradingDay;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
