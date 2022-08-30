package com.example.demo.service;


import com.example.demo.model.dto.response.CreateResponseDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Color;
import com.example.demo.model.game.Game;
import com.example.demo.model.game.GameAnswer;
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

    public CreateResponseDTO createGame() {
        return null;
    }

    public Game connectToGame(String gameId)  {
        return null;
    }


    public Game gamePlay(String gameId, String territoryId) {
        return null;
    }

    public Game chooseTerritory(String gameId, Integer territoryId) {
        return null;
    }

    public Game answer(String gameId, GameAnswer gameAnswer) {
        return null;
    }
}