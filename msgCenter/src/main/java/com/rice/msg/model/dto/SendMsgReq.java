package com.rice.msg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMsgReq {
    private String to;
    private String subject;
    private int priority;
    private String templateId;
    private Map<String,String> templateData;
    private Long sendTimestamp;
    private String msgId;
}
