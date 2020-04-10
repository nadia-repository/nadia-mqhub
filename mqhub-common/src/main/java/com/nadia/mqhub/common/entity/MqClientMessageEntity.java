package com.nadia.mqhub.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MqClientMessageEntity {
    private String mqClientMessageTable;
    private Long id;
    private String bizId;
    private String bizType;
    private String message;
    private String messageType;
    private String topic;
    private String tags;
    private String messageKey;
    private String status;
    private Date nextRetryAt;
    private Integer retryCount;
    private String failureReason;
    private String messageId;
    private String sendOpts;
    private Date createdAt;
    private Date updatedAt;
    private Long partitionKey;
    private String mqType;
    private String messageHeader;
}
