package com.nadia.mqhub.mq.rmq;

import com.nadia.mqhub.common.annotation.ProducerType;
import com.nadia.mqhub.common.domain.MqResult;
import com.nadia.mqhub.common.domain.MqType;
import com.nadia.mqhub.config.ProducerConfig;
import com.nadia.mqhub.common.entity.MqClientMessageEntity;
import com.nadia.mqhub.mq.Callback;
import com.nadia.mqhub.mq.MqMessageProducer;
import com.nadia.mqhub.common.utils.MqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;

/**
 * @author xiang.shi
 * @date 2020/4/3 2:00 下午
 */
@Service
@Slf4j
@ProducerType(MqType.RMQ)
public class RMQMessageProducer implements MqMessageProducer {

    private  DefaultMQProducer mqMessageProducer;

    @Autowired
    private ProducerConfig producerConfig;

    @PostConstruct
    private void init() {
        mqMessageProducer = new DefaultMQProducer(producerConfig.getProducerGroupName());
        mqMessageProducer.setRetryTimesWhenSendFailed(MqUtils.RMQ_MAX_RETRY_TIMES);
        mqMessageProducer.setNamesrvAddr(producerConfig.getNameSrv());

        try {
            mqMessageProducer.start();
        } catch (Throwable t) {
            log.error("Fail to start the mqMessageProducer");
            throw new RuntimeException(t);
        }
    }

    @Override
    public MqResult send(MqClientMessageEntity mqClientMessage) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(mqClientMessage.getTopic(), mqClientMessage.getTags(),
                mqClientMessage.getMessage().getBytes(RemotingHelper.DEFAULT_CHARSET));
        message.setKeys(mqClientMessage.getMessageKey());

        // put the message header
        if (StringUtils.isNotBlank(mqClientMessage.getMessageHeader())) {
            message.putUserProperty(MqUtils.HEAD, mqClientMessage.getMessageHeader());
        }

        SendResult result;
        if (producerConfig.getCustomizedMQSelector() == null) {
            result = mqMessageProducer.send(message);
        } else {
            result = mqMessageProducer.send(message, producerConfig.getCustomizedMQSelector(), mqClientMessage.getSendOpts());
        }
        log.info("Send message result, id={}, status={}", result.getMsgId(), result.getSendStatus());
        MqResult mqResult = new MqResult();
        mqResult.setMsgId(result.getMsgId());
        mqResult.setSendStatus(result.getSendStatus().name());
        return mqResult;
    }

    @Override
    public void send(MqClientMessageEntity mqClientMessageEntity, Callback callback) throws Exception {

    }

}
