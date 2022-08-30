package com.example.demo.model.game;

import com.example.demo.model.enums.Color;
import lombok.Data;

import java.util.List;

@Data
public class Player {
    private Long userId;
    private String fullName;
    private Color color;
    private Integer score;
    private Boolean active;
    private Boolean territoryToChoose;
}