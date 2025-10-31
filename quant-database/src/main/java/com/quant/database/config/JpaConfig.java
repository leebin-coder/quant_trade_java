package com.quant.database.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration
 */
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
}
