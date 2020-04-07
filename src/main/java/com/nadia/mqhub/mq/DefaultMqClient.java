package com.nadia.mqhub.mq;

import com.google.common.collect.Maps;
import com.nadia.mqhub.annotation.ProducerType;
import com.nadia.mqhub.common.AfterCommitTaskRegister;
import com.nadia.mqhub.common.ConcurrencyWrapper;
import com.nadia.mqhub.domain.MqType;
import com.nadia.mqhub.entity.MqClientMessageEntity;
import com.nadia.mqhub.domain.MqResult;
import com.nadia.mqhub.mapper.MqClientMessageMapper;
import jdk.nashorn.internal.ir.CallNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author xiang.shi
 * @date 2020/4/7 11:59 上午
 */
@Data
@Service
@Slf4j
public class DefaultMqClient implements MqClient {

    @Autowired
    private List<MqMessageProducer> mqMessageProducers;

    @Autowired
    private ExecutorService workQueueExecutor;

    @Autowired
    private MqClientMessageMapper mqClientMessageMapper;

    private Map<String, MqMessageProducer> mqMessageProducerMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        if(!CollectionUtils.isEmpty(mqMessageProducers)){
            mqMessageProducers.forEach(mqMessageProducer -> {
                ProducerType annotation = mqMessageProducer.getClass().getAnnotation(ProducerType.class);
                MqType value = annotation.value();
                mqMessageProducerMap.put(value.name(), mqMessageProducer);
            });
        }
    }

    @Override
    public void send(MqClientMessageEntity mqClientMessageEntity, Callback callback) {
        this.send(mqClientMessageEntity,callback,false);
    }

    @Override
    public void send(MqClientMessageEntity mqClientMessageEntity, Callback callback, boolean persistOn) {
        this.send(mqClientMessageEntity, callback, false, false);
    }

    @Override
    @Transactional
    public void send(MqClientMessageEntity mqClientMessageEntity, Callback callback, boolean persistOn, boolean local) {
        log.info("MqClientServiceImpl.send, param={}", mqClientMessageEntity);
        if(persistOn){
            mqClientMessageMapper.insertMessage(mqClientMessageEntity);
            log.info("insert mq client record successfully,id={}", mqClientMessageEntity.getId());
        }

        AfterCommitTaskRegister.registerTask(
                () -> workQueueExecutor.execute(
                        ConcurrencyWrapper.of(() -> doSend(mqClientMessageEntity,persistOn))
                )
        );
    }





    @Override
    public MqResult send(MqClientMessageEntity mqClientMessageEntity) {

        return null;
    }

    @Override
    public MqResult send(MqClientMessageEntity mqClientMessageEntity, boolean persistOn) {
        return null;
    }

    @Override
    public MqResult send(MqClientMessageEntity mqClientMessageEntity, boolean persistOn, boolean local) {
        return null;
    }

    private void doSend(MqClientMessageEntity mqClientMessageEntity,boolean persistOn){
        // send message
        log.info("Start to send mq with context={}", mqClientMessageEntity);

        Map<String, Object> params = Maps.newHashMap();
        params.put("mqClientMessageTable", mqClientConfig.getMqClientTableName());
        params.put("id", mqClientMessageEntity.getId());
        params.put("partitionKey", mqClientMessageEntity.getPartitionKey());
        try {
            MqMessageProducer mqMessageProducer = mqMessageProducerMap.get(mqClientMessageEntity.getMqType());
            if (mqMessageProducer == null) {
                log.error("Fail to get message producer for mqType={}", mqClientMessageEntity.getMqType());
                return;
            }
            MqResult result = mqMessageProducer.send(mqClientMessageEntity);
            if (SendStatus.SEND_OK.equals(result.getSendStatus())) {
                params.put("status", MessageClientStatus.SUCCESS.name());
                params.put("messageId", result.getMsgId());
            } else {
                params.put("status", MessageClientStatus.FAILED.name());
                params.put("nextRetryAt", new Date(new Date().getTime() + MqClientConstants.NEXT_RETRY_GAP));
            }
            log.info("doSend params={}", params);
        } catch (Throwable t) {
            log.error("Fail to send the message, params={}", params, t);

            params.put("status", MessageClientStatus.FAILED.name());
            params.put("nextRetryAt", new Date(new Date().getTime() + MqClientConstants.NEXT_RETRY_GAP));
        }

        if (persistOn) {
            log.info("Updating the message status with param={}", params);
            if (mqClientMessageContext.isFailoverCtx()) {
                params.put("retryCount", messageEntity.getRetryCount() + 1);
            }
            mqClientMessageMapper.updateMessageStatus(params);
        }
    }
}
