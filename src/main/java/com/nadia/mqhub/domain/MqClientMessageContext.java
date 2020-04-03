package com.nadia.mqhub.domain;

import com.nadia.mqhub.entity.MqClientMessageEntity;
import lombok.Data;

@Data
public class MqClientMessageContext {
    private MqClientMessageEntity mqClientMessageEntity;
    private boolean loadFromDB;
    private boolean failoverCtx = false;
}
