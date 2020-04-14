package com.nadia.mqhub.config;

import com.google.common.collect.Maps;
import com.nadia.mqhub.common.domain.MqType;
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

	private MqType mqType;
}
