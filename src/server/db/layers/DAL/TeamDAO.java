/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DAL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.db.layers.DBConnector.MysqlConnector;
import server.db.layers.DTO.GameMatch;
import server.db.layers.DTO.Player;
import server.db.layers.DTO.Team;

/**
 *
 * @author hoang
 */
public class TeamDAO {
    MysqlConnector connector;

    public TeamDAO() {

    }
    
    public ArrayList<Team> list() {
        ArrayList<Team> result = new ArrayList<>();
        connector = new MysqlConnector();

        try {
            String qry = "SELECT * FROM gamematch;";
            PreparedStatement stm = connector.getConnection().prepareStatement(qry);
            ResultSet rs = connector.sqlQry(stm);

            if (rs != null) {
                while (rs.next()) {
                    Team g = new Team(
                            rs.getInt("ID"),
                            rs.getInt("playerID1"),
                            rs.getInt("playerID2"),
                            rs.getString("name"),
                            rs.getInt("owner")
                    );
                    result.add(g);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while trying to read Matchs info from database!");
        } finally {
            connector.closeConnection();
        }

        return result;
    }
    
    public boolean insert(Player player1, Player player2) {
        boolean result = false;
        connector = new MysqlConnector();
        
        try {
            String sql = "INSERT INTO Team(playerID1,playerID2,name,owner) "
                    + "VALUES(?,?,?,?)";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setInt(1, player1.getId());
            stm.setInt(2, player2.getId());
            stm.setString(3, "");
            stm.setInt(4, player1.getId());

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }
        return result;
    }
    
    public boolean delete(int id) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String qry = "DELETE FROM Team WHERE ID=?";

            PreparedStatement stm = connector.getConnection().prepareStatement(qry);
            stm.setInt(1, id);

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }
}
