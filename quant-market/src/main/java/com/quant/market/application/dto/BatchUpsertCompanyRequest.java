package com.quant.market.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quant.market.domain.model.StockCompany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Batch Upsert Company Request
 * Used for batch insert or update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpsertCompanyRequest {

    @NotBlank(message = "Stock code is required")
    private String stockCode;

    @NotBlank(message = "Company name is required")
    private String comName;

    @NotBlank(message = "Company ID is required")
    private String comId;

    @NotBlank(message = "Exchange is required")
    private String exchange;

    @NotNull(message = "Chairman is required")
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

    /**
     * Convert to domain model
     */
    public StockCompany toDomain() {
        return StockCompany.builder()
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
                .build();
    }
}
