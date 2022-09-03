package com.example.demo.model.game;

import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.entity.MultipleChoiceQuestion;
import com.example.demo.model.entity.SingleChoiceQuestion;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.CastleType;
import com.example.demo.model.enums.Color;
import com.example.demo.model.enums.GameMode;
import com.example.demo.model.enums.GameState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;

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


//    @JsonIgnore
    private GameState gameState;

    @JsonIgnore
    private List<SingleChoiceQuestion> singleChoiceQuestions;

    @JsonIgnore
    private List<MultipleChoiceQuestion> multipleChoiceQuestions;

    @JsonIgnore
    private GameMode gameMode;

    @JsonIgnore
    private int singleQuestionId = 0;

    @JsonIgnore
    private int multipleQuestionId = 0;



    public Game(String id, List<TerritoryData> territories,
                List<SingleChoiceQuestion> singleChoiceQuestions,
                List<MultipleChoiceQuestion> multipleChoiceQuestions,
                GameMode gameMode) {
        this.gameId = id;
        this.territories = territories;
        this.singleChoiceQuestions = singleChoiceQuestions;
        this.multipleChoiceQuestions = multipleChoiceQuestions;
        this.gameState = GameState.LOBBY;
        this.gameMode = gameMode;
    }

//    @JsonIgnore
//    private GameMetaData gameMetaData;

    public void addPlayer(User user) {
        validateNewUser(user.getUserId());

        Color color = Color.fromValue(players.size() + 1);
        Player player = new Player(
                user.getUserId(), user.getFullName(), color, 0, true, false
        );
        players.add(player);

        if (players.size() == gameMode.getValue()) {
            finishLobby();
        }
    }

    private void validateNewUser(Long userId) {
        if (!gameState.equals(GameState.LOBBY)) {
            throw new ApiException("Lobby is over");
        }
        if (playerIsConnected(userId)) {
            throw new ApiException("User already in the game");
        }
    }

    private void finishLobby() {
        gameState = GameState.WAITING_FOR_RESPONSE_INITIAL;
        gameStarted = true;

        SingleChoiceQuestion singleChoiceQuestion = singleChoiceQuestions.get(singleQuestionId++);
        question = new GameQuestion(singleChoiceQuestion, players, false);
        askQuestion = true;

        distributeCastles();
    }

    // 1, 2, 3, 11    :     7, 8, 10, 12     :     4, 5, 6
    private void distributeCastles() {
        List<Integer> regionIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12);
        Random random = new Random();
        int lastInd = 0;
        int playerCount = gameMode.getValue();
        int intervalSize = (11 / playerCount) + 1;
        List<Integer> castleIds = new ArrayList<>();

        for (int i = 0; i < (playerCount - 1); i++) {
            int randInd = random.nextInt(intervalSize) + lastInd;
            lastInd += intervalSize;
            castleIds.add(regionIds.get(randInd));
        }

        intervalSize--;
        int randInd = random.nextInt(intervalSize) + lastInd;
        castleIds.add(regionIds.get(randInd));

        updateTerritoriesData(castleIds);
    }

    private void updateTerritoriesData(List<Integer> castleIds) {
        for (int i = 0; i < castleIds.size(); i++) {
            int castleId = castleIds.get(i) - 1;
            int territoryId = territories.get(castleId).getTerritoryId();
            Player player = players.get(i);

            TerritoryData castle = new TerritoryData(
                    player.getUserId(), territoryId,
                    player.getColor(), new Castle(CastleType.TRIPLE, 3)
            );

            territories.set(castleId, castle);
        }
    }

    private boolean playerIsConnected(Long id) {
        for (Player player : players) {
            if (Objects.equals(player.getUserId(), id)) return true;
        }
        return false;
    }

}