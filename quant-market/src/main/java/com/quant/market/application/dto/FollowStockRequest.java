package com.quant.market.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Follow Stock Request DTO
 * 关注股票请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStockRequest {

    /**
     * 股票代码
     */
    @NotBlank(message = "Stock code cannot be blank")
    private String stockCode;
}
