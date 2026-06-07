package com.rice.lfcdemo.service.rabbitMq;

import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.entity.dto.TimerDTO;
import com.rice.lfcdemo.entity.param.NotifyHTTPParam;
import com.rice.lfcdemo.mapper.XtimerMapper;
import com.rice.lfcdemo.service.CleanCache.CleanCacheJob;
import com.rice.lfcdemo.utils.TimerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageProducerTest {

    @Autowired
    private MessageProducer producer;
    @Autowired
    private XtimerMapper xtimerMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CleanCacheJob cleanCacheJob;

    @Test
    void sendMessage() {
        String msg = TimerUtils.UnionTimerIDUnix(8000, 20260605);
        producer.sendMessage(msg);
    }

    @Test
    void cleanCache() {
        String test = "2026-06-05 22:*";
        cleanCacheJob.cleanCache();
    }
}