/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DTO;

import java.io.Serializable;

/**
 *
 * @author hoang
 */
public class Team implements Serializable{
    private int id;
    private int player1ID;
    private int playerID2;
    private String name;
    private int owner;
    
    public Team() {
        
    }

    public Team(int id, int player1ID, int playerID2, String name, int owner) {
        this.id = id;
        this.player1ID = player1ID;
        this.playerID2 = playerID2;
        this.name = name;
        this.owner = owner;
    }

    public Team(int player1ID, int playerID2, String name, int owner) {
        this.player1ID = player1ID;
        this.playerID2 = playerID2;
        this.name = name;
        this.owner = owner;
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayer1ID() {
        return player1ID;
    }

    public void setPlayer1ID(int player1ID) {
        this.player1ID = player1ID;
    }

    public int getPlayerID2() {
        return playerID2;
    }

    public void setPlayerID2(int playerID2) {
        this.playerID2 = playerID2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
    
}
