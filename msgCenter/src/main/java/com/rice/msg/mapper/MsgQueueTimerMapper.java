package com.rice.msg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.msg.model.MsgQueueTimerModel;

import java.util.List;

public interface MsgQueueTimerMapper extends BaseMapper<MsgQueueTimerModel> {
    List<MsgQueueTimerModel> getOnTimeMsgsList(int status, long time);

    void batchSetStatus(List<String> msgIds, int status);

    void setStatus(String msgId, int status);

    void save(MsgQueueTimerModel msgQueueTimerModel);

}
