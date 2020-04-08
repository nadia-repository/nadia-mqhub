package com.nadia.mqhub.common.utils;

/**
 * @author xiang.shi
 * @date 2020/4/7 10:55 上午
 */
public interface ConcurrencyProcessor<T> {
    T getThresholdValue();

    void setThresholdValue(T arg);

    void clearThresholdValue();
}
