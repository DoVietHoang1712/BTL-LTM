/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DTO;

import java.time.LocalDateTime;

/**
 *
 * @author nguye
 */
public class GameMatch {
    int id;
    String username1;
    String username2;
    String username3;
    String username4;
    String winnerID;
    String winnerID2;
    int playTime;
    int totalMove;
    LocalDateTime startedTime;
    String chat = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(String winnerID) {
        this.winnerID = winnerID;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getTotalMove() {
        return totalMove;
    }

    public void setTotalMove(int totalMove) {
        this.totalMove = totalMove;
    }

    public LocalDateTime getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(LocalDateTime startedTime) {
        this.startedTime = startedTime;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public GameMatch(int id, String playerID1, String playerID2, String winnerID, int playTime, int totalMove, LocalDateTime startedTime, String chat) {
        this.id = id;
        this.username1 = playerID1;
        this.username2 = playerID2;
        this.winnerID = winnerID;
        this.playTime = playTime;
        this.totalMove = totalMove;
        this.startedTime = startedTime;
        this.chat = chat;
    }

    public GameMatch(String playerID1, String playerID2, String winnerID, int playTime, int totalMove, LocalDateTime startedTime) {
        this.username1 = playerID1;
        this.username2 = playerID2;
        this.winnerID = winnerID;
        this.playTime = playTime;
        this.totalMove = totalMove;
        this.startedTime = startedTime;
    }

    public GameMatch(GameMatch g) {
        this.id = g.id;
        this.username1 = g.username1;
        this.username2 = g.username2;
        this.winnerID = g.winnerID;
        this.playTime = g.playTime;
        this.totalMove = g.totalMove;
        this.startedTime = g.startedTime;
        this.chat = g.chat;
    }

    public GameMatch(String username1, String username2, String username3, String username4, String winnerID, String winnerID2, int playTime, int totalMove, LocalDateTime startedTime) {
        this.username1 = username1;
        this.username2 = username2;
        this.username3 = username3;
        this.username4 = username4;
        this.winnerID = winnerID;
        this.winnerID2 = winnerID2;
        this.playTime = playTime;
        this.totalMove = totalMove;
        this.startedTime = startedTime;
    }
    
}
