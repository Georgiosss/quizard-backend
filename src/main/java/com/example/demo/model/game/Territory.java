package com.example.demo.model.game;


import com.example.demo.model.enums.Color;
import lombok.Data;

@Data
public class Territory {
    private Long userId;
    private Integer territoryId;
    private Color color;
    private Integer points;
    private Castle castle;
}
