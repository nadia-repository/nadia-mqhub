package com.nadia.mqhub.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiang.shi
 * @date 2020/4/14 5:34 下午
 */
@Data
@Accessors(chain = true)
public class MqClientMessage {
    private String messageId;
    private String messageKey;
    private String message;
    private String messageType;
    private String destination;
    private String tags;
    private String cgName;

    private String messageHeader;
}
