package com.nadia.mqhub.mq;

import com.nadia.mqhub.common.domain.MqResult;
import com.nadia.mqhub.common.entity.MqClientMessageEntity;

/**
 * @author xiang.shi
 * @date 2020/4/3 1:57 下午
 */
public interface MqMessageProducer {

    MqResult send(MqClientMessageEntity mqClientMessageEntity) throws Exception;

    void send(MqClientMessageEntity mqClientMessageEntity, Callback callback) throws Exception;

}
