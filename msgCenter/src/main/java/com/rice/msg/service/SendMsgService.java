package com.rice.msg.service;

import com.rice.msg.model.dto.SendMsgReq;

public interface SendMsgService {
    String sendMsg(SendMsgReq sendMsgReq);
}
