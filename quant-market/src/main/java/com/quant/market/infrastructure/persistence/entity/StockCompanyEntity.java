package com.quant.market.infrastructure.persistence.entity;

import com.quant.market.domain.model.StockCompany;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stock Company JPA Entity
 * Reference: https://tushare.pro/document/2?doc_id=112
 */
@Data
@Entity
@Table(name = "t_stock_company",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_company_info",
           columnNames = {"stock_code", "com_name", "com_id", "chairman", "exchange"}
       ))
public class StockCompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "com_name", nullable = false, length = 200)
    private String comName;

    @Column(name = "com_id", nullable = false, length = 50)
    private String comId;

    @Column(name = "exchange", nullable = false, length = 20)
    private String exchange;

    @Column(name = "chairman", length = 100)
    private String chairman;

    @Column(name = "manager", length = 100)
    private String manager;

    @Column(name = "secretary", length = 100)
    private String secretary;

    @Column(name = "reg_capital", precision = 20, scale = 4)
    private BigDecimal regCapital;

    @Column(name = "setup_date")
    private LocalDate setupDate;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "office", length = 500)
    private String office;

    @Column(name = "employees")
    private Integer employees;

    @Column(name = "main_business", columnDefinition = "TEXT")
    private String mainBusiness;

    @Column(name = "business_scope", columnDefinition = "TEXT")
    private String businessScope;

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

    /**
     * Create from domain model
     */
    public static StockCompanyEntity fromDomain(StockCompany company) {
        StockCompanyEntity entity = new StockCompanyEntity();
        entity.setId(company.getId());
        entity.setStockCode(company.getStockCode());
        entity.setComName(company.getComName());
        entity.setComId(company.getComId());
        entity.setExchange(company.getExchange());
        entity.setChairman(company.getChairman());
        entity.setManager(company.getManager());
        entity.setSecretary(company.getSecretary());
        entity.setRegCapital(company.getRegCapital());
        entity.setSetupDate(company.getSetupDate());
        entity.setProvince(company.getProvince());
        entity.setCity(company.getCity());
        entity.setIntroduction(company.getIntroduction());
        entity.setWebsite(company.getWebsite());
        entity.setEmail(company.getEmail());
        entity.setOffice(company.getOffice());
        entity.setEmployees(company.getEmployees());
        entity.setMainBusiness(company.getMainBusiness());
        entity.setBusinessScope(company.getBusinessScope());
        entity.setCreatedAt(company.getCreatedAt());
        entity.setUpdatedAt(company.getUpdatedAt());
        return entity;
    }
}
