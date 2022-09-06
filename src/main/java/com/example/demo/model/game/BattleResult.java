package com.example.demo.model.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.AccessType;

import java.util.List;

@Data
@AllArgsConstructor
public class BattleResult {
    private GameAnswer gameAnswer;
    //sorted reverse
    private List<PlayerAnswer> playerAnswers;
}
