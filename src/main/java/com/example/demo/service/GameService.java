package com.example.demo.service;


import com.example.demo.model.enums.Errors;
import com.example.demo.utils.Constants;
import com.example.demo.utils.Utils;
import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.response.CreateResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.GameMode;
import com.example.demo.model.game.Game;
import com.example.demo.model.game.GameAnswer;
import com.example.demo.model.game.TerritoryData;
import com.example.demo.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class GameService {

    @Autowired
    private UserService userService;

    @Autowired
    private TerritoryService territoryService;

    @Autowired
    private QuestionPackService questionPackService;

    public CreateResponseDTO createGame(String questionPackCode, List<Long> userIds) {
        if (userIds.size() < 2) {
            throw new ApiException(Errors.NOT_ENOUGH_USERS.getValue());
        }

        QuestionPack questionPack = questionPackService.getByCode(questionPackCode);

        List<Pair<String, List<String>>> gameDistribution = getGamesDistribution(questionPack, userIds);

        return new CreateResponseDTO(gameDistribution);
    }

    private List<Pair<String, List<String>>> getGamesDistribution(QuestionPack questionPack, List<Long> userIds) {
        List<Pair<String, List<String>>> distribution = new ArrayList<>();

        int usersCount = userIds.size();
        Integer userInd = 0;

        while (usersCount % 3 != 0) {
            createSingleGame(questionPack, 2, userIds, userInd, distribution);
            usersCount -= 2;
        }

        while (usersCount != 0) {
            createSingleGame(questionPack, 3, userIds, userInd, distribution);
            usersCount -= 3;
        }

        return distribution;
    }

    private void createSingleGame(QuestionPack questionPack, int numPlayers,
                                  List<Long> userIds, Integer userInd,
                                  List<Pair<String, List<String>>> distribution) {
        Pair<List<SingleChoiceQuestion>, List<MultipleChoiceQuestion>> questions = getRandomQuestions(questionPack);
        List<SingleChoiceQuestion> singleChoiceQuestions = questions.getFirst();
        List<MultipleChoiceQuestion> multipleChoiceQuestions = questions.getSecond();

        String gameId = Utils.generateRandomCode(6);
        List<Territory> territories = territoryService.getAllTerritories();

        List<TerritoryData> territoryDataList = new ArrayList<>();
        for (Territory territory : territories) {
            StringTokenizer st = new StringTokenizer(territory.getNeighbours(), ",");
            List<Long> neighbours = new ArrayList<>();
            while (st.hasMoreTokens()) {
                neighbours.add(Long.valueOf(st.nextToken()));
            }
            TerritoryData territoryData = new TerritoryData(territory.getId(), neighbours);
            territoryDataList.add(territoryData);
        }
        GameMode gameMode = GameMode.fromValue(numPlayers);

        Game game = new Game(gameId, territoryDataList, singleChoiceQuestions, multipleChoiceQuestions, gameMode);
        GameStorage.getInstance().setGame(game);

        Pair<String, List<String>> gameInfo = new Pair<>(gameId, new ArrayList<>());

        for (int i = 0; i < numPlayers; i++) {
            Long userId = userIds.get(userInd);
            User user = userService.getById(userId);
            gameInfo.getSecond().add(user.getEmail());
            userInd++;
        }

        distribution.add(gameInfo);
    }

    private Pair<List<SingleChoiceQuestion>, List<MultipleChoiceQuestion>> getRandomQuestions(QuestionPack questionPack) {
        if (questionPack.getSingleChoiceQuestions().size() < Constants.SINGLE_CHOICE_QUESTIONS_THRESHOLD ||
                questionPack.getMultipleChoiceQuestions().size() < Constants.MULTIPLE_CHOICE_QUESTIONS_THRESHOLD) {
            throw new ApiException(Errors.NOT_ENOUGH_QUESTIONS.getValue());
        }

        List<SingleChoiceQuestion> resultSingleQuestions = new ArrayList<>();
        List<MultipleChoiceQuestion> resultMultipleQuestions = new ArrayList<>();

        List<SingleChoiceQuestion> singleChoiceQuestions = questionPack.getSingleChoiceQuestions();
        List<MultipleChoiceQuestion> multipleChoiceQuestions = questionPack.getMultipleChoiceQuestions();

        Collections.shuffle(singleChoiceQuestions);
        Collections.shuffle(multipleChoiceQuestions);

        for (int i = 0; i < Constants.SINGLE_CHOICE_QUESTIONS_THRESHOLD; i++) {
            resultSingleQuestions.add(singleChoiceQuestions.get(i));
        }

        for (int i = 0; i < Constants.MULTIPLE_CHOICE_QUESTIONS_THRESHOLD; i++) {
            resultMultipleQuestions.add(multipleChoiceQuestions.get(i));
        }

        return new Pair<>(resultSingleQuestions, resultMultipleQuestions);
    }

    public Game connectToGame(String gameId)  {
        User user = userService.getAuthenticatedUser();
        Game game = GameStorage.getInstance().getGame(gameId);
        game.addPlayer(user);
        return game;
    }

    public Game chooseTerritory(String gameId, Integer territoryId) {
        User user = userService.getAuthenticatedUser();
        Game game = GameStorage.getInstance().getGame(gameId);
        game.chooseTerritory(user, territoryId);
        return game;
    }

    public Game answer(String gameId, GameAnswer gameAnswer, Long startTime) {
        User user = userService.getAuthenticatedUser();
        Game game = GameStorage.getInstance().getGame(gameId);
        game.answer(user, gameAnswer, startTime);
        return game;
    }

}