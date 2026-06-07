package com.rice.msg.manager;

import com.rice.msg.constant.Constants;
import com.rice.msg.enums.PriorityEnum;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SendMsgManagerImpl implements SendMsgManager{
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public String SendToTimer(SendMsgReq sendMsgReq) {
        return "";
    }

    @Override
    public String sendToMq(SendMsgReq sendMsgReq) {
        if (StringUtils.isBlank(sendMsgReq.getMsgId())) {
            sendMsgReq.setMsgId(UUID.randomUUID().toString());
        }

        String msg = JSONUtil.toJsonString(sendMsgReq);

        String topic = PriorityEnum.GetPriorityStr(sendMsgReq.getPriority()) + Constants.Topic_Tail_MsgQueue;

        kafkaTemplate.send(topic, msg);

        return sendMsgReq.getMsgId();
    }

    @Override
    public String sendToMysql(SendMsgReq sendMsgReq) {
        return "";
    }
}
