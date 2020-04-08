package com.nadia.mqhub.common.domain;

public class MqClientConstants {
    public static final Integer RMQ_MAX_RETRY_TIMES = 3;

    public static final Integer PURGE_JOB_TIME_AHEAD = 3;

    public static final Integer MAX_RETRY_TIMES = 100;

    public final static Long NEXT_RETRY_GAP = 60 * 1000L;

    public final static String MESSAGE_KEY_SPLITTER = ":";

    public final static int MESSAGE_MAX_SIZE = 8192;

}
