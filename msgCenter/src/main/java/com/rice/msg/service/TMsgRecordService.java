package com.rice.msg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.msg.entity.TMsgRecord;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.enums.MsgStatus;
import com.rice.msg.model.dto.SendMsgReq;


/**
 * 消息记录表(TMsgRecord)表服务接口
 *
 * @author makejava
 * @since 2026-06-07 12:24:42
 */
public interface TMsgRecordService extends IService<TMsgRecord> {

    void createMsgRecord(String msgId, SendMsgReq sendMsgReq, TMsgTemplate tp, MsgStatus msgStatus);

    ResponseEntity getMessageById(String msgId);
}
