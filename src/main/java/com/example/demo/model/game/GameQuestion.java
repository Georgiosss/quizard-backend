package com.example.demo.model.game;

import com.example.demo.model.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class GameQuestion {
    private Question question;
    private List<Player> players = new ArrayList<>();
    private Boolean isFinished;
}
