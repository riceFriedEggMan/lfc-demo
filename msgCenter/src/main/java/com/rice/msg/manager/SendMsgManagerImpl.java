package com.rice.msg.manager;

import com.rice.msg.constant.Constants;
import com.rice.msg.entity.TMsgQueue;
import com.rice.msg.entity.TMsgRecord;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.enums.PriorityEnum;
import com.rice.msg.mapper.MsgQueueTimerMapper;
import com.rice.msg.mapper.TMsgQueueMapper;
import com.rice.msg.model.MsgQueueTimerModel;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.redis.TimerMsgCache;
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
    @Autowired
    private TMsgQueueMapper tMsgQueueMapper;
    @Autowired
    private MsgQueueTimerMapper msgQueueTimerMapper;
    @Autowired
    private TimerMsgCache timerMsgCache;

    @Override
    public String SendToTimer(SendMsgReq sendMsgReq) {
        String uuid = UUID.randomUUID().toString();
        sendMsgReq.setMsgId(uuid);
        String reqStr = JSONUtil.toJsonString(sendMsgReq);
        MsgQueueTimerModel msgQueueTimerModel = new MsgQueueTimerModel();
        msgQueueTimerModel.setMsgId(uuid);
        msgQueueTimerModel.setReq(reqStr);
        msgQueueTimerModel.setSendTimestamp(sendMsgReq.getSendTimestamp());
        msgQueueTimerModel.setStatus(MsgStatus.Pending.getStatus());

        msgQueueTimerMapper.save(msgQueueTimerModel);

        timerMsgCache.cacheSaveMsgTimePoint(msgQueueTimerModel.getSendTimestamp());

        return uuid;
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
        if (StringUtils.isBlank(sendMsgReq.getMsgId())) {
            sendMsgReq.setMsgId(UUID.randomUUID().toString());
        }
        TMsgQueue tMsgQueue = new TMsgQueue();
        tMsgQueue.setMsgId(sendMsgReq.getMsgId());
        tMsgQueue.setTo(sendMsgReq.getTo());
        tMsgQueue.setSubject(sendMsgReq.getSubject());
        tMsgQueue.setTemplateId(sendMsgReq.getTemplateId());
        tMsgQueue.setTemplateData(JSONUtil.toJsonString(sendMsgReq.getTemplateData()));
        tMsgQueue.setStatus(MsgStatus.Pending.getStatus());

        String tableName = Constants.TableNamePre_MsgQueue + PriorityEnum.GetPriorityStr(sendMsgReq.getPriority());

        tMsgQueueMapper.sava(tableName, tMsgQueue);

        return sendMsgReq.getMsgId();
    }
}
