package com.example.demo.model.game;


import com.example.demo.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameAnswer {
    //0 -> answer value, 1 -> answer index
    private Integer answerType;
    private Integer answer;

    @JsonIgnore
    private Long timeTaken;

    @JsonIgnore
    private User user;

    @JsonIgnore
    private Integer correctAnswer;

    public GameAnswer(int answer, long timeTaken, int correctAnswer) {
        this.answer = answer;
        this.timeTaken = timeTaken;
        this.correctAnswer = correctAnswer;
        this.answerType = 0;
    }
}
