package com.rice.msg.constant;

public class Constants {
    public static final String TableNamePre_MsgQueue= "t_msg_queue_";

    public static final String Topic_Tail_MsgQueue = "-topic";

    public static final String  REDIS_KEY_SOURCE_QUOTA= "XMSG_source_quota_";
    public static final String  REDIS_KEY_CHANNEL_QUOTA= "XMSG_CHANNEL_quota_";
    public static final String  REDIS_KEY_RATE_LIMIT_COUNT= "XMSG_rate_limit_count";
    public static final String  REDIS_KEY_TEMPLATE= "XMSG_template_";
    public static final String  REDIS_KEY_MES_RECORD= "XMSG_msgrecord_";
}
