/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DTO;

/**
 *
 * @author nguye
 */
public class Player {
    int id;
    String username;
    String password;
    int elo;
    int matchCount;
    int winCount;

    public Player() {

    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        elo = 0;
        matchCount = 0;
        winCount = 0;
    }

    public Player(String username, String password, int elo, int matchCount, int winCount) {
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.matchCount = matchCount;
        this.winCount = winCount;
    }

    public Player(int id, String username, String password, int elo, int matchCount, int winCount) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.matchCount = matchCount;
        this.winCount = winCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void addScore(int toAdd) {
        this.elo += toAdd;
    }

    public Player(Player p) {
        this.username = p.username;
        this.password = p.password;
        this.elo = p.elo;
        this.matchCount = p.matchCount;
        this.winCount = p.winCount;
    }

    public int calculateTieCount() {
        return matchCount - winCount;
    }

    public float calculateWinRate() {
        if (this.matchCount == 0) {
            return 0;
        }

        return (float) (100.0 * winCount / matchCount);
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }
}
