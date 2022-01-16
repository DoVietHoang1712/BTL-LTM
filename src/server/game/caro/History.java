/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.game.caro;


 
public class History {

    int row;
    int column;
    String playerEmail;
    int team;

    public History(int row, int column, String playerEmail, int team) {
        this.row = row;
        this.column = column;
        this.playerEmail = playerEmail;
        this.team = team;
    }

    @Override
    public String toString() {
        return row + ";" + column + ";" + playerEmail;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public void setPlayerEmail(String playerId) {
        this.playerEmail = playerId;
    }
    
    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

}
