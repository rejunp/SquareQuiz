package edu.calstatela.squarequiz.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GameMaster {
    String gameId;
    String leaderId;
    //Integer totalPlayers;
    String quizLink;
    String active;
    public GameMaster() {
    }

    public GameMaster(String gameId, String leaderId, String quizLink, String active) {
        this.gameId = gameId;
        this.leaderId = leaderId;
        this.quizLink = quizLink;
        this.active = active;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getQuizLink() {
        return quizLink;
    }

    public void setQuizLink(String quizLink) {
        this.quizLink = quizLink;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
