package com.nadia.mqhub.consumer.mq;

import com.nadia.mqhub.consumer.mq.config.ConsumerConfig;

/**
 * @author xiang.shi
 * @date 2020/4/14 3:46 下午
 */
public interface MQMessageConsumerBuilder {
    void start(ConsumerConfig consumerConfig);
}
