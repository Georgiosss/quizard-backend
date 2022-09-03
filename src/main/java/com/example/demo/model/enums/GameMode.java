package com.example.demo.model.enums;

public enum GameMode {
    TWO_PLAYER(2),
    THREE_PLAYER(3);

    private final int value;

    GameMode(int value) {
        this.value = value;
    }

    public static GameMode fromValue(int value) {
        switch (value) {
            case 2: return TWO_PLAYER;
            default: return THREE_PLAYER;
        }
    }

    public int getValue() {
        return value;
    }
}
