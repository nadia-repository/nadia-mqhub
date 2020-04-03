package com.nadia.mqhub.annotation;

import com.nadia.mqhub.domain.MqType;

import java.lang.annotation.*;

/**
 * @author xiang.shi
 * @date 2020/4/3 2:25 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProducerType {
    MqType value();
}
