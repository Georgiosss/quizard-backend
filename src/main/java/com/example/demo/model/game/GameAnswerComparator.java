package com.example.demo.model.game;

import java.util.Comparator;
import java.util.Objects;

public class GameAnswerComparator implements Comparator<GameAnswer> {
    @Override
    public int compare(GameAnswer g1, GameAnswer g2) {
        boolean firstCorrect = Objects.equals(g1.getAnswer(), g1.getCorrectAnswer());
        boolean secondCorrect = Objects.equals(g1.getAnswer(), g2.getCorrectAnswer());

        if (firstCorrect && secondCorrect) {
            return g1.getTimeTaken().compareTo(g2.getTimeTaken());
        } else if (firstCorrect) {
            return -1;
        } else if (secondCorrect) {
            return 1;
        } else {
            if (g1.getAnswerType() == 0) {
               int diff1 = Math.abs(g1.getAnswer() - g1.getCorrectAnswer());
               int diff2 = Math.abs(g2.getAnswer() - g2.getCorrectAnswer());

               if (diff1 < diff2) return -1;
               else if (diff1 > diff2) return 1;
               else {
                   return g1.getTimeTaken().compareTo(g2.getTimeTaken());
               }
            } else {
                return 0;
            }
        }
    }

}
