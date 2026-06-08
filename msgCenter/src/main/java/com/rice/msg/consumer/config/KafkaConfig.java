package com.rice.msg.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    @Primary
    public ConsumerFactory<Object, Object> consumerFactory(@Autowired(required = false) SslBundles sslBundles) {
        Map<String, Object> map = kafkaProperties.getConsumer().buildProperties(sslBundles);
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaProperties.getBootstrapServers()));
        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(map);

        return factory;
    }

    @Bean
    public ConsumerFactory<Object, Object> kafkaManualConsumerFactory(@Autowired(required = false) SslBundles sslBundles) {
        Map<String, Object> map = kafkaProperties.getConsumer().buildProperties(sslBundles);
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaProperties.getBootstrapServers()));
        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(map);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, Object>> kafkaManualAckListenerContainerFactory(@Autowired(required = false) SslBundles sslBundles) {
        ConcurrentKafkaListenerContainerFactory<Integer, Object> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConsumerFactory(kafkaManualConsumerFactory(sslBundles));
        listenerContainerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return listenerContainerFactory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, Object>> batchFactory(@Autowired(required = false) SslBundles sslBundles) {
        ConcurrentKafkaListenerContainerFactory<Integer, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(sslBundles));
        factory.setBatchListener(true);
        return factory;
    }


}
