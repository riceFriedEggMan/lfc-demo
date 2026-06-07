package com.rice.msg.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaMsgConsumer {

    @KafkaListener(topics = "low-topic", groupId = "TEST_GROUP", concurrency = "1", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeLow(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    @KafkaListener(topics = "middle-topic", groupId = "TEST_GROUP", concurrency = "3", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeMiddle(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    @KafkaListener(topics = "high-topic", groupId = "TEST_GROUP", concurrency = "6", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeHigh(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    @KafkaListener(topics = "retry-topic", groupId = "TEST_GROUP", concurrency = "1", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeRetry(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    private void handleMQMsg(ConsumerRecord<?, ?> record, Acknowledgment ack, String topic) {

    }
}
