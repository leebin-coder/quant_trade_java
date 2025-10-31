package com.quant.trade.domain.model;

import com.quant.common.domain.AggregateRoot;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Order Aggregate Root
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends AggregateRoot {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String symbol;
    private OrderType type;
    private OrderSide side;
    private BigDecimal price;
    private BigDecimal quantity;
    private OrderStatus status;

    public enum OrderType {
        MARKET,
        LIMIT,
        STOP
    }

    public enum OrderSide {
        BUY,
        SELL
    }

    public enum OrderStatus {
        PENDING,
        FILLED,
        PARTIALLY_FILLED,
        CANCELLED,
        REJECTED
    }
}
