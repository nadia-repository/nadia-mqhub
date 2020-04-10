package com.nadia.mqhub.job.processor;

import com.nadia.mqhub.common.utils.ThreadFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author xiang.shi
 * @date 2020/4/3 1:49 下午
 */
@Component
@Slf4j
public class PurgeJobProcessor implements JobProcessor {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
            "PurgeJobProcessorScheduledThread"));

    @Override
    public void processor() {
        log.info("PurgeJobProcessor schedule start");
    }
}
