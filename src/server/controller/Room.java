/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.time.LocalDateTime;
import server.game.caro.Caro;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import server.RunServer;
import server.game.caro.History;
import shared.constant.StreamData;

 
public class Room {

    String id;
    Caro game;
    GameClient client1 = null;
    GameClient client2 = null;
    GameClient client3 = null;
    GameClient client4 = null;
    ArrayList<GameClient> team1 = new ArrayList<>();
    ArrayList<GameClient> team2 = new ArrayList<>();
    ArrayList<GameClient> viewers = new ArrayList<>();
    ArrayList<GameClient> clients = new ArrayList<>();
    boolean gameStarted = false;

    public LocalDateTime startedTime;
    public ArrayList<GameClient> getTeam1() {
        return team1;
    }

    public ArrayList<GameClient> getViewers() {
        return viewers;
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
        game = new Caro();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        startedTime = LocalDateTime.now();
        gameStarted = true;
        game.getTurnTimer()
                .setTimerCallBack(// end turn callback
                        (Callable) () -> {
                            // TURN_TIMER_END;<winner-email>
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TIMER_END.name() + ";"
                                    + game.getLastMoveEmail()
                            );
                            return null;
                        },
                        // tick turn callback
                        (Callable) () -> {
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TICK.name() + ";"
                                    + game.getTurnTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.TURN_TIME_LIMIT / 10
                );

        game.getMatchTimer()
                .setTimerCallBack(// end match callback
                        (Callable) () -> {

                            broadcast(
                                    StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.MATCH_TIMER_END.name()
                            );
                            return null;
                        },
                        // tick match callback
                        (Callable) () -> {
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.MATCH_TICK.name() + ";"
                                    + game.getMatchTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.MATCH_TIME_LIMIT / 10
                );
        game.getTurnTimer()
                .setTimerCallBack(// end turn callback
                        (Callable) () -> {
                            // TURN_TIMER_END;<winner-email>
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TIMER_END.name() + ";"
                                    + game.getLastMoveEmail()
                            );
                            return null;
                        },
                        // tick turn callback
                        (Callable) () -> {
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TICK.name() + ";"
                                    + game.getTurnTimer().getCurrentTick()
                            );
                            return null;
                        },
                        // tick interval
                        Caro.TURN_TIME_LIMIT / 10
                );
        game.getTurnTimer()
                .setTimerCallBack(// end turn callback
                        (Callable) () -> {
                            // TURN_TIMER_END;<winner-email>
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TIMER_END.name() + ";"
                                    + game.getLastMoveEmail()
                            );
                            return null;
                        },
                        // tick turn callback
                        (Callable) () -> {
                            broadcast(StreamData.Type.GAME_EVENT + ";"
                                    + StreamData.Type.TURN_TICK.name() + ";"
                                    + game.getTurnTimer().getCurrentTick()
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

        data += Caro.MATCH_TIME_LIMIT + ";" + game.getMatchTimer().getCurrentTick() + ";";
        data += Caro.TURN_TIME_LIMIT + ";" + game.getTurnTimer().getCurrentTick();

        return data;
    }

    public String getBoardData() {
        ArrayList<History> history = game.getHistory();

        String data = history.size() + ";";
        for (History his : history) {
            data += his.getRow() + ";" + his.getColumn() + ";" + his.getPlayerEmail() + ";";
        }

        return data.substring(0, data.length() - 1); // b??? d???u ; ??? cu???i
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
        // k???t qu??? tr??? v??? c?? d???ng playerCount;player1_data;player2_data;...;playerN_data

        String data = clients.size() + ";";

        for (GameClient c : clients) {
            data += c.getInGameData() + ";";
        }

        return data.substring(0, data.length() - 1); // b??? d???u ; ??? cu???i
    }

    // gets sets
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Caro getGame() {
        return game;
    }

    public void setGamelogic(Caro gamelogic) {
        this.game = gamelogic;
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
