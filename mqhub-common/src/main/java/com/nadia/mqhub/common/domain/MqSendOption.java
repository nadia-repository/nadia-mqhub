package com.nadia.mqhub.common.domain;

import lombok.Data;
import org.apache.rocketmq.common.message.MessageType;


@Data
public class MqSendOption {
    private String bizId;

    private String bizType;

    private String referenceId;

    private MessageType messageType;

    private String message;

    private String destination;

    private String messageKey;

    private String tags;

    private String sendOpts;

    private MqType mqType;

    // Option to persist the message in local client db
    private boolean persistOn = true;

    // Option to have the feature have idempotent control, if will it is true, message should be provided
    private boolean idempotentOn = false;

    // To indicate whether the sending is in failover mode
    private boolean failOverContext = false;
}
