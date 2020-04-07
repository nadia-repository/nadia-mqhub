package com.nadia.mqhub.mq;

import com.nadia.mqhub.domain.MqResult;
import com.nadia.mqhub.entity.MqClientMessageEntity;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * @author xiang.shi
 * @date 2020/4/3 1:57 下午
 */
public interface MqMessageProducer {

    MqResult send(MqClientMessageEntity mqClientMessageEntity) throws Exception;

    void send(MqClientMessageEntity mqClientMessageEntity,Callback callback) throws Exception;
}
