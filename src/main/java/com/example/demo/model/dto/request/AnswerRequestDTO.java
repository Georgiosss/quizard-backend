package com.example.demo.model.dto.request;


import com.example.demo.model.game.GameAnswer;
import lombok.Data;

@Data
public class AnswerRequestDTO {
    private String gameId;
    //0 -> answerDouble, 1 -> answer
    private GameAnswer gameAnswer;
    private Long startTime;
}
