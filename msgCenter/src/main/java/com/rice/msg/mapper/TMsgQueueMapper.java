package com.rice.msg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.msg.entity.TMsgQueue;
import com.rice.msg.entity.TMsgRecord;
import com.rice.msg.enums.MsgStatus;

import java.util.List;


/**
 * 消息队列表(TMsgQueueHigh)表数据库访问层
 *
 * @author makejava
 * @since 2026-06-13 09:57:53
 */
public interface TMsgQueueMapper extends BaseMapper<TMsgQueue> {

    List<TMsgQueue> getMsgByStatus(String tableName, int msgStatus, int batch);

    void batchSetStatus(String tableName, List<String> msgIdList, int status);

    void setStatus(String tableName, String msgId, int msgStatus);

    TMsgQueue getMsgById(String retryTableName, String msgId);

    void sava(String tableName, TMsgQueue tMsgQueue);
}
