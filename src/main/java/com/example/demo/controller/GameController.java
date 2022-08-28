package com.example.demo.controller;

import com.example.demo.model.dto.request.ConnectRequestDTO;
import com.example.demo.model.dto.request.GamePlayRequestDTO;
import com.example.demo.model.game.Game;
import com.example.demo.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody String nothing) {
        System.out.println("shemovida");
        return ResponseEntity.ok(gameService.createGame());
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequestDTO request) {
        return ResponseEntity.ok(gameService.connectToGame(request.getGameId()));
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlayRequestDTO request) {
        Game game = gameService.gamePlay(request.getGameId(), request.getTerritoryId());
        simpMessagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }
}