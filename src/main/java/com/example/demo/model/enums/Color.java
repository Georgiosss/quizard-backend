package com.example.demo.model.enums;

public enum Color {

    TRANSPARENT(0),
    RED(1),
    GREEN(2),
    BLUE(3);

    private final int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
