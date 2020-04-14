package com.nadia.mqhub.consumer.mq;

import com.nadia.mqhub.common.domain.MqClientMessage;

/**
 * @author xiang.shi
 * @date 2020/4/14 5:33 下午
 */
public interface MqMessageListener {
    void onMessage(MqClientMessage mqClientMessage);

}
