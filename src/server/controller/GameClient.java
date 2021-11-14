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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.RunServer;
import server.db.layers.BUS.GameMatchBUS;
import server.db.layers.DAL.PlayerDAO;
import server.db.layers.DTO.GameMatch;
import server.db.layers.DTO.Player;
import server.game.caro.Caro;
import shared.constant.Code;
import shared.constant.StreamData;
import shared.helper.CustumDateTimeFormatter;
import shared.helper.Line;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
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

                    case LIST_ROOM:
                        onReceiveListRoom(received);
                        break;

                    case LIST_ONLINE:
                        onReceiveListOnline(received);
                        break;

                    case CREATE_ROOM:
                        onReceiveCreateRoom(received);
                        break;

                    case JOIN_ROOM:
                        onReceiveJoinRoom(received);
                        break;

                    case WATCH_ROOM:
                        onReceiveWatchRoom(received);
                        break;

                    case FIND_MATCH:
                        onReceiveFindMatchAndAccept(received);
                        break;

                    case CANCEL_FIND_MATCH:
                        onReceiveCancelFindMatch(received);
                        break;

                    case REQUEST_PAIR_MATCH:
                        onReceiveRequestPairMatch(received);
                        break;

                    case RESULT_PAIR_MATCH:
                        // type này có 1 chiều server->client
                        // gửi khi ghép cặp bị đối thủ từ chối
                        // nếu ghép cặp được đồng ý thì server gửi type join-room luôn chứ ko cần gửi type này
                        // client không gửi type này cho server
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
                    case INVITE:
                        onReceiveInvite(received);
                        break;
                    case ACCEPTED:
                        onReceiveAccepted(received);
                        break;
                    case REJECTED:
                        onReceiveRejected(received);
                        break;
                    case EXIT:
                        running = false;
                }

            } catch (IOException ex) {
                // leave room if needed
                onReceiveLeaveRoom("");
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
    
    private void onReceiveInvite(String received) {
        
    }
    
    private void onReceiveRejected(String received) {
        
    }
    
    private void onReceiveAccepted(String received) {
        
    }
    
    private void onReceiveLogin(String received) {
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // check đã được đăng nhập ở nơi khác
        if (RunServer.clientManager.find(username) != null) {
            sendData(StreamData.Type.LOGIN.name() + ";false;" + Code.ACCOUNT_LOGEDIN);
            return;
        }

        // check login
        Player result = new PlayerDAO().login(username, password);
        System.out.println(result);
        if (result != null) {
            // set login email
            this.loginPlayer = result;
            RunServer.clientManager.add(this);
        }

        // send result
        sendData(StreamData.Type.LOGIN.name() + ";" + "true" + ";" + username);
    }

    private void onReceiveSignup(String received) {
        // get data from received
        String[] splitted = received.split(";");
        String email = splitted[1];
        String password = splitted[2];
        String avatar = splitted[3];
        String name = splitted[4];
        String gender = splitted[5];
        int yearOfBirth = Integer.parseInt(splitted[6]);

        // sign up
        String result = new PlayerDAO().signup(email, password);

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

    // main menu
    private void onReceiveListRoom(String received) {
        // prepare data
        String result = "success;";
        ArrayList<Room> listRoom = RunServer.roomManager.getRooms();
        int roomCount = listRoom.size();

        result += roomCount + ";";

        for (Room r : listRoom) {
            String pairData
                    = ((r.getClient1() != null) ? r.getClient1().getLoginPlayer().getUsername(): "_")
                    + " VS "
                    + ((r.getClient2() != null) ? r.getClient2().getLoginPlayer().getUsername() : "_");

            result += r.getId() + ";"
                    + pairData + ";"
                    + r.clients.size() + ";";
        }

        // send data
        sendData(StreamData.Type.LIST_ROOM.name() + ";" + result);
    }

    private void onReceiveListOnline(String received) {

    }

    private void onReceiveCreateRoom(String received) {

    }

    private void onReceiveJoinRoom(String received) {

    }

    private void onReceiveWatchRoom(String received) {
        String[] splitted = received.split(";");
        String roomId = splitted[1];

        String status = joinRoom(roomId, true);

        sendData(StreamData.Type.WATCH_ROOM.name() + ";" + status);
    }

    // pair match
    private void onReceiveFindMatchAndAccept(String received) {
        // nếu đang trong phòng rồi thì báo lỗi ngay
        if (this.joinedRoom != null) {
            sendData(StreamData.Type.FIND_MATCH.name() + ";failed;" + Code.ALREADY_INROOM + " #" + this.joinedRoom.getId());
            return;
        }

        // kiểm tra xem có ai đang tìm phòng không
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
        
//        if (cCompetitor == null) {
//            // đặt cờ là đang tìm phòng
//            this.findingMatch = true;
//
//            // trả về success để client hiển thị giao diện Đang tìm phòng
//            sendData(StreamData.Type.FIND_MATCH.name() + ";success");
//
//        } else {
//            // nếu có người cũng đang tìm trận thì hỏi ghép cặp pairMatch
//            // trong lúc hỏi thì phải tắt tìm trận bên đối thủ đi (để nếu client khác tìm trận thì ko bị ghép đè)
//            cCompetitor.findingMatch = false;
//            this.findingMatch = false;
//
//            // lưu email đối thủ để dùng khi server nhận được result-pair-match
//            this.cCompetitor = cCompetitor;
//            cCompetitor.cCompetitor = this;
//
//            // trả thông tin đối thủ về cho 2 clients
//            this.sendData(StreamData.Type.REQUEST_PAIR_MATCH.name() + ";" + cCompetitor.loginPlayer.getUsername());
//            cCompetitor.sendData(StreamData.Type.REQUEST_PAIR_MATCH.name() + ";" + this.loginPlayer.getUsername());
//        }
    }

    private void onReceiveCancelFindMatch(String received) {
        // gỡ cờ đang tìm phòng
        this.findingMatch = false;

        // báo cho client để tắt giao diện đang tìm phòng
        sendData(StreamData.Type.CANCEL_FIND_MATCH.name() + ";success");
    }

    private void onReceiveRequestPairMatch(String received) {
        String[] splitted = received.split(";");
        String requestResult = splitted[1];

        // save accept pair status
        this.acceptPairMatchStatus = requestResult;

        // get competitor
        if (cCompetitor == null) {
            sendData(StreamData.Type.RESULT_PAIR_MATCH.name() + ";failed;" + Code.COMPETITOR_LEAVE);
            return;
        }

        // if once say no
        if (requestResult.equals("no")) {
            // TODO tru diem
            this.loginPlayer.setElo(this.loginPlayer.getElo()- 1);
            new PlayerDAO().update(this.loginPlayer);

            // send data
            this.sendData(StreamData.Type.RESULT_PAIR_MATCH.name() + ";failed;" + Code.YOU_CHOOSE_NO);
            cCompetitor.sendData(StreamData.Type.RESULT_PAIR_MATCH.name() + ";failed;" + Code.COMPETITOR_CHOOSE_NO);

            // reset acceptPairMatchStatus
            this.acceptPairMatchStatus = "_";
            cCompetitor.acceptPairMatchStatus = "_";
        }

        // if both say yes
        if (requestResult.equals("yes") && cCompetitor.acceptPairMatchStatus.equals("yes")) {
            // send success pair match
            this.sendData(StreamData.Type.RESULT_PAIR_MATCH.name() + ";success");
            cCompetitor.sendData(StreamData.Type.RESULT_PAIR_MATCH.name() + ";success");

            // create new room
            Room newRoom = RunServer.roomManager.createRoom();

            // join room
            String thisStatus = this.joinRoom(newRoom, false);
            String competitorStatus = cCompetitor.joinRoom(newRoom, false);

            // send join room status to client
            sendData(StreamData.Type.JOIN_ROOM.name() + ";" + thisStatus);
            cCompetitor.sendData(StreamData.Type.JOIN_ROOM.name() + ";" + competitorStatus);

            // TODO update list room to all client
            // reset acceptPairMatchStatus
            this.acceptPairMatchStatus = "_";
            cCompetitor.acceptPairMatchStatus = "_";
        }
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

        // nếu là người chơi thì đóng room luôn
        if (joinedRoom.getClient1().equals(this) || joinedRoom.getClient2().equals(this)) {
            joinedRoom.close("Người chơi " + this.loginPlayer.getUsername() + " đã thoát phòng.");
            return;
        }

        // broadcast to all clients in room
        String data = CustumDateTimeFormatter.getCurrentTimeFormatted() + ";"
                + "SERVER" + ";"
                + loginPlayer.getUsername() + " đã thoát";

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

        Caro caroGame = (Caro) joinedRoom.getGamelogic();

        switch (gameEventType) {
            case MOVE:
                // lượt đi đầu tiên sẽ bắt đầu game
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
                    joinedRoom.gamelogic.restartTurnTimer();

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
                                dao.update(client.loginPlayer);
                            }
                            for (GameClient client : team2) {
                                client.loginPlayer.addScore(-1);
                                dao.update(client.loginPlayer);
                            }
                        }
                        if (team == 2) {
                            winnerTeam = team2;
                            loserTeam = team1;
                            for (GameClient client : team2) {
                                client.loginPlayer.addScore(3);
                                client.loginPlayer.setWinCount(client.loginPlayer.getWinCount() + 1);
                                dao.update(client.loginPlayer);
                            }
                            for (GameClient client : team1) {
                                client.loginPlayer.addScore(-1);
                                dao.update(client.loginPlayer);
                            }
                        }
                        // TODO luu game match
                        new GameMatchBUS().add(new GameMatch(
                                winnerTeam.get(0).loginPlayer.getUsername(),
                                winnerTeam.get(1).loginPlayer.getUsername(),
                                loserTeam.get(0).loginPlayer.getUsername(),
                                loserTeam.get(1).loginPlayer.getUsername(),
                                winnerTeam.get(0).loginPlayer.getUsername(),
                                winnerTeam.get(1).loginPlayer.getUsername(),
                                Caro.MATCH_TIME_LIMIT - ((Caro) joinedRoom.getGamelogic()).getMatchTimer().getCurrentTick(),
                                ((Caro) joinedRoom.getGamelogic()).getHistory().size(),
                                joinedRoom.startedTime
                        ));

                        // stop game timer
                        caroGame.cancelTimer();

                        // broadcast to all client in room windata
                        joinedRoom.broadcast(
                                StreamData.Type.GAME_EVENT + ";"
                                + StreamData.Type.WIN + ";"
                                + loginPlayer.getUsername()
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

        // không tìm thấy phòng cần join ?
        if (found == null) {
            return "failed;Không tìm thấy phòng " + id;
        }

        return joinRoom(found, isWatcher);
    }

    public String joinRoom(Room room, boolean isWatcher) {
        // đang trong phòng rồi ?
        if (this.joinedRoom != null) {
            return "failed;" + Code.CANNOT_JOINROOM + Code.ALREADY_INROOM + " #" + this.joinedRoom.getId();
        }

        // join vào phòng thanh cong hay khong ?
        if (room.addClient(this, isWatcher)) {
            this.joinedRoom = room;

            // thông báo với mọi người trong phòng
            this.joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";"
                    + CustumDateTimeFormatter.getCurrentTimeFormatted()
                    + ";SERVER;"
                    + loginPlayer.getUsername()+ " đã vào phòng."
            );
            if (this.joinedRoom.getTeam1().size() < 2 && !this.joinedRoom.getTeam1().contains(this) && !this.joinedRoom.getTeam2().contains(this)) {
                ArrayList<GameClient> clients = this.joinedRoom.getTeam1();
                clients.add(this);
                this.joinedRoom.setTeam1(clients);
            }
            if (this.joinedRoom.getTeam2().size() < 2 && !this.joinedRoom.getTeam1().contains(this) && !this.joinedRoom.getTeam2().contains(this)) {
                ArrayList<GameClient> clients = this.joinedRoom.getTeam2();
                clients.add(this);
                this.joinedRoom.setTeam2(clients);
            }
            return "success;" + room.getId();
        }

        return "failed;" + Code.CANNOT_JOINROOM + " room.addClient trả về false";
    }

    // get set
    public static String getEmptyInGameData() {
        return ";;";
    }

    public String getInGameData() {
        if (loginPlayer == null) {
            return getEmptyInGameData(); // trả về rỗng
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
