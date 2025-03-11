package com.hugo.humami.service.impl;

import com.hugo.humami.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final WebClient webClient;

    public EmbeddingServiceImpl(@Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/embeddings")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }


    public List<Double> generateEmbedding(String text) {
        Map<String, Object> response = webClient.post()
                .bodyValue(Map.of("input", text, "model", "text-embedding-ada-002"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (List<Double>) ((List<Map<String, Object>>) response.get("data")).get(0).get("embedding");
    }
}