package com.rice.msg.service.impl;

import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.enums.TemplateStatus;
import com.rice.msg.exception.BusinessException;
import com.rice.msg.exception.ErrorCode;
import com.rice.msg.manager.SendMsgManager;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.SendMsgService;
import com.rice.msg.service.TMsgRecordService;
import com.rice.msg.service.TMsgTemplateService;
import com.rice.msg.tools.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class SendMsgServiceImpl implements SendMsgService {
    @Autowired
    private TMsgTemplateService tMsgTemplateService;
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private SendMsgManager sendMsgManager;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private TMsgRecordService tMsgRecordService;

    @Override
    public String sendMsg(SendMsgReq sendMsgReq) {
        // 1.校验发送参数（略)

        Date date = DateUtils.addSeconds(new Date(), 10);
        long time = date.getTime();
        sendMsgReq.setSendTimestamp(time);
        // 2.查询模板&校验模板状态
        TMsgTemplate tp = tMsgTemplateService.GetTemplateWithCache(sendMsgReq.getTemplateId());
        if (!tp.getStatus().equals(TemplateStatus.TEMPLATE_STATUS_NORMAL.getStatus())){
            throw new BusinessException(ErrorCode.TEMPLATE_STATUS_ERROR, "模板尚未准备好，检查模板状态");
        }

        //判断是否为定时消息
        boolean isTimerMsg = false;
        if (sendMsgReq.getSendTimestamp() != null) {
            isTimerMsg = true;
        }

        boolean allow = rateLimitService.isRequestAllowed(tp.getSourceId(), tp.getChannel(), isTimerMsg);
        if (!allow) {
            log.warn("请求频繁，限流");
            throw new BusinessException(ErrorCode.RateLimit_ERROR, "请求频繁，限流");
        }
        // 3.发送到缓冲区 定时｜Mysql 缓冲｜MQ 缓冲
        if (isTimerMsg){
            return sendMsgManager.SendToTimer(sendMsgReq);
        }

        String msgId = null;
        if (sendMsgConf.isMysqlAsMq()){
            msgId = sendMsgManager.sendToMysql(sendMsgReq);
            tMsgRecordService.createOrUpdateMsgRecord(sendMsgReq.getMsgId(), sendMsgReq, tp, MsgStatus.Processing);
        }else{
            // msgId = sendMsgManager.sendToMq(sendMsgReq);
        }

        if (!StringUtils.isBlank(msgId)){
            tMsgRecordService.createMsgRecord(msgId, sendMsgReq, tp, MsgStatus.Pending);
        }

        return msgId;

    }
}
