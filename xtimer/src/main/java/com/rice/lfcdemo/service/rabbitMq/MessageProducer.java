package com.rice.lfcdemo.service.rabbitMq;

import com.rice.lfcdemo.common.conf.RabbitMQConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MessageProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConf rabbitMQConf;

    public void sendMessage(String message) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                rabbitMQConf.getExchangeKey(),
                rabbitMQConf.getRoutingKey(),
                message,
                correlationData
        );
        log.info("消息发送成功" + message);
    }
}
