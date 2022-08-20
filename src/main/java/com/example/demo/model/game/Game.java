package com.example.demo.model.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Game {
    private String gameId;
    List<Long> players = new ArrayList<>();
    private int number;
    private Long winner;
}