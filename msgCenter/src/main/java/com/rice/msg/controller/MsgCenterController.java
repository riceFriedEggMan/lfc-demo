package com.rice.msg.controller;

import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.SendMsgService;
import com.rice.msg.service.TMsgRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/msgCenter")
public class MsgCenterController {

    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private TMsgRecordService tMsgRecordService;

    @PostMapping("/sendMsg")
    public ResponseEntity sendMsg(@RequestBody SendMsgReq sendMsgReq){
        String msgId = sendMsgService.sendMsg(sendMsgReq);
        return ResponseEntity.ok(msgId);
    }

    @GetMapping("/getMsg")
    public ResponseEntity getMsg(@RequestParam String msgId){
        return tMsgRecordService.getMessageById(msgId);
    }
}
