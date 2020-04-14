package com.nadia.mqhub.consumer.mq.config;

import com.google.common.collect.Maps;
import com.nadia.mqhub.consumer.mq.MqMessageListener;
import lombok.Data;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
public class ConsumerConfig {
	@Value("${mqhub.rmq.nameSrv:}")
	private String nameSrv;

	private Map<String, String> consumerGroupTopicConf = Maps.newConcurrentMap();
	private Map<String, List<String>> tagsWhitelistConf = Maps.newConcurrentMap();
	private Map<String, List<String>> listenedTagsConf = Maps.newConcurrentMap();
	private Map<String, List<String>> impactedCGNames = Maps.newConcurrentMap();
	private Map<String, Long> impactedTopicVersions = Maps.newConcurrentMap();
	private Map<String, List<Long>> supportedTopicVersion = Maps.newConcurrentMap();

	private MessageQueueSelector customizedMQSelector;
	private boolean msgListeningOrderly = false;

	private MqMessageListener mqMessageListener;
}
