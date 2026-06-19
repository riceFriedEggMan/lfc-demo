package com.rice.aidemo.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.rice.aidemo.nodes.SentenceConstructionNode;
import com.rice.aidemo.nodes.TranslationNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GraphConfig {

    @Bean("simpleGraph")
    public CompiledGraph simpleGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("word", new ReplaceStrategy());

        StateGraph simpleGraph = new StateGraph("simpleGraph", keyStrategyFactory);

        simpleGraph.addNode("SentenceConstructionNode",
                AsyncNodeAction.node_async(new SentenceConstructionNode(chatClientBuilder)));

        simpleGraph.addNode("TranslationNode",
                AsyncNodeAction.node_async(new TranslationNode(chatClientBuilder)));

        simpleGraph.addEdge(StateGraph.START, "SentenceConstructionNode");
        simpleGraph.addEdge("SentenceConstructionNode", "TranslationNode");
        simpleGraph.addEdge("TranslationNode", StateGraph.END);

        return simpleGraph.compile();

    }
}
