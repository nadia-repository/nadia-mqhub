package com.nadia.mqhub.producer.mq;

import com.nadia.mqhub.common.domain.MqResult;
import com.nadia.mqhub.common.entity.MqClientMessageEntity;

/**
 * @author xiang.shi
 * @date 2020/4/7 11:00 上午
 */
public interface MqClient {

    /**
     * 异步发送消息
     * @param mqClientMessageEntity
     * @param callback 异步回调
     */
    void send(MqClientMessageEntity mqClientMessageEntity, Callback callback);

    /**
     * 异步发送消息
     * @param mqClientMessageEntity
     * @param callback 异步回调
     * @param persistOn 是否需要数据库持久化
     */
    void send(MqClientMessageEntity mqClientMessageEntity, Callback callback, boolean persistOn);

    /**
     * 异步发送消息
     * @param mqClientMessageEntity
     * @param callback 异步回调
     * @param persistOn 是否需要数据库持久化
     * @param local 是否需要本地异常处理
     */
    void send(MqClientMessageEntity mqClientMessageEntity, Callback callback, boolean persistOn, boolean local);

    /**
     * 同步发送消息
     * @param mqClientMessageEntity
     * @return
     */
    MqResult send(MqClientMessageEntity mqClientMessageEntity);

    /**
     * 同步发送消息
     * @param mqClientMessageEntity
     * @param persistOn 是否需要数据库持久化
     * @return
     */
    MqResult send(MqClientMessageEntity mqClientMessageEntity, boolean persistOn);

    /**
     * 同步发送消息
     * @param mqClientMessageEntity
     * @param persistOn 是否需要数据库持久化
     * @param local 是否需要本地异常处理
     * @return
     */
    MqResult send(MqClientMessageEntity mqClientMessageEntity, boolean persistOn, boolean local);

}
