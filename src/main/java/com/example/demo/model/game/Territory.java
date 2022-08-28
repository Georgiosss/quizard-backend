package com.example.demo.model.game;


import com.example.demo.model.enums.Color;
import lombok.Data;

@Data
public class Territory {
    private Long userId;
    private String territoryId;
    private Color color;
    private int points;
}
