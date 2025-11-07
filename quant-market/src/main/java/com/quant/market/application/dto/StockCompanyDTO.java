package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quant.market.domain.model.StockCompany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Company DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCompanyDTO {

    private Long id;

    private String stockCode;

    private String comName;

    private String comId;

    private String exchange;

    private String chairman;

    private String manager;

    private String secretary;

    private BigDecimal regCapital;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate setupDate;

    private String province;

    private String city;

    private String introduction;

    private String website;

    private String email;

    private String office;

    private Integer employees;

    private String mainBusiness;

    private String businessScope;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Convert from domain model
     */
    public static StockCompanyDTO fromDomain(StockCompany company) {
        return StockCompanyDTO.builder()
                .id(company.getId())
                .stockCode(company.getStockCode())
                .comName(company.getComName())
                .comId(company.getComId())
                .exchange(company.getExchange())
                .chairman(company.getChairman())
                .manager(company.getManager())
                .secretary(company.getSecretary())
                .regCapital(company.getRegCapital())
                .setupDate(company.getSetupDate())
                .province(company.getProvince())
                .city(company.getCity())
                .introduction(company.getIntroduction())
                .website(company.getWebsite())
                .email(company.getEmail())
                .office(company.getOffice())
                .employees(company.getEmployees())
                .mainBusiness(company.getMainBusiness())
                .businessScope(company.getBusinessScope())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    /**
     * Convert to domain model
     */
    public StockCompany toDomain() {
        return StockCompany.builder()
                .id(id)
                .stockCode(stockCode)
                .comName(comName)
                .comId(comId)
                .exchange(exchange)
                .chairman(chairman)
                .manager(manager)
                .secretary(secretary)
                .regCapital(regCapital)
                .setupDate(setupDate)
                .province(province)
                .city(city)
                .introduction(introduction)
                .website(website)
                .email(email)
                .office(office)
                .employees(employees)
                .mainBusiness(mainBusiness)
                .businessScope(businessScope)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
