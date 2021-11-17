/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import server.game.caro.Caro;
import server.game.GameLogic;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import server.RunServer;
import server.db.layers.BUS.GameMatchBUS;
import server.db.layers.DTO.GameMatch;
import server.game.caro.History;
import shared.constant.StreamData;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class Room {

    String id;
    Caro gamelogic;
    GameClient client1 = null;
    GameClient client2 = null;
    GameClient client3 = null;
    GameClient client4 = null;
    ArrayList<GameClient> team1 = new ArrayList<>();;
    ArrayList<GameClient> team2 = new ArrayList<>();;
    ArrayList<GameClient> clients = new ArrayList<>();
    boolean gameStarted = false;

    public LocalDateTime startedTime;
    public ArrayList<GameClient> getTeam1() {
        return team1;
    }

    public void setTeam1(ArrayList<GameClient> team1) {
        this.team1 = team1;
    }

    public ArrayList<GameClient> getTeam2() {
        return team2;
    }

    public void setTeam2(ArrayList<GameClient> team2) {
        this.team2 = team2;
    }
    
    
    public GameClient getClient3() {
        return client3;
    }
    
    public void setClient3(GameClient client3) {
        this.client3 = client3;
    }

    public GameClient getClient4() {
        return client4;
    }

    public void setClient4(GameClient client4) {
        this.client4 = client4;
    }
    

    public Room(String id) {
        // room id
        this.id = id;

        // create game logic
        gamelogic = new Caro();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        startedTime = LocalDateTime.now();
        gameStarted = true;
        gamelogic.getTurnTimer()
                .setTimerCallBack(
                        // end turn callback
                        (Callable) () -> {
                            // TURN_TIMER_END;<winner-email>
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TIMER_END.name() + ";"
                                    + gamelogic.getLastMoveEmail()
                            );
                            return null;
                        },
                        // tick turn callback
                        (Callable) () -> {
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TICK.name() + ";"
                                    + gamelogic.getTurnTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.TURN_TIME_LIMIT / 10
                );

        gamelogic.getMatchTimer()
                .setTimerCallBack(
                        // end match callback
                        (Callable) () -> {

                            // tinh diem hoa
                            new GameMatchBUS().add(new GameMatch(
                                    client1.getLoginPlayer().getUsername(),
                                    client2.getLoginPlayer().getUsername(),
                                    "",
                                    gamelogic.getMatchTimer().getCurrentTick(),
                                    gamelogic.getHistory().size(),
                                    startedTime
                            ));

                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.MATCH_TIMER_END.name()
                            );
                            return null;
                        },
                        // tick match callback
                        (Callable) () -> {
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.MATCH_TICK.name() + ";"
                                    + gamelogic.getMatchTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.MATCH_TIME_LIMIT / 10
                );
        gamelogic.getTurnTimer()
                .setTimerCallBack(
                        // end turn callback
                        (Callable) () -> {
                            // TURN_TIMER_END;<winner-email>
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TIMER_END.name() + ";"
                                    + gamelogic.getLastMoveEmail()
                            );
                            return null;
                        },
                        // tick turn callback
                        (Callable) () -> {
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TICK.name() + ";"
                                    + gamelogic.getTurnTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.TURN_TIME_LIMIT / 10
                );
        gamelogic.getTurnTimer()
                .setTimerCallBack(
                        // end turn callback
                        (Callable) () -> {
                            // TURN_TIMER_END;<winner-email>
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TIMER_END.name() + ";"
                                    + gamelogic.getLastMoveEmail()
                            );
                            return null;
                        },
                        // tick turn callback
                        (Callable) () -> {
                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TICK.name() + ";"
                                    + gamelogic.getTurnTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.TURN_TIME_LIMIT / 10
                );
    }

    // add/remove client
    public boolean addClient(GameClient c, boolean isWatcher) {
        if (!clients.contains(c)) {
            clients.add(c);

            if (!isWatcher) {
                if (client1 == null) {
                    client1 = c;
                } else if (client2 == null) {
                    client2 = c;
                } else if (client3 == null) {
                    client3 = c;
                } else if (client4 == null) {
                    client4 = c;
                }
            }

            return true;
        }
        return false;
    }

    public boolean removeClient(GameClient c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    // broadcast messages
    public void broadcast(String msg) {
        clients.forEach((c) -> {
            c.sendData(msg);
        });
    }

    public void close(String reason) {
        // notify all client in room
        broadcast(StreamData.Type.CLOSE_ROOM.name() + ";" + reason);

        // remove reference
        clients.forEach((client) -> {
            client.setJoinedRoom(null);
        });

        // remove all clients
        clients.clear();

        // remove room
        RunServer.roomManager.remove(this);
    }

    // get room data
    public String getFullData() {
        String data = "";

        // player data
        data += getClient12InGameData() + ";";
        data += getListClientData() + ";";
        // timer
        data += getTimerData() + ";";
        // board
        data += getBoardData();

        return data;
    }

    public String getTimerData() {
        String data = "";

        data += Caro.MATCH_TIME_LIMIT + ";" + gamelogic.getMatchTimer().getCurrentTick() + ";";
        data += Caro.TURN_TIME_LIMIT + ";" + gamelogic.getTurnTimer().getCurrentTick();

        return data;
    }

    public String getBoardData() {
        ArrayList<History> history = gamelogic.getHistory();

        String data = history.size() + ";";
        for (History his : history) {
            data += his.getRow() + ";" + his.getColumn() + ";" + his.getPlayerEmail() + ";";
        }

        return data.substring(0, data.length() - 1); // bỏ dấu ; ở cuối
    }

    public String getClient12InGameData() {
        String data = "";

        data += (client1 == null ? GameClient.getEmptyInGameData() : client1.getInGameData() + ";");
        data += (client2 == null ? GameClient.getEmptyInGameData() : client2.getInGameData() + ";");
        data += (client3 == null ? GameClient.getEmptyInGameData() : client3.getInGameData() + ";");
        data += (client4 == null ? GameClient.getEmptyInGameData() : client4.getInGameData());

        return data;
    }

    public String getListClientData() {
        // kết quả trả về có dạng playerCount;player1_data;player2_data;...;playerN_data

        String data = clients.size() + ";";

        for (GameClient c : clients) {
            data += c.getInGameData() + ";";
        }

        return data.substring(0, data.length() - 1); // bỏ dấu ; ở cuối
    }

    // gets sets
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameLogic getGamelogic() {
        return gamelogic;
    }

    public void setGamelogic(Caro gamelogic) {
        this.gamelogic = gamelogic;
    }

    public GameClient getClient1() {
        return client1;
    }

    public void setClient1(GameClient client1) {
        this.client1 = client1;
    }

    public GameClient getClient2() {
        return client2;
    }

    public void setClient2(GameClient client2) {
        this.client2 = client2;
    }

    public ArrayList<GameClient> getClients() {
        return clients;
    }

    public void setClients(ArrayList<GameClient> clients) {
        this.clients = clients;
    }

}
