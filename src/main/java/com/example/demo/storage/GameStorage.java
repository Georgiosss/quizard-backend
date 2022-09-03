package com.example.demo.storage;

import com.example.demo.model.game.Game;

import java.util.HashMap;
import java.util.Map;

public class GameStorage {

    private static Map<String, Game> games;
    private static GameStorage instance;

    private GameStorage() {
        games = new HashMap<>();
    }

    public static synchronized GameStorage getInstance() {
        if (instance == null) {
            instance = new GameStorage();
        }
        return instance;
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public void setGame(Game game) {
        games.put(game.getGameId(), game);
    }
}