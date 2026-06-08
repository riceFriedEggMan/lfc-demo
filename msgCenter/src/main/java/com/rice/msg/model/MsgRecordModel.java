package com.rice.msg.model;

import com.rice.lfcdemo.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgRecordModel extends BaseModel implements Serializable {
    private Long id;

    private String msgId;

    private String sourceId;

    private int channel;

    private String subject;

    private String to;

    private String templateId;

    private String templateData;

    private int status;

    private int retryCount; //重试次数，默认为 0
}
