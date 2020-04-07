package com.nadia.mqhub.mapper;

import com.nadia.mqhub.entity.MqClientMessageEntity;

import java.util.Map;

/**
 * @author xiang.shi
 * @date 2020/4/7 1:44 下午
 */

public interface MqClientMessageMapper {

    int insertMessage(MqClientMessageEntity mqClientMessageEntity);

    int updateMessageStatus(Map<String,Object> paramer);
}
