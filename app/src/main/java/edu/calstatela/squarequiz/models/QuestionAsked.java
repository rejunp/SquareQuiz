package edu.calstatela.squarequiz.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class QuestionAsked {
    String gameId;
    String playerUid;
    List<String> questionId = new ArrayList<>();

    public QuestionAsked() {
    }

    public QuestionAsked(String gameId, String playerUid, List<String> questionId) {
        this.gameId = gameId;
        this.playerUid = playerUid;
        this.questionId = questionId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(String playerUid) {
        this.playerUid = playerUid;
    }

    public List<String> getQuestionId() {
        return questionId;
    }

    public void setQuestionId(List<String> questionId) {
        this.questionId = questionId;
    }
}
