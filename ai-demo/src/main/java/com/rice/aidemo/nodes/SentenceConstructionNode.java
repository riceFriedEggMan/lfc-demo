package com.rice.aidemo.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class SentenceConstructionNode implements NodeAction {
    private final ChatClient chatClient;

    public SentenceConstructionNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String word = state.value("word", "");

        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语造句专家，能够基于给定的单词进行造句。要求只返回最终造好的句子，不要返回其他信息。 给定的单词:{word}");

        promptTemplate.add("word", word);

        String prompt = promptTemplate.render();

        String content = chatClient.prompt().user(prompt).call().content();

        return Map.of("sentence", content);
    }
}
