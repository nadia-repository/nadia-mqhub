package com.nadia.mqhub.mq.rmq;

import lombok.Setter;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @author xiang.shi
 * @date 2020/4/7 6:58 下午
 */
public abstract class RMQSendCallback implements SendCallback {
    @Setter
    private boolean local = false;

    @Override
    public void onSuccess(SendResult sendResult) {
        success(sendResult);
    }

    @Override
    public void onException(Throwable e) {
        exception(e);
        if(local){

        }
    }

    abstract void success(SendResult sendResult);

    abstract void exception(Throwable e);
}
