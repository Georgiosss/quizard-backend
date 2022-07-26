package com.example.demo;

import java.util.Random;

public class Utils {

    public static String generateRandomCode(int codeLength) {
        Random random = new Random();

        return random.ints('0', 'z' + 1)
                .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i >= 'a'))
                .limit(codeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
