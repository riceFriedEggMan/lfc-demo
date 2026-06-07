package com.rice.msg.controller;

import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.SendMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msgCenter")
public class MsgCenterController {

    @Autowired
    private SendMsgService sendMsgService;

    @PostMapping("/sendMsg")
    public ResponseEntity sendMsg(@RequestBody SendMsgReq sendMsgReq){
        String msgId = sendMsgService.sendMsg(sendMsgReq);
        return ResponseEntity.ok(msgId);
    }
}
