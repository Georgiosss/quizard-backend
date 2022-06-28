package com.example.demo.controller;


import com.example.demo.service.GameRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/game-rules")
public class GameRulesController {


    @Autowired
    private GameRulesService gameRulesService;

    @GetMapping("/get-rules")
    public ResponseEntity<?> getRules() {
        return ResponseEntity.ok(gameRulesService.getRules());
    }
}
