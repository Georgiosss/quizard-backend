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
    private List<Long> availableTerritories = new ArrayList<>();
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

    @JsonIgnore
    private int currentQuestionType; // 0 -> single, 1 -> multiple

    @JsonIgnore
    private List<GameAnswer> answers = new ArrayList<>();

    @JsonIgnore
    private int territoryDistributionTurn = 0;

    @JsonIgnore
    private List<Long> territoryChoosingUsers = new ArrayList<>();

    @JsonIgnore
    private int territoryChoosingInd = 0;



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
        validateNewUser();

        Color color = Color.fromValue(players.size() + 1);

        if (!playerIsConnected(user.getUserId())) {
            Player player = new Player(
                    user.getUserId(), user.getFullName(), user.getEmail(), color, 1000, true, false
            );
            players.add(player);
        }

        if (players.size() == gameMode.getValue()) {
            finishLobby();
        }
    }

    private void validateNewUser() {
        if (!gameState.equals(GameState.LOBBY)) {
            throw new ApiException("Lobby is over");
        }
    }

    private void finishLobby() {
        gameState = GameState.WAITING_FOR_RESPONSE_INITIAL;
        gameStarted = true;

        SingleChoiceQuestion singleChoiceQuestion = singleChoiceQuestions.get(singleQuestionId++);
        question = new GameQuestion(singleChoiceQuestion, players, false);
        askQuestion = true;
        currentQuestionType = 0;

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
            TerritoryData territory = territories.get(castleId);
            Player player = players.get(i);

            TerritoryData castle = new TerritoryData(
                    player.getUserId(), territory.getTerritoryId(),
                    player.getColor(), new Castle(CastleType.TRIPLE, 3),
                    territory.getNeighbourIds()
            );

            territories.set(castleId, castle);
        }
    }



    public void answer(User user, GameAnswer gameAnswer, Long startTime) {
        Long timeTaken = (new Date().getTime()) - startTime;
        validateAnswer(user.getUserId());

        gameAnswer.setTimeTaken(timeTaken);
        gameAnswer.setUser(user);
        if (currentQuestionType == 0) {
            gameAnswer.setCorrectAnswer(singleChoiceQuestions.get(singleQuestionId).getAnswer().intValue());
        } else {
            gameAnswer.setCorrectAnswer(multipleChoiceQuestions.get(multipleQuestionId).getAnswer());
        }
        answers.add(gameAnswer);

        if (gameState.equals(GameState.WAITING_FOR_RESPONSE_INITIAL)) {
            handleInitialQuestion();
        } else {
//            handleMainQuestion();
        }
    }

    private void handleInitialQuestion() {
        question.setIsFinished(answers.size() == gameMode.getValue());

        if (question.getIsFinished()) {
            answers.sort(new GameAnswerComparator());

            gameState = GameState.CHOOSE_TERRITORY_INITIAL;
            askQuestion = false;
            territoryToChoose = true;

            territoryChoosingUsers.add(answers.get(0).getUser().getUserId());
            territoryChoosingUsers.add(answers.get(1).getUser().getUserId());
            territoryChoosingUsers.add(answers.get(0).getUser().getUserId());

            activateTerritoryToChoose();
        }
    }

    private void activateTerritoryToChoose() {
        Long userId = territoryChoosingUsers.get(territoryChoosingInd++);

        for (Player player : players) {
            player.setTerritoryToChoose(Objects.equals(player.getUserId(), userId));
        }

        availableTerritories = getAvailableTerritories(userId);
    }

    private List<Long> getAvailableTerritories(Long userId) {
        Set<Long> availableNeighbours = new HashSet<>();

        for (TerritoryData territoryData : territories) {
            if (Objects.equals(territoryData.getUserId(), userId)) {
                for (Long neighbour : territoryData.getNeighbourIds()) {
                    if (territories.get(neighbour.intValue() - 1).getColor().equals(Color.TRANSPARENT)) {
                        availableNeighbours.add(neighbour);
                    }
                }
            }
        }

        if (availableNeighbours.isEmpty()) {
            for (TerritoryData territoryData : territories) {
                if (territoryData.getColor().equals(Color.TRANSPARENT)) {
                    availableNeighbours.add(territoryData.getTerritoryId().longValue());
                }
            }
        }

        return new ArrayList<>(availableNeighbours);
    }

    public void justTest() {
        GameAnswer g1 = new GameAnswer(1, 10, 1);
        GameAnswer g2 = new GameAnswer(1, 8, 1);
        GameAnswer g3 = new GameAnswer(1, 10, 2);
        GameAnswer g4 = new GameAnswer(1, 10, 3);
        GameAnswer g5 = new GameAnswer(1, 10, 2);
        GameAnswer g6 = new GameAnswer(1, 2, 3);
        GameAnswer g7 = new GameAnswer(1, 10, 4);

        List<GameAnswer> ga = new ArrayList<>();
        ga.add(g1);
        ga.add(g4);
        ga.add(g2);
        ga.add(g3);
        ga.add(g7);
        ga.add(g6);
        ga.add(g5);

        ga.sort(new GameAnswerComparator());
    }

    private void validateAnswer(Long userId) {
        if (!playerIsConnected(userId)) {
            throw new ApiException("User not in the game");
        }
        if (playerHasAnswered(userId)) {
            throw new ApiException("User already answered");
        }
    }

    private boolean playerHasAnswered(Long id) {
        for (GameAnswer answer : answers) {
            if (Objects.equals(answer.getUser().getUserId(), id)) return true;
        }
        return false;
    }

    private boolean playerIsConnected(Long id) {
        for (Player player : players) {
            if (Objects.equals(player.getUserId(), id)) return true;
        }
        return false;
    }

    public void chooseTerritory(User user, Integer territoryId) {
        validateTerritoryChoose(user.getUserId(), territoryId);

        attachTerritoryToUser(user.getUserId(), territoryId);

        if (territoryChoosingInd == 3) {
            territoryDistributionTurn++;
            territoryToChoose = false;
            askQuestion = true;
            answers.clear();
            availableTerritories.clear();
            territoryChoosingUsers.clear();
            territoryChoosingInd = 0;

            for (Player player : players) {
                player.setTerritoryToChoose(false);
            }

            if (territoryDistributionTurn == 3) {
//                MultipleChoiceQuestion multipleChoiceQuestion = multipleChoiceQuestions.get(multipleQuestionId++);
//                question = new GameQuestion(multipleChoiceQuestion, players, false);

            } else {
                SingleChoiceQuestion singleChoiceQuestion = singleChoiceQuestions.get(singleQuestionId++);
                question = new GameQuestion(singleChoiceQuestion, players, false);
                gameState = GameState.WAITING_FOR_RESPONSE_INITIAL;
            }
        } else {
            activateTerritoryToChoose();
        }
    }

    private void attachTerritoryToUser(Long userId, Integer territoryId) {
        Player player = getPlayer(userId);
        TerritoryData territoryData = territories.get(territoryId - 1);

        territoryData.setUserId(userId);
        territoryData.setColor(player.getColor());
    }

    private void validateTerritoryChoose(Long userId, Integer territoryId) {
        if (!playerIsConnected(userId)) {
            throw new ApiException("User not in the game");
        }

        for (Player player : players) {
            if (Objects.equals(player.getUserId(), userId) && !player.getTerritoryToChoose()) {
                throw new ApiException("User can't choose territory");
            }
        }

        TerritoryData territoryData = territories.get(territoryId - 1);
        if (!territoryData.getColor().equals(Color.TRANSPARENT)) {
            throw new ApiException("Territory already chosen");
        }
    }

    private Player getPlayer(Long userId) {
        for (Player player : players) {
            if (Objects.equals(player.getUserId(), userId)) {
                return player;
            }
        }

        throw new ApiException("User not in the game");
    }
}