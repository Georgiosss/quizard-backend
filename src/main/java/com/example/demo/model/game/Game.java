package com.example.demo.model.game;

import com.example.demo.utils.Constants;
import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.entity.MultipleChoiceQuestion;
import com.example.demo.model.entity.SingleChoiceQuestion;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

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
    private Integer turn = 0;

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
    private QuestionType currentQuestionType; // 0 -> single, 1 -> multiple

    @JsonIgnore
    private List<GameAnswer> answers = new ArrayList<>();

    @JsonIgnore
    private int territoryDistributionTurn = 0;

    @JsonIgnore
    private List<Long> territoryChoosingUsers = new ArrayList<>();

    @JsonIgnore
    private int territoryChoosingInd = 0;

    @JsonIgnore
    private Long attackerUserId;

    @JsonIgnore
    private Long defenderUserId;

    @JsonIgnore
    private Integer battleTerritoryId;

    @JsonIgnore
    private Boolean shouldUpdate = true;



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
            throw new ApiException(Errors.LOBBY_IS_OVER.getValue());
        }
    }

    private void finishLobby() {
        gameStarted = true;

        prepareSingleChoiceQuestion(GameState.WAITING_FOR_RESPONSE_INITIAL, players);

        distributeCastles();
    }

    private void distributeCastles() {
        List<Integer> availableRegionIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12);
        Random random = new Random();
        int prevCastleId = -1;
        int regionInd = 0;
        int playerCount = gameMode.getValue();
        int intervalSize = (11 / playerCount) + 1;
        List<List<Integer>> regionsForPlayers = new ArrayList<>();
        List<Integer> castleIds = new ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            List<Integer> regionsForPlayer = new ArrayList<>();
            for (int j = 0; j < intervalSize && regionInd < availableRegionIds.size(); j++) {
                regionsForPlayer.add(availableRegionIds.get(regionInd));
                regionInd++;
            }
            regionsForPlayers.add(regionsForPlayer);
        }

        for (int i = 0; i < playerCount; i++) {
            List<Integer> regionsForPlayer = regionsForPlayers.get(i);
            if (prevCastleId != -1) {
                TerritoryData territoryData = territories.get(prevCastleId - 1);
                regionsForPlayer = regionsForPlayer.stream().filter(
                        regionId -> !territoryData.getNeighbourIds().contains(regionId.longValue())
                ).collect(Collectors.toList());
            }
            int randInd = random.nextInt(regionsForPlayer.size());
            prevCastleId = regionsForPlayer.get(randInd);
            castleIds.add(prevCastleId);
        }

        updateTerritoriesData(castleIds);
    }

    private void updateTerritoriesData(List<Integer> castleIds) {
        for (int i = 0; i < castleIds.size(); i++) {
            int castleId = castleIds.get(i) - 1;
            TerritoryData territory = territories.get(castleId);
            Player player = players.get(i);

            TerritoryData castle = new TerritoryData(
                    player.getUserId(), territory.getTerritoryId(),
                    Constants.TRIPLE_TOWER_POINTS, player.getColor(),
                    new Castle(CastleType.TRIPLE, 3),
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
        if (currentQuestionType == QuestionType.SINGLE) {
            gameAnswer.setCorrectAnswer(singleChoiceQuestions.get(singleQuestionId - 1).getAnswer().intValue());
        } else {
            gameAnswer.setCorrectAnswer(multipleChoiceQuestions.get(multipleQuestionId - 1).getAnswer());
        }
        answers.add(gameAnswer);

        if (gameState.equals(GameState.WAITING_FOR_RESPONSE_INITIAL)) {
            handleInitialQuestion();
        } else if (gameState.equals(GameState.WAITING_FOR_RESPONSE_MAIN)){
            handleMainQuestion();
        }
    }

    private void handleInitialQuestion() {
        question.setIsFinished(answers.size() == gameMode.getValue());
        shouldUpdate = question.getIsFinished();

        if (question.getIsFinished()) {
            answers.sort(new GameAnswerComparator());
            prepareBattleResult();

            territoryChoosingUsers.add(answers.get(0).getUser().getUserId());
            territoryChoosingUsers.add(answers.get(1).getUser().getUserId());
            if (gameMode.equals(GameMode.THREE_PLAYER)) {
                territoryChoosingUsers.add(answers.get(0).getUser().getUserId());
            }

            activateTerritoryToChooseInitial();
        }
    }

    private void activateTerritoryToChooseInitial() {
        gameState = GameState.CHOOSE_TERRITORY_INITIAL;
        askQuestion = false;
        territoryToChoose = true;

        Long userId = territoryChoosingUsers.get(territoryChoosingInd++);

        for (Player player : players) {
            player.setTerritoryToChoose(Objects.equals(player.getUserId(), userId));
        }

        availableTerritories = getAvailableTerritoriesToChoose(userId);
    }

    private List<Long> getAvailableTerritoriesToChoose(Long userId) {
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

    private void handleMainQuestion() {
        question.setIsFinished(answers.size() == 2);
        shouldUpdate = question.getIsFinished();

        if (question.getIsFinished()) {
            answers.sort(new GameAnswerComparator());
            prepareBattleResult();
            BattleOutcome battleOutcome = getBattleOutcome();

            TerritoryData battleTerritory = territories.get(battleTerritoryId - 1);
            if (battleTerritory.getCastle().getCastleType().equals(CastleType.TRIPLE)) {
                handleTripleTowerAttack(battleOutcome);
            } else {
                handleSingleTowerAttack(battleOutcome);
            }
        }
    }

    private void handleTripleTowerAttack(BattleOutcome battleOutcome) {
        switch (battleOutcome) {
            case BOTH_CORRECT: {
                List<Player> battlePlayers = Arrays.asList(getPlayer(attackerUserId), getPlayer(defenderUserId));
                prepareSingleChoiceQuestion(GameState.WAITING_FOR_RESPONSE_MAIN, battlePlayers);
            } break;
            case ATTACKER_WINS: {
                TerritoryData territoryData = territories.get(battleTerritoryId - 1);
                if (territoryData.getCastle().getLeftTowers() == 1) {
                    destroyPlayer(territoryData);
                    turn++;
                    activateTerritoryToChooseMain();
                } else {
                    territoryData.getCastle().destroyTower();

                    List<Player> battlePlayers = Arrays.asList(getPlayer(attackerUserId), getPlayer(defenderUserId));
                    prepareMultipleChoiceQuestion(battlePlayers);
                }
            } break;
            case DEFENDER_WINS: {
                Player defender = getPlayer(defenderUserId);
                defender.addScore(Constants.SUCCESSFUL_DEFENSE_POINTS);
                turn++;
                activateTerritoryToChooseMain();
            }
            case BOTH_INCORRECT: {
                if (currentQuestionType.equals(QuestionType.SINGLE)) {
                    List<Player> battlePlayers = Arrays.asList(getPlayer(attackerUserId), getPlayer(defenderUserId));
                    prepareSingleChoiceQuestion(GameState.WAITING_FOR_RESPONSE_MAIN, battlePlayers);
                } else {
                    turn++;
                    activateTerritoryToChooseMain();
                }
            } break;
        }
    }

    private void destroyPlayer(TerritoryData territoryData) {
        territoryData.setCastle(new Castle(CastleType.SINGLE));
        for (TerritoryData territory : territories) {
            if (Objects.equals(territory.getUserId(), defenderUserId)) {
                attachTerritoryToUser(attackerUserId, territory.getTerritoryId());
            }
        }
        Player player = getPlayer(defenderUserId);
        player.setActive(false);
        player.setScore(0);

        int activeCount = 0;
        for (Player p : players) {
            if (p.getActive()) {
                activeCount++;
            }
        }
        if (activeCount == 1) {
            prepareFinalResults();
        }
    }

    private void handleSingleTowerAttack(BattleOutcome battleOutcome) {
        switch (battleOutcome) {
            case BOTH_CORRECT: {
                List<Player> battlePlayers = Arrays.asList(getPlayer(attackerUserId), getPlayer(defenderUserId));
                prepareSingleChoiceQuestion(GameState.WAITING_FOR_RESPONSE_MAIN, battlePlayers);
            } break;
            case ATTACKER_WINS: {
                attachTerritoryToUser(attackerUserId, battleTerritoryId);
                turn++;
                activateTerritoryToChooseMain();
            } break;
            case DEFENDER_WINS: {
                Player defender = getPlayer(defenderUserId);
                defender.addScore(Constants.SUCCESSFUL_DEFENSE_POINTS);
                turn++;
                activateTerritoryToChooseMain();
            } break;
            case BOTH_INCORRECT: {
                if (currentQuestionType.equals(QuestionType.SINGLE)) {
                    List<Player> battlePlayers = Arrays.asList(getPlayer(attackerUserId), getPlayer(defenderUserId));
                    prepareSingleChoiceQuestion(GameState.WAITING_FOR_RESPONSE_MAIN, battlePlayers);
                } else {
                    turn++;
                    activateTerritoryToChooseMain();
                }
            } break;
        }
    }


    private BattleOutcome getBattleOutcome() {
        GameAnswer attackerAnswer = new GameAnswer(), defenderAnswer = new GameAnswer();

        for (GameAnswer gameAnswer : answers) {
            if (Objects.equals(gameAnswer.getUser().getUserId(), attackerUserId)) {
                attackerAnswer = gameAnswer;
            } else {
                defenderAnswer = gameAnswer;
            }
        }
        boolean attackerCorrect = attackerAnswer.getAnswer().equals(attackerAnswer.getCorrectAnswer());
        boolean defenderCorrect = defenderAnswer.getAnswer().equals(defenderAnswer.getCorrectAnswer());

        if (currentQuestionType.equals(QuestionType.SINGLE)) {
            if (attackerCorrect && defenderCorrect) {
                if (attackerAnswer.getTimeTaken() < defenderAnswer.getTimeTaken()) return BattleOutcome.ATTACKER_WINS;
                else if (attackerAnswer.getTimeTaken() > defenderAnswer.getTimeTaken()) return BattleOutcome.DEFENDER_WINS;
                else return BattleOutcome.BOTH_CORRECT;
            } else  if (attackerCorrect) return BattleOutcome.ATTACKER_WINS;
            else if (defenderCorrect) return BattleOutcome.DEFENDER_WINS;
            else {
                int attackerDiff = Math.abs(attackerAnswer.getAnswer() - attackerAnswer.getCorrectAnswer());
                int defenderDiff = Math.abs(defenderAnswer.getAnswer() - defenderAnswer.getCorrectAnswer());

                if (attackerDiff < defenderDiff) return BattleOutcome.ATTACKER_WINS;
                else if (attackerDiff > defenderDiff) return BattleOutcome.DEFENDER_WINS;
                else {
                    if (attackerAnswer.getTimeTaken() < defenderAnswer.getTimeTaken()) return BattleOutcome.ATTACKER_WINS;
                    else if (attackerAnswer.getTimeTaken() > defenderAnswer.getTimeTaken()) return BattleOutcome.DEFENDER_WINS;
                    else return BattleOutcome.BOTH_INCORRECT;
                }
            }
        } else {
            if (attackerCorrect && defenderCorrect) return BattleOutcome.BOTH_CORRECT;
            else if (attackerCorrect) return BattleOutcome.ATTACKER_WINS;
            else if (defenderCorrect) return BattleOutcome.DEFENDER_WINS;
            else return BattleOutcome.BOTH_INCORRECT;
        }
    }

    public void justTest() {
        if (players.size() == 2) {
            for (int i = 0; i < 6; i++) {
                TerritoryData territoryData = territories.get(i);
                if (territoryData.getColor().equals(Color.TRANSPARENT)) {
                    attachTerritoryToUser(players.get(0).getUserId(), territoryData.getTerritoryId());
                }
            }
            for (int i = 6; i < 12; i++) {
                TerritoryData territoryData = territories.get(i);
                if (territoryData.getColor().equals(Color.TRANSPARENT)) {
                    attachTerritoryToUser(players.get(1).getUserId(), territoryData.getTerritoryId());
                }
            }
            for (int i = 0; i < 6; i++) {
                sequence.add(Color.RED);
                sequence.add(Color.GREEN);
            }
            activateTerritoryToChooseMain();
        }
    }

    private void validateAnswer(Long userId) {
        if (!playerIsConnected(userId)) {
            throw new ApiException(Errors.USER_NOT_IN_GAME.getValue());
        }
        if (playerHasAnswered(userId)) {
            throw new ApiException(Errors.USER_ALREADY_ANSWERED.getValue());
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

        if (gameState.equals(GameState.CHOOSE_TERRITORY_INITIAL)) {
            handleInitialTerritoryChoose(user, territoryId);
        } else if (gameState.equals(GameState.CHOOSE_TERRITORY_MAIN)) {
            handleMainTerritoryChoose(user, territoryId);
        }
    }

    private void handleInitialTerritoryChoose(User user, Integer territoryId) {
        battleResult = null;
        attachTerritoryToUser(user.getUserId(), territoryId);

        if (territoryChoosingInd == gameMode.getValue()) {
            territoryDistributionTurn++;
            territoryChoosingUsers.clear();
            territoryChoosingInd = 0;

            for (Player player : players) {
                player.setTerritoryToChoose(false);
            }

            if (territoryDistributionTurn == ((12 / gameMode.getValue()) - 1)) {
                generateTurnSequence();
                activateTerritoryToChooseMain();
            } else {
                prepareSingleChoiceQuestion(GameState.WAITING_FOR_RESPONSE_INITIAL, players);
            }
        } else {
            activateTerritoryToChooseInitial();
        }
    }

    private void handleMainTerritoryChoose(User user, Integer territoryId) {
        battleResult = null;
        TerritoryData territoryData = territories.get(territoryId - 1);
        defenderUserId = players.get(territoryData.getColor().getValue() - 1).getUserId();
        battleTerritoryId = territoryId;

        Player player = getPlayer(user.getUserId());
        player.setTerritoryToChoose(false);

        List<Player> battlePlayers = Arrays.asList(getPlayer(attackerUserId), getPlayer(defenderUserId));
        prepareMultipleChoiceQuestion(battlePlayers);
    }

    private void prepareSingleChoiceQuestion(GameState gameState, List<Player> questionPlayers) {
        this.gameState = gameState;
        askQuestion = true;
        territoryToChoose = false;
        answers.clear();
        availableTerritories.clear();

        SingleChoiceQuestion singleChoiceQuestion = singleChoiceQuestions.get(singleQuestionId++);
        question = new GameQuestion(singleChoiceQuestion, questionPlayers, false);
        currentQuestionType = QuestionType.SINGLE;
    }


    private void prepareMultipleChoiceQuestion(List<Player> questionPlayers) {
        this.gameState = GameState.WAITING_FOR_RESPONSE_MAIN;
        askQuestion = true;
        territoryToChoose = false;
        answers.clear();
        availableTerritories.clear();

        MultipleChoiceQuestion multipleChoiceQuestion = multipleChoiceQuestions.get(multipleQuestionId++);
        question = new GameQuestion(multipleChoiceQuestion, questionPlayers, false);
        currentQuestionType = QuestionType.MULTIPLE;
    }

    private void attachTerritoryToUser(Long userId, Integer territoryId) {
        TerritoryData territoryData = territories.get(territoryId - 1);
        Player newOwner = getPlayer(userId);
        Player previousOwner = getPlayer(territoryData.getUserId());

        newOwner.addScore(territoryData.getPoints());
        previousOwner.addScore(-1 * territoryData.getPoints());

        territoryData.setUserId(userId);
        territoryData.setCastle(new Castle(CastleType.SINGLE, 1));
        territoryData.setColor(newOwner.getColor());
    }

    private void validateTerritoryChoose(Long userId, Integer territoryId) {
        if (!playerIsConnected(userId)) {
            throw new ApiException(Errors.USER_NOT_IN_GAME.getValue());
        }

        for (Player player : players) {
            if (Objects.equals(player.getUserId(), userId) && !player.getTerritoryToChoose()) {
                throw new ApiException(Errors.USER_CANT_CHOOSE_TERRITORY.getValue());
            }
        }

        boolean availableTerritory = false;
        for (Long availableTerritoryId : availableTerritories) {
            if (territoryId.equals(availableTerritoryId.intValue())) {
                availableTerritory = true;
            }
        }
        if (!availableTerritory) {
            throw new ApiException(Errors.USER_CANT_CHOOSE_TERRITORY.getValue());
        }
    }

    private void generateTurnSequence() {
        int epochs = 12 / gameMode.getValue();

        for (int i = 0; i < epochs; i++) {
            List<Color> colors = new ArrayList<>();
            for (int j = 1; j <= gameMode.getValue(); j++) {
                colors.add(Color.fromValue(j));
            }
            Collections.shuffle(colors, new Random());
            sequence.addAll(colors);
        }
    }

    private void activateTerritoryToChooseMain() {
        answers.clear();

        if (turn == Constants.TURN_COUNT) {
            prepareFinalResults();
        } else {
            gameState = GameState.CHOOSE_TERRITORY_MAIN;
            askQuestion = false;
            territoryToChoose = true;

            Color currentColor = sequence.get(turn);
            attackerUserId = players.get(currentColor.getValue() - 1).getUserId();

            for (Player player : players) {
                player.setTerritoryToChoose(player.getColor().equals(currentColor));
            }

            availableTerritories = getAvailableTerritoriesToAttack(attackerUserId);
        }
    }

    private List<Long> getAvailableTerritoriesToAttack(Long userId) {
        Set<Long> availableTerritoriesSet = new HashSet<>();

        for (TerritoryData territoryData : territories) {
            if (Objects.equals(territoryData.getUserId(), userId)) {
                for (Long neighbourId : territoryData.getNeighbourIds()) {
                    if (!Objects.equals(territories.get(neighbourId.intValue() - 1).getUserId(), userId)) {
                        availableTerritoriesSet.add(neighbourId);
                    }
                }
            }
        }

        return new ArrayList<>(availableTerritoriesSet);
    }

    private void prepareBattleResult() {
        GameAnswer gameAnswer;
        if (currentQuestionType.equals(QuestionType.SINGLE)) {
            int answer = singleChoiceQuestions.get(singleQuestionId - 1).getAnswer().intValue();
            gameAnswer = new GameAnswer(answer, 0);
        } else {
            int answer = multipleChoiceQuestions.get(multipleQuestionId - 1).getAnswer();
            gameAnswer = new GameAnswer(answer, 1);
        }
        List<PlayerAnswer> playerAnswers = new ArrayList<>();
        for (GameAnswer answer : answers) {
            Player player = getPlayer(answer.getUser().getUserId());
            Double seconds = answer.getTimeTaken().doubleValue() / 1000.0;
            PlayerAnswer playerAnswer = new PlayerAnswer(answer, seconds, 100, player);
            playerAnswers.add(playerAnswer);
        }
        battleResult = new BattleResult(gameAnswer, playerAnswers);
    }

    private void prepareFinalResults() {
        gameEnded = true;

        Map<Integer, Player> playerResults = new TreeMap<>(Collections.reverseOrder());
        for (Player player : players) {
            playerResults.put(player.getScore(), player);
        }

        finalResults.addAll(playerResults.values());
    }

    private Player getPlayer(Long userId) {
        for (Player player : players) {
            if (Objects.equals(player.getUserId(), userId)) {
                return player;
            }
        }

        throw new ApiException(Errors.USER_NOT_IN_GAME.getValue());
    }

}