package com.rice.aidemo.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.rice.aidemo.nodes.*;
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

    @Bean("jokeGraph")
    public CompiledGraph jokeGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("topic", new ReplaceStrategy());
        StateGraph jokeGraph = new StateGraph("jokeGraph", keyStrategyFactory);

        jokeGraph.addNode("生成笑话", AsyncNodeAction.node_async(new GenerateJokeNode(chatClientBuilder)));
        jokeGraph.addNode("评估笑话", AsyncNodeAction.node_async(new EvaluateJokesNode(chatClientBuilder)));
        jokeGraph.addNode("优化笑话", AsyncNodeAction.node_async(new EnhancejokeQualityNode(chatClientBuilder)));

        jokeGraph.addEdge(StateGraph.START, "生成笑话");
        jokeGraph.addEdge("生成笑话", "评估笑话");
        jokeGraph.addConditionalEdges("评估笑话", AsyncEdgeAction.edge_async(state -> state.value("result", "优秀")),
                Map.of("优秀", StateGraph.END,
                        "不够优秀", "优化笑话"));

        jokeGraph.addEdge("优化笑话", StateGraph.END);

        return jokeGraph.compile();

    }

    @Bean("loopGraph")
    public CompiledGraph loopGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("topic", new ReplaceStrategy());
        StateGraph loopGraph = new StateGraph("loopGraph", keyStrategyFactory);

        loopGraph.addNode("生成笑话", AsyncNodeAction.node_async(new GenerateJokeNode(chatClientBuilder)));
        loopGraph.addNode("评估笑话", AsyncNodeAction.node_async(new LoopEvaluateJokesNode(chatClientBuilder, 6, 10)));

        loopGraph.addEdge(StateGraph.START, "生成笑话");
        loopGraph.addEdge("生成笑话", "评估笑话");
        loopGraph.addConditionalEdges("评估笑话", AsyncEdgeAction.edge_async(state -> state.value("result", "loop")),
                Map.of("loop", "生成笑话",
                        "break", StateGraph.END));

        return loopGraph.compile();

    }

}
