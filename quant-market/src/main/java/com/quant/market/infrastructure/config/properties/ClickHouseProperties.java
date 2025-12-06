package com.quant.market.infrastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClickHouse connection properties loaded from application configuration.
 */
@Data
@ConfigurationProperties(prefix = "clickhouse")
public class ClickHouseProperties {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
}
