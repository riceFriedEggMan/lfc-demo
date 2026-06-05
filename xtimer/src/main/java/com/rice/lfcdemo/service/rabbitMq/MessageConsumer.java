package com.rice.lfcdemo.service.rabbitMq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rice.lfcdemo.common.conf.RabbitMQConf;
import com.rice.lfcdemo.service.executor.ExecutorWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MessageConsumer {

    @Autowired
    private ExecutorWorker executorWorker;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "direct.queue")
    public void consume(Message message, Channel channel) throws IOException {
        try {
            String msg = objectMapper.readValue(message.getBody(), String.class);
            log.info("收到消息" + msg);
            // 业务逻辑
            executorWorker.work(msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
