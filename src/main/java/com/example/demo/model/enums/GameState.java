package com.example.demo.model.enums;

public enum GameState {

    LOBBY(0),
    WAITING_FOR_RESPONSE_INITIAL(1),
    CHOOSE_TERRITORY_INITIAL(2),
    WAITING_FOR_RESPONSE_MAIN(3),
    CHOOSE_TERRITORY_MAIN(4),
    FINISHED(450);

    private final int value;

    GameState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
