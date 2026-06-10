package com.rice.msg.manager;

import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.ChannelEnum;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.msgpush.ChannelMsgBase;
import com.rice.msg.msgpush.MsgPushService;
import com.rice.msg.msgpush.channel.EmailServiceImpl;
import com.rice.msg.service.TMsgRecordService;
import com.rice.msg.service.TMsgTemplateService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DealMsgManagerImpl implements DealMsgManager {
    public static Map<Integer, MsgPushService> channelStrategyMap = new HashMap<>();

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @PostConstruct
    public void initChannelStrategyMap() {
        channelStrategyMap.put(ChannelEnum.Channel_EMAIL.getChannel(), emailServiceImpl);
    }

    @Autowired
    private TMsgTemplateService tMsgTemplateService;
    @Autowired
    private TMsgRecordService tMsgRecordService;

    @Override
    public void dealOneMsg(SendMsgReq sendMsgReq) {
        TMsgTemplate tp = tMsgTemplateService.GetTemplateWithCache(sendMsgReq.getTemplateId());
        String msgContent = replaceStr(sendMsgReq.getTemplateData(), tp.getContent());
        ChannelMsgBase channelMsgBase = new ChannelMsgBase();
        channelMsgBase.setTo(sendMsgReq.getTo());
        channelMsgBase.setSubject(sendMsgReq.getSubject());
        channelMsgBase.setContent(msgContent);
        channelMsgBase.setPriority(sendMsgReq.getPriority());
        channelMsgBase.setTemplateId(sendMsgReq.getTemplateId());
        MsgPushService msgPushService = channelStrategyMap.get(tp.getChannel());
        msgPushService.pushMsg(channelMsgBase);

        try{
            tMsgRecordService.createMsgRecord(sendMsgReq.getMsgId(), sendMsgReq, tp, MsgStatus.Success);
        }catch (Exception e){
            log.error("存储消息发送记录失败， msgId", sendMsgReq.getMsgId());
        }

    }

    private String replaceStr(Map<String, String> templateData, String content) {
        String res = content;
        for (Map.Entry<String, String> entry : templateData.entrySet()) {
            res = StringUtils.replace(res, "${" + entry.getKey() + "}", entry.getValue());
        }
        return res;
    }
}
