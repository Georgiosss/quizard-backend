package com.example.demo.model.game;

import lombok.Data;

@Data
public class PlayerAnswer {
    private GameAnswer gameAnswer;
    private Double seconds;
    private Integer score;
}
