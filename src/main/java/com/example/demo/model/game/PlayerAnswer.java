package com.example.demo.model.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerAnswer {
    private GameAnswer gameAnswer;
    private Double seconds;
    private Integer score;
    private Player player;
}
