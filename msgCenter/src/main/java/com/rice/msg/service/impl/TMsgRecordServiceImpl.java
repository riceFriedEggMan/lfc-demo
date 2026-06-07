package com.rice.msg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.msg.entity.TMsgRecord;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.mapper.TMsgRecordMapper;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.TMsgRecordService;
import com.rice.msg.utils.JSONUtil;
import org.springframework.stereotype.Service;

/**
 * 消息记录表(TMsgRecord)表服务实现类
 *
 * @author makejava
 * @since 2026-06-07 12:24:42
 */
@Service
public class TMsgRecordServiceImpl extends ServiceImpl<TMsgRecordMapper, TMsgRecord> implements TMsgRecordService {

    @Override
    public void createMsgRecord(String msgId, SendMsgReq sendMsgReq, TMsgTemplate tp, MsgStatus msgStatus) {
        TMsgRecord tMsgRecord = new TMsgRecord();
        tMsgRecord.setMsgId(msgId);
        tMsgRecord.setTo(sendMsgReq.getTo());
        tMsgRecord.setSubject(sendMsgReq.getSubject());
        tMsgRecord.setTemplateId(sendMsgReq.getTemplateId());
        tMsgRecord.setTemplateData(JSONUtil.toJsonString(sendMsgReq.getTemplateData()));
        tMsgRecord.setSourceId(tp.getSourceId());
        tMsgRecord.setChannel(tp.getChannel());
        tMsgRecord.setStatus(msgStatus.getStatus());
        try {
            getBaseMapper().save(tMsgRecord);
        } catch (Exception e) {
            log.error("存储消息发送记录失败， msgId:"+tMsgRecord.getMsgId());
        }

    }
}
