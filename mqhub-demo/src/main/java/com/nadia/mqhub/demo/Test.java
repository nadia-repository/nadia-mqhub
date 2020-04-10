package com.nadia.mqhub.demo;

import com.nadia.mqhub.common.domain.MqType;
import com.nadia.mqhub.common.entity.MqClientMessageEntity;
import com.nadia.mqhub.mq.MqClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @param <T>
 * @author xiang.shi
 * @date 2020/4/8 6:33 下午
 */
@Component
public class Test {

    @Autowired
    private MqClient mqClient;

    public void send(){
        MqClientMessageEntity mqClientMessageEntity = new MqClientMessageEntity();
        mqClientMessageEntity.setMqType(MqType.RMQ.name());
        mqClientMessageEntity.setTopic("Mqhub-test");
        mqClientMessageEntity.setMessage("11111111");
        mqClient.send(mqClientMessageEntity);
    }
}
