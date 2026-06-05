package com.rice.lfcdemo.common.config;

import com.rice.lfcdemo.common.conf.RabbitMQConf;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Autowired
    private RabbitMQConf rabbitMQConf;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(rabbitMQConf.getExchangeKey(), true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue(rabbitMQConf.getQueueName(), true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(directExchange())
                .with(rabbitMQConf.getRoutingKey());
    }
}
