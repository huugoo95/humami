package com.hugo.humami.service.impl;

import com.hugo.humami.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final Logger LOGGER = Logger.getLogger(EmbeddingServiceImpl.class.getName());

    private final String baseApiUrl;
    private final WebClient webClient;


    public EmbeddingServiceImpl(@Value("${embedding.api.url}") String apiUrl,
                                @Value("${embedding.api.key}") String apiKey,
                                WebClient.Builder webClientBuilder) {
        // ToDO not working properly
        this.baseApiUrl = apiUrl;
        String fullUrl = apiUrl + "?pipeline_tag=feature-extraction";
        this.webClient = webClientBuilder
                .baseUrl(fullUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public List<Double> getEmbedding(String text) {
        Map<String, Object> requestBody = Map.of("inputs", List.of(text));

        List<List<Double>> embeddings = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<Double>>>() {})
                .onErrorResume(e -> {
                    LOGGER.log(Level.SEVERE, "Error al obtener embedding. Retornando fallback vacío. Error: {0}", e.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .block(Duration.ofSeconds(10));

        if (embeddings != null && !embeddings.isEmpty()) {
            return embeddings.get(0);
        }
        return Collections.emptyList();
    }
}