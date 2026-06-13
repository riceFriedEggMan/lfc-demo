package com.rice.msg.consumer.poll;

import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.constant.Constants;
import com.rice.msg.entity.TMsgQueue;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.enums.PriorityEnum;
import com.rice.msg.manager.DealMsgManager;
import com.rice.msg.manager.SendMsgManager;
import com.rice.msg.mapper.TMsgQueueMapper;
import com.rice.msg.mapper.TMsgRecordMapper;
import com.rice.msg.model.MsgRecordModel;
import com.rice.msg.model.dto.SendMsgReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MysqlMsgPollTask {

    @Autowired
    private DealMsgManager dealMsgManager;
    @Autowired
    private TMsgQueueMapper tMsgQueueMapper;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private TMsgRecordMapper tMsgRecordMapper;
    @Autowired
    private SendMsgManager sendMsgManager;

    @Async("mysqlMsgDealPoll")
    public void asyncHandleMsg(SendMsgReq sendMsgReq) {
        String tableName = Constants.TableNamePre_MsgQueue + PriorityEnum.GetPriorityStr(sendMsgReq.getPriority());

        try {
            dealMsgManager.dealOneMsg(sendMsgReq);
            tMsgQueueMapper.setStatus(tableName, sendMsgReq.getMsgId(), MsgStatus.Success.getStatus());
        } catch (Exception e) {
            if (sendMsgReq.getPriority() != PriorityEnum.PRIORITY_RETRY.getPriority()){
                tMsgQueueMapper.setStatus(tableName, sendMsgReq.getMsgId(), MsgStatus.Failed.getStatus());
            }
            dealRetryMysqlQueue(sendMsgReq);
        }

    }

    private void dealRetryMysqlQueue(SendMsgReq sendMsgReq) {
        MsgRecordModel msg = tMsgRecordMapper.getMsgById(sendMsgReq.getMsgId());
        String retryTableName = Constants.TableNamePre_MsgQueue + PriorityEnum.GetPriorityStr(PriorityEnum.PRIORITY_RETRY.getPriority());

        if (msg.getRetryCount() > 0 && msg.getRetryCount() >= sendMsgConf.getMaxRetryCount()){
            log.info("消息" + sendMsgReq.getMsgId() + "已达到最大重试次数，不再重试" + sendMsgConf.getMaxRetryCount());
            tMsgRecordMapper.setStatus(sendMsgReq.getMsgId(), MsgStatus.Failed.getStatus());
            tMsgQueueMapper.setStatus(retryTableName, sendMsgReq.getMsgId(), MsgStatus.Failed.getStatus());
            return;
        }

        int retryCount = msg.getRetryCount() + 1;
        tMsgRecordMapper.incrementRetryCount(sendMsgReq.getMsgId(), retryCount);

        TMsgQueue tMsgQueue = tMsgQueueMapper.getMsgById(retryTableName, sendMsgReq.getMsgId());
        if (tMsgQueue == null){
            sendMsgReq.setPriority(PriorityEnum.PRIORITY_RETRY.getPriority());
            sendMsgManager.sendToMq(sendMsgReq);
        }else{
            tMsgQueueMapper.setStatus(retryTableName, sendMsgReq.getMsgId(), MsgStatus.Pending.getStatus());
        }

        log.info("消息" + sendMsgReq.getMsgId() + "已加入MySQL重试队列，当前重试次数:", retryCount);
    }
}

