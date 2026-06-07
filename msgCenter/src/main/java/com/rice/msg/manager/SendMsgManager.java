package com.rice.msg.manager;

import com.rice.msg.model.dto.SendMsgReq;

public interface SendMsgManager {
    String SendToTimer(SendMsgReq sendMsgReq);

    String sendToMq(SendMsgReq sendMsgReq);

    String sendToMysql(SendMsgReq sendMsgReq);
}
