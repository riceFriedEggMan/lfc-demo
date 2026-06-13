package com.rice.msg.consumer.poll;

import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.enums.TemplateStatus;
import com.rice.msg.exception.BusinessException;
import com.rice.msg.exception.ErrorCode;
import com.rice.msg.manager.SendMsgManager;
import com.rice.msg.mapper.MsgQueueTimerMapper;
import com.rice.msg.mapper.TMsgQueueMapper;
import com.rice.msg.mapper.TMsgTemplateMapper;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.TMsgRecordService;
import com.rice.msg.service.TMsgTemplateService;
import com.rice.msg.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class TimerMsgResendPollTask {
    @Autowired
    private TMsgTemplateMapper tMsgTemplateMapper;
    @Autowired
    private TMsgRecordService tMsgRecordService;
    @Autowired
    private MsgQueueTimerMapper msgQueueTimerMapper;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private SendMsgManager sendMsgManager;


    @Async("timerMsgPoll")
    public void asyncHandleMsg(String msgStr){
        SendMsgReq sendMsgReq = JSONUtil.parseObject(msgStr, SendMsgReq.class);
        if (sendMsgReq == null){
            return;
        }
        TMsgTemplate tp = tMsgTemplateMapper.getTemplateById(sendMsgReq.getTemplateId());
        if (tp.getStatus() != TemplateStatus.TEMPLATE_STATUS_NORMAL.getStatus()){
            throw new BusinessException(ErrorCode.TEMPLATE_STATUS_ERROR, "模板尚未准备好，检查模板状态");
        }
        boolean success = false;

        try {
            success = sendWithRetry(sendMsgReq);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }

        if (success){
            tMsgRecordService.createOrUpdateMsgRecord(sendMsgReq.getMsgId(), sendMsgReq, tp, MsgStatus.Processing);
        }else{
            tMsgRecordService.createOrUpdateMsgRecord(sendMsgReq.getMsgId(), sendMsgReq, tp, MsgStatus.Failed);
        }
        msgQueueTimerMapper.setStatus(sendMsgReq.getMsgId(), MsgStatus.Success.getStatus());

    }

    private boolean sendWithRetry(SendMsgReq sendMsgReq) {
        for (int retryCount = 0; retryCount <= sendMsgConf.getMaxRetryCount(); retryCount++) {
            try {
                if (retryCount > 0){
                    log.info("第{}次重试发送消息至中转站", retryCount);
                    Thread.sleep(1000 * (1 << (retryCount - 1)));
                }
                if (sendMsgConf.isMysqlAsMq()){
                    sendMsgManager.sendToMysql(sendMsgReq);
                }else{
                    sendMsgManager.sendToMq(sendMsgReq);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("发送被中断", e);
            } catch (Exception e) {
                log.error("发送失败，重试次数： {}/{}",retryCount, sendMsgConf.getMaxRetryCount(), e);

                if (retryCount == sendMsgConf.getMaxRetryCount()){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送失败，已超过最大重试次数");
                }
            }
        }
        return false;
    }

}
