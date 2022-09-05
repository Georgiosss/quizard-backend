package com.example.demo.model.game;

import com.example.demo.model.enums.Color;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
=======
>>>>>>> 214cb7997eda7dd93efa0e98205bdc2b279a5a0f
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Player {
    private Long userId;
    private String fullName;
    private Color color;
    private Integer score;
    private Boolean active;
    private Boolean territoryToChoose;
}