package com.nadia.mqhub.config;

import com.nadia.mqhub.common.ThreadFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiang.shi
 * @date 2020/4/3 2:35 下午
 */
@Configuration
public class ExcutorServiceConfig {

    @Bean("workQueueExecutor")
    public ExecutorService getWorkQueueExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                5,
                300, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(2000),
                new ThreadFactoryImpl("workQueueExecutor"),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executor;
    }

}
