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

<<<<<<< HEAD
    public static Color fromValue(int value) {
        switch (value) {
            case 1: return RED;
            case 2: return GREEN;
            case 3: return BLUE;
            default: return TRANSPARENT;
        }
    }


    public int getValue() {
        return value;
    }

=======
    public int getValue() {
        return value;
    }
>>>>>>> 214cb7997eda7dd93efa0e98205bdc2b279a5a0f
}
