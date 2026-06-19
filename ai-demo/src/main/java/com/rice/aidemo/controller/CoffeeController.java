package com.rice.aidemo.controller;

import com.rice.aidemo.advisor.MemoryAdvisor;
import com.rice.aidemo.tools.TimeTools;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coffee")
public class CoffeeController {
    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    public CoffeeController(VectorStore vectorStore, ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        this.vectorStore = vectorStore;
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.6)
                .build();
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                .build();
        this.chatClient  = chatClientBuilder
                .defaultAdvisors(retrievalAugmentationAdvisor, new MemoryAdvisor())
//                .defaultTools(toolCallbackProvider.getToolCallbacks())
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
        System.out.println(1);
    }

    @GetMapping("/import")
    public String importData(){

        try {
            ClassPathResource resource = new ClassPathResource("QA.csv");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);

            List<Document> documents = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {
                String question = csvRecord.get("问题");
                String answer = csvRecord.get("回答");
                String content = "问题: " + question + "回答： " + answer;

                Document document = new Document(content);
                documents.add(document);
            }

            csvParser.close();
            vectorStore.add(documents);
            return "成功导入 " + documents.size() + " 条记录到向量数据库";
        } catch (IOException e) {
            e.printStackTrace();
            return "导入失败: " + e.getMessage();
        }
    }

    @GetMapping("/ragAsk")
    public String ragAskQuestion(@RequestParam("question") String question, @RequestParam("id") String id) {
        return chatClient.prompt()
                .system("你是咖啡店服务员，需要回答用户问题")
                .user(question)
                .advisors(advisorSpec -> advisorSpec.param("id", id))
                .call()
                .content();
    }

}
