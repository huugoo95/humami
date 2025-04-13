package com.hugo.humami.service;

import java.util.List;

public interface EmbeddingService {
    List<Double> getEmbedding(String text);
}