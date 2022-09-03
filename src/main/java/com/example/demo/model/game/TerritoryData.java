package com.example.demo.model.game;


import com.example.demo.model.enums.CastleType;
import com.example.demo.model.enums.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TerritoryData {

    @JsonIgnore
    private int DEFAULT_POINTS = 200;

    private Long userId;
    private Integer territoryId;
    private Color color;
    private Integer points;
    private Castle castle;

    public TerritoryData(Integer territoryId) {
        this.territoryId = territoryId;
        this.color = Color.TRANSPARENT;
        this.points = DEFAULT_POINTS;
        this.castle = new Castle();
    }

    public TerritoryData(Long userId, Integer territoryId,
                         Color color, Castle castle) {
        this.userId = userId;
        this.territoryId = territoryId;
        this.color = color;
        this.points = DEFAULT_POINTS;
        this.castle = castle;
    }
}
