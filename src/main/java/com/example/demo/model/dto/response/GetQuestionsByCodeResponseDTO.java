package com.example.demo.model.dto.response;

import com.example.demo.model.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetQuestionsByCodeResponseDTO {
    private List<Question> singleChoiceQuestions;
    private List<Question> multipleChoiceQuestions;
}
