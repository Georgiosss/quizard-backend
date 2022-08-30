package com.example.demo.model.enums;

public enum QuestionType {

    SINGLE(0),
    MULTIPLE(1);

    private final int value;

    QuestionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
