package com.nadia.mqhub.common.domain;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
public class ProducerConfig {
	private String dataSource;
	private String mqClientTableName;
	private String mqClientIdempotentTableName;
	private int workQueueCorePoolSize = 5;
	private int workQueueMaxPoolSize = 5;
	private int workQueueCapacity = 2000;
	private int workQueueKeepAliveSeconds = 300;
	@Value("${mqhub.rmq.producerGroupName:}")
	private String producerGroupName;
	@Value("${mqhub.rmq.nameSrv:}")
	private String nameSrv;

	private Map<String, String> consumerGroupTopicConf = Maps.newConcurrentMap();
	private Map<String, List<String>> tagsWhitelistConf = Maps.newConcurrentMap();
	private Map<String, List<String>> listenedTagsConf = Maps.newConcurrentMap();
	private Map<String, List<String>> impactedCGNames = Maps.newConcurrentMap();
	private Map<String, Long> impactedTopicVersions = Maps.newConcurrentMap();
	private Map<String, List<Long>> supportedTopicVersion = Maps.newConcurrentMap();


	private MessageQueueSelector customizedMQSelector;

	private MqType mqType;

	private boolean msgListeningOrderly = false;
}
