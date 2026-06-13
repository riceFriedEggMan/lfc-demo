package com.rice.msg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.constant.Constants;
import com.rice.msg.entity.TMsgRecord;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.mapper.TMsgRecordMapper;
import com.rice.msg.model.MsgRecordModel;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.TMsgRecordService;
import com.rice.msg.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 消息记录表(TMsgRecord)表服务实现类
 *
 * @author makejava
 * @since 2026-06-07 12:24:42
 */
@Service
public class TMsgRecordServiceImpl extends ServiceImpl<TMsgRecordMapper, TMsgRecord> implements TMsgRecordService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SendMsgConf sendMsgConf;
    @Autowired
    private TMsgRecordMapper tMsgRecordMapper;

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

    @Override
    public ResponseEntity getMessageById(String msgId) {
        String key = Constants.REDIS_KEY_MES_RECORD + msgId;
        String object = redisTemplate.opsForValue().get(key);
        MsgRecordModel msgRecordModel = null;
        if (StringUtils.isNotBlank(object) && sendMsgConf.isOpenCache()) {
            msgRecordModel = JSONUtil.parseObject(object, MsgRecordModel.class);
            if (msgRecordModel != null) {
                return ResponseEntity.ok(msgRecordModel);
            }
        }

        msgRecordModel = getBaseMapper().getMsgById(msgId);

        redisTemplate.opsForValue().set(key, JSONUtil.toJsonString(msgRecordModel), Duration.ofSeconds(30));

        return ResponseEntity.ok(msgRecordModel);
    }

    @Override
    public void createOrUpdateMsgRecord(String msgId, SendMsgReq sendMsgReq, TMsgTemplate tp, MsgStatus msgStatus) {
        MsgRecordModel msg = tMsgRecordMapper.getMsgById(msgId);
        if (msg == null) {
            msg = new MsgRecordModel();
            msg.setMsgId(msgId);
            msg.setTo(sendMsgReq.getTo());
            msg.setSubject(sendMsgReq.getSubject());
            msg.setTemplateId(sendMsgReq.getTemplateId());
            msg.setTemplateData(JSONUtil.toJsonString(sendMsgReq.getTemplateData()));
            msg.setMsgId(sendMsgReq.getMsgId());
            msg.setSourceId(tp.getSourceId());
            msg.setChannel(tp.getChannel());
            msg.setStatus(msgStatus.getStatus());
            TMsgRecord tMsgRecord = new TMsgRecord();
            BeanUtils.copyProperties(msg, tMsgRecord);
            try {
                tMsgRecordMapper.save(tMsgRecord);
            } catch (Exception e) {
                log.error("存储消息失败");
            }
        }else{
            try {
                tMsgRecordMapper.setStatus(msgId, msgStatus.getStatus());
            } catch (Exception e) {
                log.error("更新消息失败");
            }
        }


    }
}
