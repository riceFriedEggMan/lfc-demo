package com.rice.aidemo.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class TranslationNode implements NodeAction {
    private final ChatClient chatClient;

    public TranslationNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String sentence = state.value("sentence", "");

        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语翻译专家，能够对句子进行翻译。要求只返回翻译的结果不要返回其他信息。要翻译的句子:{sentence}");
        promptTemplate.add("sentence", sentence);
        String prompt = promptTemplate.render();

        String content = chatClient.prompt().user(prompt).call().content();

        return Map.of("translation", content);
    }
}
