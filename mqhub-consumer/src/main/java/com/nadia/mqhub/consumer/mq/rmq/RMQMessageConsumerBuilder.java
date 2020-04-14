package com.nadia.mqhub.consumer.mq.rmq;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.nadia.mqhub.common.domain.MqClientMessage;
import com.nadia.mqhub.consumer.mq.config.ConsumerConfig;
import com.nadia.mqhub.common.domain.MqClientConstants;
import com.nadia.mqhub.common.domain.MqType;
import com.nadia.mqhub.common.entity.MqClientIdempotentEntity;
import com.nadia.mqhub.common.mapper.MqClientIdempotentMapper;
import com.nadia.mqhub.consumer.mq.MQMessageConsumerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author xiang.shi
 * @date 2020/4/14 4:40 下午
 */
@Component
@Slf4j
public class RMQMessageConsumerBuilder implements MQMessageConsumerBuilder {

    @Autowired
    private MqClientIdempotentMapper mqClientIdempotentMapper;

    @Override
    public void start(ConsumerConfig consumerConfig) {
        log.info("RMQMessageConsumerBuilder, build the RMQ consumer, param={}", consumerConfig);
        for (Map.Entry<String, String> config : consumerConfig.getConsumerGroupTopicConf().entrySet()) {
            String consumerGroup = config.getKey();
            String topic = config.getValue();
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(consumerConfig.getNameSrv());
            List<String> tags = consumerConfig.getListenedTagsConf().get(config.getValue());
            try {
                if (tags == null || tags.isEmpty()) {
                    consumer.subscribe(topic, "*");
                } else {
                    consumer.subscribe(topic, Joiner.on(" || ").join(tags));
                }
            } catch (MQClientException e) {
                log.error("RMQMessageConsumerBuilder start Exception:{}",e);
            }
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.setInstanceName(UUID.randomUUID().toString());

            if(consumerConfig.isMsgListeningOrderly()){
                consumer.registerMessageListener((MessageListenerOrderly)(msgs, context)-> {
                    try {
                        handleMessage(msgs,consumerConfig);
                    } catch (UnsupportedEncodingException e) {
                        log.error("RMQMessageConsumerBuilder start Exception:{}",e);
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                });
            }else {
                consumer.registerMessageListener((MessageListenerConcurrently)(msgs, context)-> {
                    try {
                        handleMessage(msgs,consumerConfig);
                    } catch (UnsupportedEncodingException e) {
                        log.error("RMQMessageConsumerBuilder start Exception:{}",e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                });
            }
        }
    }

    private void handleMessage(List<MessageExt> msgs,ConsumerConfig consumerConfig) throws UnsupportedEncodingException {
        for (MessageExt msg : msgs) {
            String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
            log.info("RMQ consumer, tags={}, consumeTimes={}, msgId={}, msgBody={}, keys={}",
                    msg.getTags(), msg.getReconsumeTimes(),
                    msg.getMsgId(), messageBody, msg.getKeys());

            Map<String, Object> params = Maps.newHashMap();
            params.put("messageKey", msg.getKeys());
            params.put("mqType", MqType.RMQ.name());
            MqClientIdempotentEntity mqClientIdempotentEntity = mqClientIdempotentMapper.findByMessageKey(params);
            if (mqClientIdempotentEntity != null) {
                log.info("Exiting message has existed, messageKey={}", msg.getKeys());
                return;
            }

            mqClientIdempotentEntity = new MqClientIdempotentEntity();
            mqClientIdempotentEntity.setMessageId(msg.getMsgId());
            mqClientIdempotentEntity.setMessageKey(msg.getKeys());
            mqClientIdempotentEntity.setMessage(
                    messageBody.length() > MqClientConstants.MESSAGE_MAX_SIZE ?
                            "Message length too long" : messageBody);
            mqClientIdempotentEntity.setMessageType("TOPIC");
            mqClientIdempotentEntity.setDestination(msg.getTopic());
            mqClientIdempotentEntity.setTags(msg.getTags());
            mqClientIdempotentEntity.setMqType(MqType.RMQ.name());

            mqClientIdempotentMapper.insert(mqClientIdempotentEntity);

            MqClientMessage mqClientMessage = new MqClientMessage();
            mqClientMessage
                    .setMessage(messageBody)
                    .setMessageId(msg.getMsgId())
                    .setMessageKey(msg.getKeys())
                    .setDestination(msg.getTopic())
                    .setMessageType("TOPIC");
            consumerConfig.getMqMessageListener().onMessage(mqClientMessage);


        }
    }
}
