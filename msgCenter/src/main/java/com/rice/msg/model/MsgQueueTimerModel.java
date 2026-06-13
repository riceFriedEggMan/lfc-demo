package com.rice.msg.model;

import com.rice.lfcdemo.model.BaseModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class MsgQueueTimerModel extends BaseModel implements Serializable {
    private Long id;

    private String msgId;

    private String req;

    private Long sendTimestamp;

    private int status;

    @Override
    public String toString(){
        return "MsgQueueTimerModel{" +
                "id=" + id +
                ", msgId='" + msgId + '\'' +
                ", req='" + req + '\'' +
                ", sendTimestamp=" + sendTimestamp +
                ", status=" + status +
                '}';
    }
}
