package com.example.demo.model.game;

import com.example.demo.model.entity.MultipleChoiceQuestion;
import com.example.demo.model.entity.Question;
import com.example.demo.model.entity.SingleChoiceQuestion;
import com.example.demo.model.enums.Color;
import com.example.demo.model.enums.GameState;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<TerritoryData> territories = new ArrayList<>();
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


    @JsonIgnore
    private GameState gameState;

    @JsonIgnore
    private List<SingleChoiceQuestion> singleChoiceQuestions;

    @JsonIgnore
    private List<MultipleChoiceQuestion> multipleChoiceQuestions;

    @JsonIgnore
    private int playerCount;

    public Game(String id, List<TerritoryData> territories,
                List<SingleChoiceQuestion> singleChoiceQuestions,
                List<MultipleChoiceQuestion> multipleChoiceQuestions) {
        this.gameId = id;
        this.territories = territories;
        this.singleChoiceQuestions = singleChoiceQuestions;
        this.multipleChoiceQuestions = multipleChoiceQuestions;
        this.gameState = GameState.LOBBY;
    }

//    @JsonIgnore
//    private GameMetaData gameMetaData;


}