package com.quant.market.infrastructure.config;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.quant.market.infrastructure.config.properties.ClickHouseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.sql.SQLException;
import java.util.Properties;

/**
 * ClickHouse client configuration and tick streaming scheduler.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ClickHouseProperties.class)
public class ClickHouseConfig {

    /**
     * JdbcTemplate dedicated to ClickHouse queries.
     */
    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(ClickHouseProperties properties) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", properties.getUsername());
        props.setProperty("password", properties.getPassword());
        props.setProperty("socket_timeout", "30000");
        props.setProperty("connect_timeout", "30000");
        props.setProperty("compress", "0");

        String url = String.format("jdbc:clickhouse://%s:%d/%s",
                properties.getHost(),
                properties.getPort(),
                properties.getDatabase());

        log.info("Initializing ClickHouse datasource: {}", url);
        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, props);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(1000);
        return jdbcTemplate;
    }

    /**
     * Scheduler for streaming tasks (polling every few seconds).
     */
    @Bean
    public ThreadPoolTaskScheduler tickStreamScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("tick-stream-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
