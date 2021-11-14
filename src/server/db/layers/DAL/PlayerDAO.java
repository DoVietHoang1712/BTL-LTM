/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DAL;

import java.sql.Connection;
import server.db.layers.DBConnector.MysqlConnector;
import server.db.layers.DTO.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nguye
 */
public class PlayerDAO {
    public Player login(String username, String password) {
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "SELECT * FROM Player WHERE username = ? and password = ? limit 1;";
            PreparedStatement stm = connection.prepareStatement(qry);
            stm.setString(1, username);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                Player p = new Player();
                p.setElo(rs.getInt("elo"));
                p.setUsername(rs.getString("username"));
                p.setPassword(rs.getString("password"));
                p.setMatchCount(rs.getInt("matchCount"));
                p.setWinCount(rs.getInt("winCount"));
                
                return p;
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public ArrayList readDB() {
        ArrayList<Player> result = new ArrayList<>();
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "SELECT * FROM Player;";
            PreparedStatement stm = connection.prepareStatement(qry);
            ResultSet rs = stm.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Player p = new Player(
                            rs.getString("Username"),
                            rs.getString("Password"),
                            rs.getInt("Elo"),
                            rs.getInt("MatchCount"),
                            rs.getInt("WinCount")
                    );
                    result.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while trying to read Players info from database!");
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    public boolean add(Player p) {
        boolean result = false;
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "INSERT INTO Player(Username,Password,Elo,MatchCount,WinCount) "
                    + "VALUES(?,?,?,?,?)";

            PreparedStatement stm = connection.prepareStatement(qry);
            stm.setString(1, p.getUsername());
            stm.setString(2, p.getPassword());
            stm.setInt(3, p.getElo());
            stm.setInt(4, p.getMatchCount());
            stm.setInt(5, p.getWinCount());

            result = stm.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    public boolean update(Player p) {
        boolean result = false;
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "UPDATE Player SET "
                    + "Password=?,"
                    + "Elo=?,"
                    + "MatchCount=?,"
                    + "WinCount=?,"
                    + " WHERE Username=?";

            PreparedStatement stm = connection.prepareStatement(qry);

            stm.setString(1, p.getPassword());
            stm.setInt(2, p.getElo());
            stm.setInt(3, p.getMatchCount());
            stm.setInt(4, p.getWinCount());
            stm.setString(5, p.getUsername());

            result = stm.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    public String signup(String email, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String changePassword(String username, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
