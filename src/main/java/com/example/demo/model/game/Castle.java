package com.example.demo.model.game;


import com.example.demo.model.enums.CastleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Castle {
    private CastleType castleType;
    private Integer leftTowers;

    public Castle() {
        this.castleType = CastleType.NON;
        this.leftTowers = 0;
    }

    public Castle(CastleType castleType) {
        this.castleType = castleType;
    }

    public void destroyTower() {
        leftTowers--;
    }
}
