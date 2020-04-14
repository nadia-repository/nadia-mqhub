package com.nadia.mqhub.consumer.mq;

import com.nadia.mqhub.consumer.mq.config.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author xiang.shi
 * @date 2020/4/14 4:38 下午
 */
@Component
public class MqConsumerInitializer {

    @Autowired
    private List<MQMessageConsumerBuilder> mqMessageConsumerBuilders;
    @Autowired
    private ConsumerConfig consumerConfig;

    @PostConstruct
    public void init(){
        if(!CollectionUtils.isEmpty(mqMessageConsumerBuilders)){
            mqMessageConsumerBuilders.forEach(mqMessageConsumerBuilder -> {mqMessageConsumerBuilder.start(consumerConfig);});
        }
    }

}
