package com.example.demo.controller;


import com.example.demo.model.dto.response.GetRulesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/game-rules")
public class GameRulesController {



    @GetMapping("/get-rules")
    public ResponseEntity<?> getRules() {
        List<GetRulesResponseDTO> list =  new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            list.add(new GetRulesResponseDTO("წესი ნომერი " + i, "წესი"));
        }
        return ResponseEntity.ok(list);
    }
}
