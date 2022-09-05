package com.example.demo.model.game;


<<<<<<< HEAD
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
=======
import lombok.Data;

@Data
public class GameAnswer {
    //0 -> answerDouble, 1 -> answer
    private Integer answerType;
    private Double answerDouble;
    private Integer answer;
>>>>>>> 214cb7997eda7dd93efa0e98205bdc2b279a5a0f
}
