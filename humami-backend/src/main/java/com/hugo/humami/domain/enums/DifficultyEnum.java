package com.hugo.humami.domain.enums;

import lombok.*;

@AllArgsConstructor
public enum DifficultyEnum {
    EASY("Fácil"),
    INTERMEDIATE("Intermedio"),
    HARD("Difícil");
    private final String label;
}