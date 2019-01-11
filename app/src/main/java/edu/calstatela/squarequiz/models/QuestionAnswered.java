package edu.calstatela.squarequiz.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class QuestionAnswered {
    String gameId;
    String playerUid;
    Integer totalScore;
    Long totalTime;
    Integer correctAnswerCnt;
    Map<String, Map<String, String>> answers = new HashMap<>();


    public QuestionAnswered() {
    }

    public QuestionAnswered(String gameId, String playerUid, Integer totalScore, Long totalTime, Integer correctAnswerCnt, Map<String, Map<String, String>> answers) {
        this.gameId = gameId;
        this.playerUid = playerUid;
        this.totalScore = totalScore;
        this.totalTime = totalTime;
        this.correctAnswerCnt = correctAnswerCnt;
        this.answers = answers;
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

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getCorrectAnswerCnt() {
        return correctAnswerCnt;
    }

    public void setCorrectAnswerCnt(Integer correctAnswerCnt) {
        this.correctAnswerCnt = correctAnswerCnt;
    }

    public Map<String, Map<String, String>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Map<String, String>> answers) {
        this.answers = answers;
    }
}
