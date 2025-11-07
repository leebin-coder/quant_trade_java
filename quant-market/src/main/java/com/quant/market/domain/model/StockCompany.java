package com.quant.market.domain.model;

import com.quant.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Company Domain Model
 * Reference: https://tushare.pro/document/2?doc_id=112
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class StockCompany extends BaseEntity {

    private Long id;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 公司全称
     */
    private String comName;

    /**
     * 统一社会信用代码
     */
    private String comId;

    /**
     * 交易所代码
     */
    private String exchange;

    /**
     * 法人代表
     */
    private String chairman;

    /**
     * 总经理
     */
    private String manager;

    /**
     * 董秘
     */
    private String secretary;

    /**
     * 注册资本(万元)
     */
    private BigDecimal regCapital;

    /**
     * 注册日期
     */
    private LocalDate setupDate;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 公司介绍
     */
    private String introduction;

    /**
     * 公司主页
     */
    private String website;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 办公室地址
     */
    private String office;

    /**
     * 员工人数
     */
    private Integer employees;

    /**
     * 主要业务及产品
     */
    private String mainBusiness;

    /**
     * 经营范围
     */
    private String businessScope;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Get full stock code with exchange prefix
     */
    public String getFullCode() {
        return exchange + "." + stockCode;
    }
}
