package com.rice.aidemo.controller;

import com.rice.aidemo.advisor.MemoryAdvisor;
import com.rice.aidemo.advisor.TestAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhipuai")
public class ZhipuChatController {
    private final ChatModel chatModel;
    private final ChatClient chatClient;

    public ZhipuChatController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.chatModel = chatModel;
    }

    @GetMapping("/simple")
    public String simpleChat(@RequestParam(name = "query") String query) {
        return chatClient.prompt()
                .system("你是一个热情可爱的萌妹")
                .user("你喜欢什么类型的男孩子")
                .advisors(new TestAdvisor())
                .call()
                .content();
        //return chatModel.call(query);
    }

    @GetMapping("memoryChat")
    public String memoryChat(@RequestParam(name = "id")  String id, @RequestParam(name = "query") String query) {
        return chatClient.prompt()
                .user(query)
                .advisors(advisorSpec -> advisorSpec.param("id", id))
                .advisors(new MemoryAdvisor())
                .call().content();
    }
}
