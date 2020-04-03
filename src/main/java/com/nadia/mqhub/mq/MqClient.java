package com.nadia.mqhub.mq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nadia.mqhub.annotation.ProducerType;
import com.nadia.mqhub.domain.MqClientMessageContext;
import com.nadia.mqhub.domain.MqType;
import com.nadia.mqhub.entity.MqClientMessageEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Data
@Service
@Slf4j
public class MqClient {

    @Autowired
    private List<MqMessageProducer> mqMessageProducers;
    @Autowired
    private ExecutorService workQueueExecutor;


    @Resource
    private MqClientConfig mqClientConfig;


    @Resource
    private MqClientMessageRepository mqClientMessageRepository;

    @Value("${eureka.instance.metadata-map.appVer:}")
    private String appVer;



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


    public void doSend(MqClientMessageContext mqClientMessageContext) {
        // send message
        log.info("Start to send mq with context={}", mqClientMessageContext);

        MqClientMessageEntity messageEntity = mqClientMessageContext.getMqClientMessageEntity();
        Map<String, Object> params = Maps.newHashMap();
        params.put("mqClientMessageTable", mqClientConfig.getMqClientTableName());
        params.put("id", messageEntity.getId());
        params.put("partitionKey", messageEntity.getPartitionKey());
        try {
            MqMessageProducer mqMessageProducer = mqMessageProducerMap.get(messageEntity.getMqType());
            if (mqMessageProducer == null) {
                log.error("Fail to get message producer for mqType={}", messageEntity.getMqType());
                return;
            }
            SendResult result = mqMessageProducer.send(messageEntity);
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

        if (mqClientMessageContext.isLoadFromDB()) {
            log.info("Updating the message status with param={}", params);
            if (mqClientMessageContext.isFailoverCtx()) {
                params.put("retryCount", messageEntity.getRetryCount() + 1);
            }
            mqClientMessageRepository.updateMessageStatus(params);
        }
    }

    @Transactional
    public void send(MqSendOption mqSendOption) {
        log.info("MqClientServiceImpl.send, param={}", mqSendOption);

        MqClientMessageEntity messageEntity = new MqClientMessageEntity();
        messageEntity.setBizId(mqSendOption.getBizId());
        messageEntity.setBizType(mqSendOption.getBizType());
        messageEntity.setMessage(mqSendOption.getMessage());
        messageEntity.setMessageType(mqSendOption.getMessageType().name());
        messageEntity.setDestination(mqSendOption.getDestination());
        messageEntity.setStatus(MessageClientStatus.NEW.name());
        messageEntity.setTags(mqSendOption.getTags());
        messageEntity.setNextRetryAt(new Date(new Date().getTime() + MqClientConstants.NEXT_RETRY_GAP));
        messageEntity.setPartitionKey(MqUtils.evalPartitionKey(new Date()));
        messageEntity.setMqClientMessageTable(mqClientConfig.getMqClientTableName());
        messageEntity.setMqType(mqClientConfig.getMqType().name());
        messageEntity.setSendOpts(mqSendOption.getSendOpts());
        messageEntity.setMessageHeader(buildMessageHeader(mqSendOption.getDestination()));

        if (mqSendOption.isIdempotentOn()) {
            messageEntity.setMessageKey(Joiner.on(MqClientConstants.MESSAGE_KEY_SPLITTER).join(
                    mqSendOption.getDestination(),
                    StringUtils.isNotBlank(mqSendOption.getTags()) ? mqSendOption.getTags() : "",
                    StringUtils.isNotBlank(mqSendOption.getMessageKey()) ? mqSendOption.getMessageKey() : "")
            );
        }

        MqClientMessageContext mqClientMessageContext = new MqClientMessageContext();
        mqClientMessageContext.setMqClientMessageEntity(messageEntity);
        mqClientMessageContext.setFailoverCtx(mqSendOption.isFailOverContext());
        mqClientMessageContext.setLoadFromDB(mqSendOption.isPersistOn());

        if (mqSendOption.isPersistOn()) {
            mqClientMessageRepository.insertMessage(messageEntity);
            log.info("insert mq client record successfully,id={}", messageEntity.getId());
        }

        final ExecutorService workQueueExecutor = getWorkQueueExecutor();

        AfterCommitTaskRegister.registerTask(
                () -> workQueueExecutor.execute(
                        ConcurrencyWrapper.of(() -> doSend(mqClientMessageContext))
                )
        );
    }

    @PreDestroy
    public void destroy() {
        if (workQueueExecutor != null) {
            List<Runnable> abortedElements = workQueueExecutor.shutdownNow();
            if (abortedElements != null && !abortedElements.isEmpty()) {
                log.error("workQueue aborted elements={}", abortedElements);
            }
        }
    }

    private String buildMessageHeader(String topic) {
        JSONObject mqTrace = new JSONObject();
        try {
            List<String> impactedCGNames = mqClientConfig.getImpactedCGNames().getOrDefault(topic, Lists.newArrayList());
            Long version = mqClientConfig.getImpactedTopicVersions().get(topic);
            for (String cgName : impactedCGNames) {
                mqTrace.put(cgName, "1");
            }
            mqTrace.put("impactedTopicVersion", version);

            String traceId = MetricConverter.processor.getTraceId();
            Long accountId = SecurityContextHolder.getUserDetail() != null ?
                    SecurityContextHolder.getUserDetail().getUid() : null;

            mqTrace.put("accountId", accountId != null ? String.valueOf(accountId) : null);
            mqTrace.put("traceId", traceId);
            UserAgentContext ctx= UserAgentContextHolder.getUserAgentContext();
            if(ctx!= null){
                mqTrace.put("platform", ctx.getPlatform());
                mqTrace.put("requestVersion", ctx.getVersion());
            }

            if (StringUtils.isNotBlank(appVer)) {
                mqTrace.put("appVer", appVer);
            }

            log.info("in sending, MqTrace={}", mqTrace);
        } catch (Exception e) {
            log.warn("Fail to build message headers", e);
        }
        return JSONObject.toJSONString(mqTrace);
    }
}
