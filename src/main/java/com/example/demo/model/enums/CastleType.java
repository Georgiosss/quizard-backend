package com.example.demo.model.enums;

public enum CastleType {

    NON(0),
    SINGLE(1),
    TRIPLE(3);

    private final int value;

    CastleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
