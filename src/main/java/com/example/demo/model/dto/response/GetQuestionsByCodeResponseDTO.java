package com.example.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetQuestionsByCodeResponseDTO {
    private Long questionNumber;
    private String question;
}
