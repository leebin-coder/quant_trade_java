package com.quant.market.infrastructure.persistence.repository;

import com.quant.market.infrastructure.persistence.entity.StockCompanyEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

/**
 * Stock Company Batch Repository
 * High-performance batch upsert using native SQL and JDBC batch operations
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class StockCompanyBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Batch upsert companies using PostgreSQL ON CONFLICT
     * This is much faster than individual insert/update operations
     *
     * @param entities List of company entities to upsert
     * @return Number of rows affected
     */
    @Transactional
    public int batchUpsert(List<StockCompanyEntity> entities) {
        if (entities.isEmpty()) {
            return 0;
        }

        String sql = """
            INSERT INTO t_stock_company (
                stock_code, com_name, com_id, exchange, chairman,
                manager, secretary, reg_capital, setup_date,
                province, city, introduction, website, email,
                office, employees, main_business, business_scope,
                created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (stock_code, com_name, com_id, chairman, exchange)
            DO UPDATE SET
                manager = EXCLUDED.manager,
                secretary = EXCLUDED.secretary,
                reg_capital = EXCLUDED.reg_capital,
                setup_date = EXCLUDED.setup_date,
                province = EXCLUDED.province,
                city = EXCLUDED.city,
                introduction = EXCLUDED.introduction,
                website = EXCLUDED.website,
                email = EXCLUDED.email,
                office = EXCLUDED.office,
                employees = EXCLUDED.employees,
                main_business = EXCLUDED.main_business,
                business_scope = EXCLUDED.business_scope,
                updated_at = CURRENT_TIMESTAMP
            """;

        int[][] results = jdbcTemplate.batchUpdate(sql, entities, entities.size(),
            (PreparedStatement ps, StockCompanyEntity entity) -> {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                ps.setString(1, entity.getStockCode());
                ps.setString(2, entity.getComName());
                ps.setString(3, entity.getComId());
                ps.setString(4, entity.getExchange());
                ps.setString(5, entity.getChairman());
                ps.setString(6, entity.getManager());
                ps.setString(7, entity.getSecretary());
                ps.setBigDecimal(8, entity.getRegCapital());
                ps.setObject(9, entity.getSetupDate());
                ps.setString(10, entity.getProvince());
                ps.setString(11, entity.getCity());
                ps.setString(12, entity.getIntroduction());
                ps.setString(13, entity.getWebsite());
                ps.setString(14, entity.getEmail());
                ps.setString(15, entity.getOffice());
                ps.setObject(16, entity.getEmployees());
                ps.setString(17, entity.getMainBusiness());
                ps.setString(18, entity.getBusinessScope());
                ps.setTimestamp(19, now);
                ps.setTimestamp(20, now);
            });

        int totalAffected = 0;
        for (int[] result : results) {
            totalAffected += result.length;
        }

        log.info("Batch upsert completed: {} rows affected", totalAffected);
        return totalAffected;
    }
}
