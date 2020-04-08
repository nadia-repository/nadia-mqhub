package com.nadia.mqhub.job.processor;

import com.nadia.mqhub.common.ThreadFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author xiang.shi
 * @date 2020/4/3 11:27 上午
 */
@Component
@Slf4j
public class FailoverJobProcessor implements JobProcessor {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
            "FailoverJobProcessorScheduledThread"));
    @Override
    public void processor() {
        log.info("FailoverJobProcessor schedule start");
    }
}
