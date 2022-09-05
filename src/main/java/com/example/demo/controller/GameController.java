package com.example.demo.controller;

import com.example.demo.model.dto.request.*;
import com.example.demo.model.dto.response.CreateResponseDTO;
import com.example.demo.model.game.Game;
import com.example.demo.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /*

     */
    @PostMapping("/create")
    public ResponseEntity<CreateResponseDTO> create(@RequestBody CreateRequestDTO request) {
        return ResponseEntity.ok(gameService.createGame(request.getQuestionPackCode(), request.getUserIds()));
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody ConnectRequestDTO request) {
        Game game = gameService.connectToGame(request.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/gameplay/answer")
    public ResponseEntity<?> answer(@RequestBody AnswerRequestDTO request) {
        Game game = gameService.answer(request.getGameId(), request.getGameAnswer(), request.getStartTime());
        if (game.getQuestion().getIsFinished()) {
            simpMessagingTemplate.convertAndSend("/topic/game/" + request.getGameId(), game);
        }
        return ResponseEntity.ok(game);
    }

    @PostMapping("/gameplay/chooseTerritory")
    public ResponseEntity<?> chooseTerritory(@RequestBody ChooseTerritoryRequestDTO request) {
        Game game = gameService.chooseTerritory(request.getGameId(), request.getTerritoryId());
        simpMessagingTemplate.convertAndSend("/topic/game/" + request.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/test")
    public void test() {
        gameService.test();
    }

}