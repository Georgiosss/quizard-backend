package com.example.demo.model.enums;

public enum BattleOutcome {
    ATTACKER_WINS(0),
    DEFENDER_WINS(1),
    BOTH_CORRECT(2),
    BOTH_INCORRECT(3);

    private final int value;

    BattleOutcome(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
