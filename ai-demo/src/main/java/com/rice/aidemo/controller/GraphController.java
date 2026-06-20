package com.rice.aidemo.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("graph")
public class GraphController {

    private final CompiledGraph simpleGraph;
    private final CompiledGraph jokeGraph;
    private final CompiledGraph loopGraph;


    public GraphController(@Qualifier("simpleGraph") CompiledGraph simpleGraph,
                           @Qualifier("jokeGraph") CompiledGraph jokeGraph,
                           @Qualifier("loopGraph") CompiledGraph loopGraph) {
        this.simpleGraph = simpleGraph;
        this.jokeGraph = jokeGraph;
        this.loopGraph = loopGraph;
    }

    @GetMapping("/simpleGraph")
    public Map<String, Object> simpleGraph(@RequestParam String params) {
        Optional<OverAllState> state = simpleGraph.call(Map.of("word", params));
        Map<String, Object> res = state.map(OverAllState::data).orElse(Map.of());
        return res;
    }

    @GetMapping("/jokeGraph")
    public Map<String, Object> jokeGraph(@RequestParam String params) {
        Optional<OverAllState> state = jokeGraph.call(Map.of("topic", params));
        Map<String, Object> res = state.map(OverAllState::data).orElse(Map.of());
        return res;
    }

    @GetMapping("/loopGraph")
    public Map<String, Object> loopGraph(@RequestParam String params) {
        Optional<OverAllState> state = loopGraph.call(Map.of("topic", params));
        Map<String, Object> res = state.map(OverAllState::data).orElse(Map.of());
        return res;
    }
}
