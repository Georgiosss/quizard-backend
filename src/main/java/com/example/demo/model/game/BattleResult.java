package com.example.demo.model.game;

import lombok.Data;

import java.util.List;

@Data
public class BattleResult {
    private GameAnswer gameAnswer;
    //sorted reverse
    private List<PlayerAnswer> playerAnswers;
}
