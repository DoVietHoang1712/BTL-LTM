/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DAL;

import java.sql.Connection;
import server.db.layers.DBConnector.MysqlConnector;
import server.db.layers.DTO.GameMatch;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nguye
 */
public class GameMatchDAO {

    MysqlConnector connector;

    public GameMatchDAO() {

    }

    public ArrayList readDB() {
        ArrayList<GameMatch> result = new ArrayList<>();
        connector = new MysqlConnector();

        try {
            String qry = "SELECT * FROM gamematch;";
            PreparedStatement stm = connector.getConnection().prepareStatement(qry);
            ResultSet rs = connector.sqlQry(stm);

            if (rs != null) {
                while (rs.next()) {
                    GameMatch g = new GameMatch(
                            rs.getInt("ID"),
                            rs.getString("username1"),
                            rs.getString("username2"),
                            rs.getString("username3"),
                            rs.getString("username4"),
                            rs.getString("winnerID"),
                            rs.getString("winnerID2"),
                            rs.getInt("playTime"),
                            rs.getInt("totalMove"),
                            LocalDateTime.parse(rs.getString("startedTime"))
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
    
    public ArrayList<GameMatch> getListHistory(String username) {
        ArrayList<GameMatch> result = new ArrayList<>();
        Connection connection = MysqlConnector.getConnection();

        try {
            String qry = "SELECT * FROM gamematch where username1 = ? or username2 = ? or username3 = ? or username4 = ? order by id desc;";
            PreparedStatement stm = connection.prepareStatement(qry);
            stm.setString(1, username);
            stm.setString(2, username);
            stm.setString(3, username);
            stm.setString(4, username);
            ResultSet rs = stm.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    GameMatch g = new GameMatch(
                            rs.getInt("ID"),
                            rs.getString("username1"),
                            rs.getString("username2"),
                            rs.getString("username3"),
                            rs.getString("username4"),
                            rs.getString("winnerID"),
                            rs.getString("winnerID2"),
                            rs.getInt("playTime"),
                            rs.getInt("totalMove"),
                            LocalDateTime.parse(rs.getString("startedTime"))
                    );
                    result.add(g);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while trying to read Matchs info from database!");
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(GameMatchDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    public boolean add(GameMatch m) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String sql = "INSERT INTO gamematch(username1,username2,username3,username4,winnerID,winnerID2,playTime,totalMove,startedTime) "
                    + "VALUE(?,?,?,?,?,?,?,?,?)";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setString(1, m.getUsername1());
            stm.setString(2, m.getUsername2());
            stm.setString(3, m.getUsername3());
            stm.setString(4, m.getUsername4());
            stm.setString(5, m.getWinnerID());
            stm.setString(6, m.getWinnerID2());
            stm.setInt(7, m.getPlayTime());
            stm.setInt(8, m.getTotalMove());
            stm.setString(9, m.getStartedTime().toString());

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(GameMatchDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public boolean update(GameMatch m) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String sql = "UPDATE GameMatch SET "
                    + "username1=?,"
                    + "username2=?,"
                    + "username3=?,"
                    + "username4=?,"
                    + "winnerID=?,"
                    + "winnerID2=?,"
                    + "playTime=?,"
                    + "totalMove=?,"
                    + "startedTime=?"
                    + " WHERE id=?";

            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setString(1, m.getUsername1());
            stm.setString(2, m.getUsername2());
            stm.setString(3, m.getUsername3());
            stm.setString(4, m.getUsername4());
            stm.setString(5, m.getWinnerID());
            stm.setString(6, m.getWinnerID2());
            stm.setInt(7, m.getPlayTime());
            stm.setInt(8, m.getTotalMove());
            stm.setString(9, m.getStartedTime().toString());
            stm.setInt(10, m.getId());

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(GameMatchDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public boolean delete(int id) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String qry = "DELETE FROM GameMatch WHERE ID=?";

            PreparedStatement stm = connector.getConnection().prepareStatement(qry);
            stm.setInt(1, id);

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(GameMatchDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }

}
