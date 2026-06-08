package com.rice.msg.manager;

import com.rice.msg.model.dto.SendMsgReq;

public interface DealMsgManager {
    void dealOneMsg(SendMsgReq sendMsgReq);
}
