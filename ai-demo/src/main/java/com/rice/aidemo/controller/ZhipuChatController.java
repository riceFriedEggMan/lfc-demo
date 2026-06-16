package com.rice.aidemo.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhipuai")
public class ZhipuChatController {
    private final ChatModel chatModel;

    public ZhipuChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/simple")
    public String simpleChat(@RequestParam(name = "query") String query) {
        return chatModel.call(query);
    }
}
