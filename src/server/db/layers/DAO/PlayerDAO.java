/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.db.layers.DAO;

import java.sql.Connection;
import server.db.layers.DBConnector.MysqlConnector;
import server.db.layers.DTO.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
                p.setId(rs.getInt("id"));
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
    
    public Player getByUsernamee(String username) {
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "SELECT * FROM Player WHERE username = ? limit 1;";
            PreparedStatement stm = connection.prepareStatement(qry);
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("id"));
                p.setElo(rs.getInt("elo"));
                p.setUsername(rs.getString("username"));
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
                            rs.getInt("ID"),
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
                    + "elo=?,"
                    + "password=?,"
                    + "matchCount=?,"
                    + "winCount=?"
                    + " WHERE username=?";

            PreparedStatement stm = connection.prepareStatement(qry);

            stm.setInt(1, p.getElo());
            stm.setString(2, p.getPassword());
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

    public boolean signup(String username, String password) {
        boolean result = false;
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "INSERT INTO Player(Username,Password,Elo,MatchCount,WinCount) "
                    + "VALUES(?,?,0,0,0)";

            PreparedStatement stm = connection.prepareStatement(qry);
            stm.setString(1, username);
            stm.setString(2, password);

            result = stm.execute();
            return true;
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

    public String changePassword(String username, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public List<Player> listRank() {
        ArrayList<Player> result = new ArrayList<>();
        Connection connection = MysqlConnector.getConnection();
        try {
            String qry = "SELECT * FROM Player Order by Elo Desc Limit 10;";
            PreparedStatement stm = connection.prepareStatement(qry);
            ResultSet rs = stm.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Player p = new Player(
                            rs.getInt("ID"),
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
}
