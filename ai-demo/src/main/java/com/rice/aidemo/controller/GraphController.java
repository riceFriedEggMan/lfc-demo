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

    public GraphController(@Qualifier("simpleGraph") CompiledGraph simpleGraph) {
        this.simpleGraph = simpleGraph;
    }

    @GetMapping("/simpleGraph")
    public Map<String, Object> simpleGraph(@RequestParam String params) {
        Optional<OverAllState> state = simpleGraph.call(Map.of("word", params));
        Map<String, Object> res = state.map(OverAllState::data).orElse(Map.of());
        return res;
    }
}
