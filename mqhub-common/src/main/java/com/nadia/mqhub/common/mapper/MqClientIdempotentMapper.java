package com.nadia.mqhub.common.mapper;

import com.nadia.mqhub.common.entity.MqClientIdempotentEntity;

import java.util.Map;

/**
 * @author xiang.shi
 * @date 2020/4/14 5:16 下午
 */
public interface MqClientIdempotentMapper {

    MqClientIdempotentEntity findByMessageKey(Map params);

    Long insert(MqClientIdempotentEntity mqClientIdempotentEntity);

}
