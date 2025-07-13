package com.hugo.humami.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DifficultyEnumDTO {
    EASY("Fácil"),
    INTERMEDIATE("Intermedio"),
    HARD("Difícil");
    private final String label;
}