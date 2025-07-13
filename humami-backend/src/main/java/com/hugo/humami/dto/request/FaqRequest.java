package com.hugo.humami.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Data
public class FaqRequest {
    private String question;
    private String answer;
}
