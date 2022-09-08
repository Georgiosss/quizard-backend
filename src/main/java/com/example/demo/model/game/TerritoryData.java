package com.example.demo.model.game;


import com.example.demo.utils.Constants;
import com.example.demo.model.enums.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class TerritoryData {

    private Long userId;
    private Integer territoryId;
    private Color color;
    private Integer points;
    private Castle castle;

    @JsonIgnore
    private List<Long> neighbourIds;

    public TerritoryData(Integer territoryId, List<Long> neighbourIds) {
        this.territoryId = territoryId;
        this.color = Color.TRANSPARENT;
        this.points = Constants.SINGLE_TOWER_POINTS;
        this.castle = new Castle();
        this.neighbourIds = neighbourIds;
    }

    public TerritoryData(Long userId, Integer territoryId, Integer points,
                         Color color, Castle castle, List<Long> neighbourIds) {
        this.userId = userId;
        this.territoryId = territoryId;
        this.color = color;
        this.points = points;
        this.castle = castle;
        this.neighbourIds = neighbourIds;
    }
}
