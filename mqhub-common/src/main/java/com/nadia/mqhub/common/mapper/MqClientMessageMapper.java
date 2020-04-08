package com.nadia.mqhub.common.mapper;

import com.nadia.mqhub.common.entity.MqClientMessageEntity;

import java.util.Map;

/**
 * @author xiang.shi
 * @date 2020/4/7 1:44 下午
 */

public interface MqClientMessageMapper {

    int insertMessage(MqClientMessageEntity mqClientMessageEntity);

    int updateMessageStatus(Map<String, Object> paramer);
}
