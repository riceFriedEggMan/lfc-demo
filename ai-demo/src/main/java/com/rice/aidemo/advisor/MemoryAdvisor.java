package com.rice.aidemo.advisor;


import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryAdvisor implements BaseAdvisor {
    private static Map<String, List<Message>> charMemory = new HashMap<>();

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String id = chatClientRequest.context().get("id").toString();

        List<Message> messages = charMemory.get(id);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.addAll(chatClientRequest.prompt().getInstructions());

        ChatClientRequest clientRequest = chatClientRequest.mutate().prompt(
                (chatClientRequest.prompt().mutate().messages(messages).build())
        ).build();

        charMemory.put(id, messages);

        return clientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        String id = chatClientResponse.context().get("id").toString();
        List<Message> messages = new ArrayList<>();
        if (chatClientResponse.chatResponse() != null) {
            messages = chatClientResponse
                    .chatResponse()
                    .getResults()
                    .stream()
                    .map(g -> (Message) g.getOutput())
                    .toList();
        }
        charMemory.get(id).addAll(messages);
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
