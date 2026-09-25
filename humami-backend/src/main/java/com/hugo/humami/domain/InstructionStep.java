package com.hugo.humami.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstructionStep {
    private Integer order;
    private String text;
}
