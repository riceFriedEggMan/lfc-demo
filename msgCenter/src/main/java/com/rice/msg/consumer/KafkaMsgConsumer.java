package com.rice.msg.consumer;

import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.enums.PriorityEnum;
import com.rice.msg.manager.DealMsgManager;
import com.rice.msg.manager.SendMsgManager;
import com.rice.msg.mapper.TMsgRecordMapper;
import com.rice.msg.model.MsgRecordModel;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaMsgConsumer {
    @Autowired
    private TMsgRecordMapper tMsgRecordMapper;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private SendMsgManager sendMsgManager;
    @Autowired
    private DealMsgManager dealMsgManager;

    @KafkaListener(topics = "low-topic", groupId = "TEST_GROUP_low", concurrency = "1", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeLow(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    @KafkaListener(topics = "middle-topic", groupId = "TEST_GROUP_middle", concurrency = "3", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeMiddle(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    @KafkaListener(topics = "high-topic", groupId = "TEST_GROUP_high", concurrency = "6", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeHigh(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    @KafkaListener(topics = "retry-topic", groupId = "TEST_GROUP_retry", concurrency = "1", containerFactory = "kafkaManualAckListenerContainerFactory")
    public void consumeRetry(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        handleMQMsg(record,ack,topic);
    }

    private void handleMQMsg(ConsumerRecord<?, ?> record, Acknowledgment ack, String topic) {
        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            SendMsgReq sendMsgReq = null;
            try {
                sendMsgReq = JSONUtil.parseObject(msg.toString(), SendMsgReq.class);
                dealMsgManager.dealOneMsg(sendMsgReq);
                log.info("Kafka消费成功! Topic:" + topic + ",Message:" + msg);
            } catch (Exception e) {
                if (sendMsgReq != null) {
                    handleMqRetryAfterFailure(sendMsgReq);
                }
                e.printStackTrace();
                log.error("发送消息失败");
            } finally {
                ack.acknowledge();
            }
        }
    }

    private void handleMqRetryAfterFailure(SendMsgReq sendMsgReq) {
        MsgRecordModel msgRecordModel = tMsgRecordMapper.getMsgById(sendMsgReq.getMsgId());
        int retryCount = msgRecordModel.getRetryCount();
        if (retryCount > sendMsgConf.getMaxRetryCount()){
            log.info("消息" + sendMsgReq.getMsgId() + "已达到最大重试次数，不再重试:" + sendMsgConf.getMaxRetryCount());
            tMsgRecordMapper.setStatus(sendMsgReq.getMsgId(), MsgStatus.Failed.getStatus());
            return;
        }
        int num = msgRecordModel.getRetryCount() + 1;
        tMsgRecordMapper.incrementRetryCount(sendMsgReq.getMsgId(), num);
        sendMsgReq.setPriority(PriorityEnum.PRIORITY_RETRY.getPriority());
        sendMsgManager.sendToMq(sendMsgReq);
    }
}
