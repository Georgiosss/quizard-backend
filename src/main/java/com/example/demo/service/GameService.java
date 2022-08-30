package com.example.demo.service;


import com.example.demo.model.entity.User;
import com.example.demo.model.game.Game;
import com.example.demo.model.game.Player;
import com.example.demo.storage.GameStorage;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        game.getPlayers().add(userService.getAuthenticatedUser().getUserId());
        game.setWinner(-1L);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToGame(String gameId)  {
        Game game = GameStorage.getInstance().getGames().get(gameId);
        game.getPlayers().add(userService.getAuthenticatedUser().getUserId());
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(String gameId) {
        Game game = GameStorage.getInstance().getGames().get(gameId);
        if (game.getWinner() != -1) return game;
        game.setNumber(game.getNumber() + 1);
        if (game.getNumber() == 10) {
            game.setWinner(userService.getAuthenticatedUser().getUserId());
        }
        GameStorage.getInstance().setGame(game);
        return game;
    }
}