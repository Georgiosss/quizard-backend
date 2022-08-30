package com.example.demo.model.game;


import lombok.Data;

@Data
public class GameAnswer {
    //0 -> answerDouble, 1 -> answer
    private Integer answerType;
    private Double answerDouble;
    private Integer answer;
}
