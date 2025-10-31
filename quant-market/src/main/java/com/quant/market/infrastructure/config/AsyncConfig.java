package com.quant.market.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async Task Executor Configuration
 * For high-performance batch operations
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Thread pool for stock batch operations
     *
     * Configuration:
     * - Core pool size: 5 (minimum threads always alive)
     * - Max pool size: 10 (maximum concurrent threads)
     * - Queue capacity: 100 (pending task queue size)
     * - Rejection policy: CallerRunsPolicy (run in caller thread if queue is full)
     * - Keep alive: 60s (idle thread timeout)
     *
     * @return Thread pool executor
     */
    @Bean(name = "stockBatchExecutor")
    public Executor stockBatchExecutor() {
        log.info("Initializing stock batch executor");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size - minimum threads
        executor.setCorePoolSize(5);

        // Max pool size - maximum threads
        executor.setMaxPoolSize(10);

        // Queue capacity - pending tasks
        executor.setQueueCapacity(100);

        // Thread name prefix
        executor.setThreadNamePrefix("stock-batch-");

        // Keep alive time for idle threads
        executor.setKeepAliveSeconds(60);

        // Rejection policy when queue is full
        // CallerRunsPolicy: run in the caller's thread (provides backpressure)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Max wait time on shutdown
        executor.setAwaitTerminationSeconds(60);

        // Initialize
        executor.initialize();

        log.info("Stock batch executor initialized: corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }
}
