package com.rice.aidemo.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class GenerateJokeNode implements NodeAction {
    private final ChatClient chatClient;

    public GenerateJokeNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String topic = state.value("topic", "");

        PromptTemplate promptTemplate = new PromptTemplate("你需要写一个关于指定主题的短笑话。要求返回的结果中只能包含笑话的内容" +
                "主题:{topic}");

        promptTemplate.add("topic", topic);

        String prompt = promptTemplate.render();

        String content = chatClient.prompt().user(prompt).call().content();

        return Map.of("joke", content);
    }
}
