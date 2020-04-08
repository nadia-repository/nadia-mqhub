package com.nadia.mqhub.job;

import com.nadia.mqhub.job.processor.JobProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2020/4/3 11:30 上午
 */
@Component
@Slf4j
public class JobFactory {

    @Autowired
    private List<JobProcessor> jobProcessors;

    @PostConstruct
    public void excutuor() {
        log.info("JobFactory excutuor start");
        if (!CollectionUtils.isEmpty(jobProcessors)) {
            jobProcessors.forEach(jobProcessor -> {
                jobProcessor.processor();
            });
        }
    }
}
