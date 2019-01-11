package edu.calstatela.squarequiz.utils;

import java.util.Comparator;
import edu.calstatela.squarequiz.models.QuestionAnswered;

public class ResultSortUtil implements Comparator<QuestionAnswered> {
    public int compare(QuestionAnswered a, QuestionAnswered b) {
        if (a.getTotalScore().equals(b.getTotalScore())) {
            return b.getTotalTime().intValue() - a.getTotalTime().intValue();
        }
        return a.getTotalScore().compareTo(b.getTotalScore());
    }
}
