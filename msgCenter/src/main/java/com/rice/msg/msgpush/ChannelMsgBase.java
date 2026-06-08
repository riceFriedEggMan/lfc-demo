package com.rice.msg.msgpush;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMsgBase {
    private String to;

    private String subject;

    private String content;

    private int priority;

    private String templateId;

    private Map<String,String> templateData;

    private String notifyURL;
}
