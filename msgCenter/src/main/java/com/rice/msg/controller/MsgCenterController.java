package com.rice.msg.controller;

import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.msg.entity.TMsgTemplate;
import com.rice.msg.model.dto.SendMsgReq;
import com.rice.msg.service.SendMsgService;
import com.rice.msg.service.TMsgRecordService;
import com.rice.msg.service.TMsgTemplateService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/msgCenter")
public class MsgCenterController {

    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private TMsgRecordService tMsgRecordService;
    @Autowired
    private TMsgTemplateService tMsgTemplateService;


    @PostMapping("/addTemplate")
    public ResponseEntity addTemplate(@RequestBody TMsgTemplate tMsgTemplate) {
        return tMsgTemplateService.addTemplate(tMsgTemplate);
    }

    @DeleteMapping("/deleteTemplate/{id}")
    public ResponseEntity deleteTemplate(@PathVariable(name = "id") String templateId) {
        return tMsgTemplateService.delete(templateId);
    }

    @PutMapping("/updateTemplate")
    public ResponseEntity updateTemplate(@RequestBody TMsgTemplate tMsgTemplate) {
        return tMsgTemplateService.updateTemplate(tMsgTemplate);
    }

    @GetMapping("/getTemplateById")
    public ResponseEntity getTemplateById(@RequestParam String templateId) {
        return tMsgTemplateService.getTemplateById(templateId);
    }


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
