package com.nadia.mqhub.common.domain;

import com.nadia.mqhub.common.entity.MqClientMessageEntity;
import lombok.Data;

@Data
public class MqClientMessageContext {
    private MqClientMessageEntity mqClientMessageEntity;
    private boolean loadFromDB;
    private boolean failoverCtx = false;
}
