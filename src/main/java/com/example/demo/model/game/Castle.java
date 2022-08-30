package com.example.demo.model.game;


import com.example.demo.model.enums.CastleType;
import lombok.Data;

@Data
public class Castle {
    private CastleType castleType;
    private Integer leftTowers;
}
