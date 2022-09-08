package com.example.demo.model.game;

import com.example.demo.model.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private Long userId;
    private String fullName;
    private String email;
    private Color color;
    private Integer score;
    private Boolean active;
    private Boolean territoryToChoose;

    public void addScore(int amount) {
        this.score += amount;
    }
}