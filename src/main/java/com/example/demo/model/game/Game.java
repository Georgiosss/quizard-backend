package com.example.demo.model.game;

import com.example.demo.model.entity.Question;
import com.example.demo.model.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Game {
    private String gameId;

    private List<Player> players = new ArrayList<>();
    private List<Territory> territories = new ArrayList<>();
    private Boolean gameStarted = false;

    private Boolean gameEnded = false;
    private List<Player> finalResults = new ArrayList<>();

    private Boolean askQuestion = false;
    //private GameQuestion question;
}