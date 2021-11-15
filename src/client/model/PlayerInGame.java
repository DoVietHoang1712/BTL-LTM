/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.model;
/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class PlayerInGame {

    String username;

    public PlayerInGame(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return this.username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
}
