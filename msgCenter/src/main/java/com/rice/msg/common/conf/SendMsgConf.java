package com.rice.msg.common.conf;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SendMsgConf {

    @Value("${send-msg-conf.mysql-as-mq}")
    private boolean mysqlAsMq;

    @Value("${send-msg-conf.open-cache}")
    private boolean openCache;

    @Value("${send-msg-conf.max-retry-count}")
    private int maxRetryCount;

    @Value("${send-msg-conf.email-account}")
    private String emailAccount;

    @Value("${send-msg-conf.email-auth-code}")
    private String emailAuthCode;

    @Value("${send-msg-conf.email-host}")
    private String emailHost;

    @Value("${send-msg-conf.email-port}")
    private String emailPort;
}
