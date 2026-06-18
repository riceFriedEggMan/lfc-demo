package com.rice.aidemo.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j
public class TestAdvisor implements CallAdvisor {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("这是个调用前测试advisor");
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        log.info("这是个调用后测试advisor");
        return chatClientResponse;
    }
}
