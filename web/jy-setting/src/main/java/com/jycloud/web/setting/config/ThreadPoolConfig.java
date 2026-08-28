package com.jycloud.web.setting.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 5;

    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 10;

    /**
     * 空闲线程存活时间（秒）
     */
    private static final int KEEP_ALIVE_SECONDS = 10;

    /**
     * 缓冲队列大小
     */
    private static final int QUEUE_CAPACITY = 200;

    /**
     * 线程池名前缀
     */
    private static final String THREAD_NAME_PREFIX = "jy-setting-task-";

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return TtlExecutors.getTtlExecutor(threadPoolTaskExecutor());
    }
}
