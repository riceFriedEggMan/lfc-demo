package com.rice.lfcdemo.common.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RabbitMQConf {
    @Value("${rabbit.exchangeKey}")
    private String exchangeKey;

    @Value("${rabbit.routingKey}")
    private String routingKey;

    @Value("${rabbit.queueName}")
    private String queueName;

}
