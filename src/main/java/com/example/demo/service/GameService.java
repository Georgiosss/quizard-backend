package com.example.demo.service;


import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Color;
import com.example.demo.model.game.Game;
import com.example.demo.model.game.Player;
import com.example.demo.model.game.Territory;
import com.example.demo.storage.GameStorage;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.W3CDomHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static jdk.net.SocketFlow.Status.IN_PROGRESS;

@Service
@AllArgsConstructor
public class GameService {

    @Autowired
    private UserService userService;

    public Game createGame() {
        Game game = new Game();
        game.setGameId(UUID.randomUUID().toString());
        List<Territory> territoryList =  new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Territory territory = new Territory();
            territory.setTerritoryId("t" + i);
            territory.setColor(Color.TRANSPARENT);
            territory.setUserId(1L);
            territory.setPoints(200);
            territoryList.add(territory);
        }
        game.setTerritories(territoryList);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToGame(String gameId)  {
        Game game = GameStorage.getInstance().getGames().get(gameId);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(String gameId, String territoryId) {
        Game game = GameStorage.getInstance().getGames().get(gameId);
        List<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        for (int i = 0; i < game.getTerritories().size(); i++) {
            if (game.getTerritories().get(i).getTerritoryId().equals(territoryId)) {
                game.getTerritories().get(i).setColor(colors.get(new Random().nextInt(3)));
            }
        }
        GameStorage.getInstance().setGame(game);
        return game;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        return userService.getById(userId);
    }
}