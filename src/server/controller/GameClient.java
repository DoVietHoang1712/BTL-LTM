/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;
  
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import server.RunServer;
import server.db.layers.DAO.GameMatchDAO;
import server.db.layers.DAO.PlayerDAO;
import server.db.layers.DTO.GameMatch;
import server.db.layers.DTO.Player;
import server.game.caro.Caro;
import shared.constant.Code;
import shared.constant.StreamData;
import shared.helper.CustumDateTimeFormatter;
import shared.helper.Line;

 
public class GameClient implements Runnable {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    Player loginPlayer;
    GameClient cCompetitor;
    Room joinedRoom; // if == null => chua vao phong nao het

    boolean findingMatch = false;
    String acceptPairMatchStatus = "_"; // yes/no/_

    public GameClient(Socket s) throws IOException {
        this.s = s;

        // obtaining input and output streams 
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {

        String received;
        boolean running = true;

        while (!RunServer.isShutDown) {
            try {
                // receive the request from client
                received = dis.readUTF();

                // process received data
                StreamData.Type type = StreamData.getTypeFromData(received);
                System.out.println("Server received: " + type);
                switch (type) {

                    case LOGIN:
                        onReceiveLogin(received);
                        break;

                    case SIGNUP:
                        onReceiveSignup(received);
                        break;

                    case LOGOUT:
                        onReceiveLogout(received);
                        break;
                        
                    case LIST_RANK:
                        onReceiveListRank(received);
                        break;

                    case LIST_ROOM:
                        onReceiveListRoom(received);
                        break;

                    case LIST_ONLINE:
                        onReceiveListOnline(received);
                        break;
                        
                    case GET_PROFILE:
                        onReceiveGetProfile(received);
                        break;
                        
                    case MATCH_HISTORY:
                        onReceiveListHistory(received);
                        break;

                    case FIND_MATCH:
                        onReceiveFindMatchAndAccept(received);
                        break;

                    case CANCEL_FIND_MATCH:
                        onReceiveCancelFindMatch(received);
                        break;

                    case DATA_ROOM:
                        onReceiveDataRoom(received);
                        break;

                    case CHAT_ROOM:
                        onReceiveChatRoom(received);
                        break;

                    case LEAVE_ROOM:
                        onReceiveLeaveRoom(received);
                        break;

                    case CHANGE_PASSWORD:
                        onReceiveChangePassword(received);
                        break;

                    case GAME_EVENT:
                        onReceiveGameEvent(received);
                        break;
                        
                    case WATCH_ROOM:
                        onReceiveWatchRoom(received);
                        break;
                        
                    case EXIT:
                        running = false;
                }

            } catch (IOException ex) {
                // leave room if needed
                // onReceiveLeaveRoom("");
                break;
            }
        }

        try {
            // closing resources 
            this.s.close();
            this.dis.close();
            this.dos.close();
            // System.out.println("- Client disconnected: " + s);

            // remove from clientManager
            RunServer.clientManager.remove(this);

        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void onReceiveGetProfile(String receive) {
        // prepare data
        String result = "success;";
        Player p = loginPlayer;
        result += p.getUsername() + ";" + p.getElo() + ";" + p.getWinCount() +";" + (p.getMatchCount() - p.getWinCount());

        // send data
        sendData(StreamData.Type.GET_PROFILE.name() + ";" + result);
    }
    
    private void onReceiveLogin(String received) {
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // check ???? ???????c ????ng nh???p ??? n??i kh??c
//        if (RunServer.clientManager.find(username) != null) {
//            sendData(StreamData.Type.LOGIN.name() + ";false;" + Code.ACCOUNT_LOGEDIN);
//            return;
//        }

        // check login
        Player result = new PlayerDAO().login(username, password);
        System.out.println(result);
        if (result != null) {
            // set login email
            this.loginPlayer = result;
            RunServer.clientManager.add(this);
            sendData(StreamData.Type.LOGIN.name() + ";" + "true" + ";" + username);
        } else {
            // send result
            sendData(StreamData.Type.LOGIN.name() + ";" + "false" + ";" + username);
        }

    }

    private void onReceiveSignup(String received) {
        // get data from received
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // sign up
        boolean result = new PlayerDAO().signup(username, password);

        // send data
        sendData(StreamData.Type.SIGNUP.name() + ";" + result);
    }

    private void onReceiveLogout(String received) {
        // log out now
        this.loginPlayer = null;
        this.findingMatch = false;

        // TODO leave room
        // TODO broadcast to all clients
        // send status
        sendData(StreamData.Type.LOGOUT.name() + ";success");
    }
    
    private void onReceiveListRank(String received) {
        // prepare data
        String result = "success;";
        
        ArrayList<Player> players = (ArrayList<Player>) new PlayerDAO().listRank();
        for(Player p: players) {
            result += p.getUsername() + ";" + p.getElo() + ";" + p.getWinCount() +";" + (p.getMatchCount() - p.getWinCount()) + ";";
        }

        // send data
        sendData(StreamData.Type.LIST_RANK.name() + ";" + result.substring(0, result.length()-1));
    }

    // main menu
    private void onReceiveListRoom(String received) {
        // prepare data
        String result = "success;";
        ArrayList<Room> listRoom = RunServer.roomManager.getRooms();
        int roomCount = listRoom.size();

        result += roomCount + ";";

        for (Room r : listRoom) {

            result += r.getId() + ";"
                    + r.getViewers().size() + ";";
        }

        // send data
        sendData(StreamData.Type.LIST_ROOM.name() + ";" + result);
    }

    private void onReceiveListOnline(String received) {
        // prepare data
        String result = "success;";
        PlayerDAO dao = new PlayerDAO();
        for(GameClient gc: RunServer.clientManager.clients) {
            Player p = dao.getByUsernamee(gc.loginPlayer.getUsername());
            result += p.getUsername() + ";" + p.getElo() + ";" + p.getWinCount() +";" + (p.getMatchCount() - p.getWinCount()) + ";";
        }

        // send data
        sendData(StreamData.Type.LIST_ONLINE.name() + ";" + result.substring(0, result.length()-1));
    }
    
    private void onReceiveListHistory(String receive) {
        // prepare data
        String result = "success;";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        for(GameMatch gm: new GameMatchDAO().getListHistory(loginPlayer.getUsername())) {
            result += gm.getUsername1() + ";" + gm.getUsername2() + ";" 
                    + gm.getUsername3() + ";" + gm.getUsername4() + ";"
                    + gm.getWinnerID() + ";" + gm.getWinnerID2() + ";"
                    + gm.getPlayTime() + ";" + gm.getStartedTime().format(formatter)
                    + ";" + gm.getTotalMove() + ";" + gm.getId() + ";";
        }

        // send data
        sendData(StreamData.Type.MATCH_HISTORY.name() + ";" + result.substring(0, result.length()-1));
    }
    
    private void onReceiveWatchRoom(String received) {
        String[] splitted = received.split(";");
        String roomId = splitted[1];

        String status = joinRoom(roomId, true);

        sendData(StreamData.Type.WATCH_ROOM.name() + ";" + status);
    }
    
    // pair match
    private void onReceiveFindMatchAndAccept(String received) {
        // n???u ??ang trong ph??ng r???i th?? b??o l???i ngay
        if (this.joinedRoom != null) {
            sendData(StreamData.Type.FIND_MATCH.name() + ";failed;" + Code.ALREADY_INROOM + " #" + this.joinedRoom.getId());
            return;
        }

        // ki???m tra xem c?? ai ??ang t??m ph??ng kh??ng
        // GameClient cCompetitor = RunServer.clientManager.findClientFindingMatch();
        List<GameClient> list = RunServer.clientManager.findClientFinding(this.loginPlayer);
        if (list == null) {
            this.findingMatch = true;
            sendData(StreamData.Type.FIND_MATCH.name() + ";success");
        } else {
            this.findingMatch = false;
            Room newRoom = RunServer.roomManager.createRoom();
            // join room
            String thisStatus = this.joinRoom(newRoom, false);
            sendData(StreamData.Type.JOIN_ROOM.name() + ";" + thisStatus);
            this.acceptPairMatchStatus = "_";
            for (GameClient competitor : list) {
                competitor.findingMatch = false;
                String competitorStatus = competitor.joinRoom(newRoom, false);
                competitor.sendData(StreamData.Type.JOIN_ROOM.name() + ";" + competitorStatus);
                competitor.acceptPairMatchStatus = "_";
            }
            
        }
        
    }

    private void onReceiveCancelFindMatch(String received) {
        // g??? c??? ??ang t??m ph??ng
        this.findingMatch = false;

        // b??o cho client ????? t???t giao di???n ??ang t??m ph??ng
        sendData(StreamData.Type.CANCEL_FIND_MATCH.name() + ";success");
    }

    // in game
    private void onReceiveDataRoom(String received) {
        // get room id
        String[] splitted = received.split(";");
        String roomId = splitted[1];

        // check roomid is valid
        Room room = RunServer.roomManager.find(roomId);
        if (room == null) {
            sendData(StreamData.Type.DATA_ROOM.name() + ";failed;" + Code.ROOM_NOTFOUND + " #" + roomId);
            return;
        }

        // prepare data
        String data = room.getFullData();

        // send data
        sendData(StreamData.Type.DATA_ROOM.name() + ";success;" + data);
    }

    private void onReceiveChatRoom(String received) {
        String[] splitted = received.split(";");
        String chatMsg = splitted[1];

        if (joinedRoom != null) {
            String data = CustumDateTimeFormatter.getCurrentTimeFormatted() + ";"
                    + loginPlayer.getUsername() + ";"
                    + chatMsg;

            joinedRoom.broadcast(StreamData.Type.CHAT_ROOM.name() + ";" + data);
        }
    }

    private void onReceiveLeaveRoom(String received) {
        if (joinedRoom == null) {
            sendData(StreamData.Type.LEAVE_ROOM.name() + ";failed" + Code.CANT_LEAVE_ROOM);
            return;
        }

        // n???u l?? ng?????i ch??i th?? ????ng room lu??n
        if (joinedRoom.getClient1().equals(this) || joinedRoom.getClient2().equals(this)) {
            joinedRoom.close("Ng?????i ch??i " + this.loginPlayer.getUsername() + " ???? tho??t ph??ng.");
            return;
        }

        // broadcast to all clients in room
        String data = CustumDateTimeFormatter.getCurrentTimeFormatted() + ";"
                + "SERVER" + ";"
                + loginPlayer.getUsername() + " ???? tho??t";

        joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";" + data);

        // delete refernce to room
        joinedRoom.removeClient(this);
        joinedRoom = null;

        // TODO if this client is player -> close room
        // send result
        sendData(StreamData.Type.LEAVE_ROOM.name() + ";success");
    }

    private void onReceiveChangePassword(String received) {
        // get old pass, new pass from data
        String[] splitted = received.split(";");
        String oldPassword = splitted[1];
        String newPassword = splitted[2];

        // check change pass
        String result = new PlayerDAO().changePassword(loginPlayer.getUsername(), oldPassword, newPassword);

        // send result
        sendData(StreamData.Type.CHANGE_PASSWORD.name() + ";" + result);
    }

    // game event
    private void onReceiveGameEvent(String received) {
        String[] splitted = received.split(";");
        StreamData.Type gameEventType = StreamData.getType(splitted[1]);

        Caro caroGame = (Caro) joinedRoom.getGame();

        switch (gameEventType) {
            case MOVE:
                // l?????t ??i ?????u ti??n s??? b???t ?????u game
                if (!joinedRoom.isGameStarted()) {
                    joinedRoom.startGame();
                    joinedRoom.broadcast(
                            StreamData.Type.GAME_EVENT + ";"
                            + StreamData.Type.START + ";"
                            + Caro.TURN_TIME_LIMIT + ";"
                            + Caro.MATCH_TIME_LIMIT
                    );
                }

                // get row/col data
                int row = Integer.parseInt(splitted[2]);
                int column = Integer.parseInt(splitted[3]);

                // check move
                ArrayList<GameClient> team1 = joinedRoom.getTeam1();
                ArrayList<GameClient> team2 = joinedRoom.getTeam2();
                
                int team;
                if (team1.contains(this)) team = 1;
                else team = 2;
                ArrayList<GameClient> winnerTeam = new ArrayList<>();
                ArrayList<GameClient> loserTeam = new ArrayList<>();
                if (caroGame.move(row, column, loginPlayer.getUsername(), team)) {
                    // restart turn timer
                    joinedRoom.game.restartTurnTimer();

                    // broadcast to all client in room movedata
                    joinedRoom.broadcast(
                            StreamData.Type.GAME_EVENT + ";"
                            + StreamData.Type.MOVE + ";"
                            + row + ";"
                            + column + ";"
                            + loginPlayer.getUsername()
                    );

                    // check win
                    Line winPath = caroGame.CheckWin(row, column);
                    if (winPath != null) {

                        PlayerDAO dao = new PlayerDAO();
                        if (team == 1) {
                            winnerTeam = team1;
                            loserTeam = team2;
                            for (GameClient client : team1) {
                                client.loginPlayer.addScore(3);
                                client.loginPlayer.setWinCount(client.loginPlayer.getWinCount() + 1);
                                client.loginPlayer.setMatchCount(client.loginPlayer.getMatchCount() + 1);
                                dao.update(client.loginPlayer);
                            }
                            for (GameClient client : team2) {
                                client.loginPlayer.addScore(-1);
                                client.loginPlayer.setMatchCount(client.loginPlayer.getMatchCount() + 1);
                                dao.update(client.loginPlayer);
                            }
                        }
                        if (team == 2) {
                            winnerTeam = team2;
                            loserTeam = team1;
                            for (GameClient client : team2) {
                                client.loginPlayer.addScore(3);
                                client.loginPlayer.setWinCount(client.loginPlayer.getWinCount() + 1);
                                client.loginPlayer.setMatchCount(client.loginPlayer.getMatchCount() + 1);
                                dao.update(client.loginPlayer);
                            }
                            for (GameClient client : team1) {
                                client.loginPlayer.addScore(-1);
                                client.loginPlayer.setMatchCount(client.loginPlayer.getMatchCount() + 1);
                                client.loginPlayer.setWinCount(client.loginPlayer.getWinCount());
                                dao.update(client.loginPlayer);
                            }
                        }

                        // stop game timer
                        caroGame.cancelTimer();

                        // broadcast to all client in room windata
                        
                            joinedRoom.broadcast(
                                StreamData.Type.GAME_EVENT + ";"
                                + StreamData.Type.WIN + ";"
                                + winnerTeam.stream().map(client -> client.loginPlayer.getUsername()).collect(Collectors.toList()).toString()
                        );
                        
                        

                    }
                } else {
                    // do nothing
                }
                break;

            case UNDO:
            case UNDO_ACCEPT:
            case NEW_GAME:
            case NEW_GAME_ACCEPT:
            case SURRENDER:
                // stop game timer
                caroGame.cancelTimer();

                // broadcast to all client in room windata
                joinedRoom.broadcast(
                        StreamData.Type.GAME_EVENT + ";"
                        + StreamData.Type.SURRENDER + ";"
                        + loginPlayer.getUsername()
                );
                break;
        }
    }

    // send data fucntions
    public String sendData(String data) {
        try {
            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            System.err.println("Send data failed to " + this.loginPlayer.getUsername());
            return "failed;" + e.getMessage();
        }
    }

    // room handlers
    public String joinRoom(String id, boolean isWatcher) {
        Room found = RunServer.roomManager.find(id);

        // kh??ng t??m th???y ph??ng c???n join ?
        if (found == null) {
            return "failed;Kh??ng t??m th???y ph??ng " + id;
        }

        return joinRoom(found, isWatcher);
    }

    public String joinRoom(Room room, boolean isWatcher) {
        // ??ang trong ph??ng r???i ?
        if (this.joinedRoom != null) {
            return "failed;" + Code.CANNOT_JOINROOM + Code.ALREADY_INROOM + " #" + this.joinedRoom.getId();
        }

        // join v??o ph??ng thanh cong hay khong ?
        if (room.addClient(this, isWatcher)) {
            this.joinedRoom = room;

            // th??ng b??o v???i m???i ng?????i trong ph??ng
//            this.joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";"
//                    + CustumDateTimeFormatter.getCurrentTimeFormatted()
//                    + ";SERVER;"
//                    + loginPlayer.getUsername()+ " ???? v??o ph??ng."
//            );
            if (this.joinedRoom.getTeam1().size() < 2 && !this.joinedRoom.getTeam1().contains(this) && !this.joinedRoom.getTeam2().contains(this) && isWatcher == false) {
                ArrayList<GameClient> clients = this.joinedRoom.getTeam1();
                System.out.println(this.loginPlayer.getUsername() + " " +"team 1");
                clients.add(this);
                this.joinedRoom.setTeam1(clients);
            }
            if (this.joinedRoom.getTeam2().size() < 2 && !this.joinedRoom.getTeam1().contains(this) && !this.joinedRoom.getTeam2().contains(this) && isWatcher == false) {
                ArrayList<GameClient> clients = this.joinedRoom.getTeam2();
                System.out.println(this.loginPlayer.getUsername() + " " +"team 2");
                clients.add(this);
                this.joinedRoom.setTeam2(clients);
            }
            return "success;" + room.getId();
        }

        return "failed;" + Code.CANNOT_JOINROOM + " room.addClient tr??? v??? false";
    }

    // get set
    public static String getEmptyInGameData() {
        return ";;";
    }

    public String getInGameData() {
        if (loginPlayer == null) {
            return getEmptyInGameData(); // tr??? v??? r???ng
        }
        return loginPlayer.getUsername();
    }

    public boolean isFindingMatch() {
        return findingMatch;
    }

    public void setFindingMatch(boolean findingMatch) {
        this.findingMatch = findingMatch;
    }

    public Player getLoginPlayer() {
        return loginPlayer;
    }

    public void setLoginPlayer(Player loginPlayer) {
        this.loginPlayer = loginPlayer;
    }

    public GameClient getcCompetitor() {
        return cCompetitor;
    }

    public void setcCompetitor(GameClient cCompetitor) {
        this.cCompetitor = cCompetitor;
    }

    public Room getJoinedRoom() {
        return joinedRoom;
    }

    public void setJoinedRoom(Room room) {
        this.joinedRoom = room;
    }

    public String getAcceptPairMatchStatus() {
        return acceptPairMatchStatus;
    }

    public void setAcceptPairMatchStatus(String acceptPairMatchStatus) {
        this.acceptPairMatchStatus = acceptPairMatchStatus;
    }

}
