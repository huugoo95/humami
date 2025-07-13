package com.hugo.humami.dto.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DifficultyEnumRequest {
    EASY("Fácil"),
    INTERMEDIATE("Intermedio"),
    HARD("Difícil");
    private final String label;
}