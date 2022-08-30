package com.example.demo.model.game;

import com.example.demo.model.entity.Question;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Color;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Game {
    //lobby
    private String gameId;
    //reverse sorted
    private List<Player> players = new ArrayList<>();

    //game
    private Boolean gameStarted = false;
    private List<Territory> territories = new ArrayList<>();
    private Boolean territoryToChoose = false;
    private List<Integer> availableTerritories = new ArrayList<>();
    private List<Color> sequence = new ArrayList<>();
    private Integer turn;

    //question
    private Boolean askQuestion = false;
    private GameQuestion question;
    private BattleResult battleResult;

    //end
    private Boolean gameEnded = false;
    //reverse sorted
    private List<Player> finalResults = new ArrayList<>();
}