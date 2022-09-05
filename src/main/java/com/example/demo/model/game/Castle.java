package com.example.demo.model.game;


import com.example.demo.model.enums.CastleType;
<<<<<<< HEAD
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
=======
import lombok.Data;

@Data
public class Castle {
    private CastleType castleType;
    private Integer leftTowers;
>>>>>>> 214cb7997eda7dd93efa0e98205bdc2b279a5a0f
}
