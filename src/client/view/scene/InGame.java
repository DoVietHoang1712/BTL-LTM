/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view.scene;

import client.RunClient;
import client.model.ChatItem;
import client.model.PlayerInGame;
import client.view.helper.PlayerInRoomCustomRenderer;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import shared.constant.Avatar;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;
import shared.helper.CountDownTimer;
import shared.helper.CustumDateTimeFormatter;

 
public class InGame extends javax.swing.JFrame {

    final ImageIcon p1Icon = new ImageIcon(Avatar.ASSET_PATH + "icons8_round_24px.png");
    final ImageIcon p2Icon = new ImageIcon(Avatar.ASSET_PATH + "icons8_delete_24px_1.png");

    // https://codelearn.io/sharing/lam-game-caro-don-gian-bang-java
    final int COLUMN = 16, ROW = 16;

    DefaultListModel<PlayerInGame> listPlayersModel;
    PlayerInGame player1;
    PlayerInGame player2;
    PlayerInGame player3;
    PlayerInGame player4;
    ArrayList<PlayerInGame> winner = new ArrayList<>();
    ArrayList<PlayerInGame> loser = new ArrayList<>();
    int turn = 1;

    JButton btnOnBoard[][];
    JButton lastMove = null;

    CountDownTimer turnTimer;
    CountDownTimer matchTimer;

    /**
     * Creates new form InGame
     */
    public InGame() {
        initComponents();
        this.setLocationRelativeTo(null);

        // list user
        listPlayersModel = new DefaultListModel<>();
        lListUser.setModel(listPlayersModel);
        lListUser.setCellRenderer(new PlayerInRoomCustomRenderer());
        // https://stackoverflow.com/a/4344762
        lListUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());

                    RunClient.openScene(RunClient.SceneName.PROFILE);
                    RunClient.profileScene.loadProfileData(listPlayersModel.get(index).getUsername());
                }
            }
        });

        // board
        plBoardContainer.setLayout(new GridLayout(ROW, COLUMN));
        initBoard();

        // https://stackoverflow.com/a/1627068
        ((DefaultCaret) txaChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // close window event
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(InGame.this,
                        "B???n c?? ch???c mu???n tho??t ph??ng?", "Tho??t ph??ng?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    RunClient.socketHandler.leaveRoom();
                }
            }
        });
    }

    public void setPlayerInGame(PlayerInGame p1, PlayerInGame p2, PlayerInGame p3, PlayerInGame p4) {
        // save data
        player1 = p1;
        player2 = p2;
        player3 = p3;
        player4 = p4;          

        // player 1
        lbPlayerNameId1.setText(p1.getUsername());
//        if (p1.getAvatar().equals("")) {
//            lbAvatar1.setIcon(new ImageIcon(Avatar.PATH + Avatar.EMPTY_AVATAR));
//        } else {
//            lbAvatar1.setIcon(new ImageIcon(Avatar.PATH + p1.getAvatar()));
//        }

        // player 2
        lbPlayerNameId2.setText(p2.getUsername());
        lbPlayerNameId3.setText(p3.getUsername());
        lbPlayerNameId4.setText(p4.getUsername());
        // lbAvatar2.setIcon(new ImageIcon(Avatar.PATH + Avatar.EMPTY_AVATAR));
//        if (p2.getAvatar().equals("")) {
//            lbAvatar2.setIcon(new ImageIcon(Avatar.PATH + Avatar.EMPTY_AVATAR));
//        } else {
//            lbAvatar2.setIcon(new ImageIcon(Avatar.PATH + p2.getAvatar()));
//        }

        // reset turn
        
        lbActive1.setVisible(false);
        lbActive2.setVisible(false);
    }

    public void setListUser(ArrayList<PlayerInGame> list) {
        listPlayersModel.clear();

        for (PlayerInGame p : list) {
            listPlayersModel.addElement(p);
        }
    }

    public void setWin(String winEmail) {
        // pause timer
        matchTimer.pause();
        turnTimer.pause();

        // tie
        if (winEmail == null) {
            addChat(new ChatItem("[]", "K???T QU???", "H??A"));
            JOptionPane.showMessageDialog(this, "Tr???n ?????u k???t th??c v???i t??? s??? H??A.", "H??A", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // get myEmail
        String myEmail = RunClient.socketHandler.getLoginEmail();
        addChat(new ChatItem("[]", "K???T QU???", winEmail +" ???? th???ng"));
        JOptionPane.showMessageDialog(this, "Ch??c m???ng. "+winEmail + " ???? chi???n th???ng.", "Chi???n th???ng", JOptionPane.INFORMATION_MESSAGE);
//        if (winEmail.equals(myEmail)) {
//            // l?? email c???a m??nh th?? win
//            addChat(new ChatItem("[]", "K???T QU???", "B???n ???? th???ng"));
//            JOptionPane.showMessageDialog(this, "Ch??c m???ng. B???n ???? chi???n th???ng.", "Chi???n th???ng", JOptionPane.INFORMATION_MESSAGE);
//
//        } else if (myEmail.equals(player1.getUsername()) || myEmail.equals(player2.getUsername())) {
//            // n???u m??nh l?? 1 trong 2 ng?????i ch??i, m?? winEmail ko ph???i m??nh => thua
//            addChat(new ChatItem("[]", "K???T QU???", "B???n ???? thua"));
//            JOptionPane.showMessageDialog(this, "R???t ti???c. B???n ???? thua cu???c.", "Thua cu???c", JOptionPane.INFORMATION_MESSAGE);
//
//        } else {
//            // c??n l???i l?? viewers
//            String nameId = "";
//            if (player1.getUsername().equals(winEmail)) {
//                nameId = player1.getUsername();
//            } else {
//                nameId = player2.getUsername();
//            }
//            addChat(new ChatItem("[]", "K???T QU???", "Ng?????i ch??i " + nameId + " ???? th???ng"));
//            JOptionPane.showMessageDialog(this, "Ng?????i ch??i " + nameId + " ???? th???ng", "K???t qu???", JOptionPane.INFORMATION_MESSAGE);
//        }

        // tho??t ph??ng sau khi thua 
        // TODO sau n??y s??? cho t???o v??n m???i, hi???n t???i cho tho??t ????? tr??nh l???i
        // RunClient.socketHandler.leaveRoom();
    }

    public void startGame(int turnTimeLimit, int matchTimeLimit) {
        System.out.println(player1.getUsername() + player2.getUsername() + player3.getUsername() + player4.getUsername());

        turnTimer = new CountDownTimer(turnTimeLimit);
        turnTimer.setTimerCallBack(
                // end match callback
                null,
                // tick match callback
                (Callable) () -> {
                    pgbTurnTimer.setValue(100 * turnTimer.getCurrentTick() / turnTimer.getTimeLimit());
                    pgbTurnTimer.setString(CustumDateTimeFormatter.secondsToMinutes(turnTimer.getCurrentTick()));
                    return null;
                },
                // tick interval
                1
        );

        matchTimer = new CountDownTimer(matchTimeLimit);
        matchTimer.setTimerCallBack(
                // end match callback
                null,
                // tick match callback
                (Callable) () -> {
                    pgbMatchTimer.setValue(100 * matchTimer.getCurrentTick() / matchTimer.getTimeLimit());
                    pgbMatchTimer.setString("" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick()));
                    return null;
                },
                // tick interval
                1
        );
    }

    public void setTurnTimerTick(int value) {
        turnTimer.setCurrentTick(value);
    }

    public void setMatchTimerTick(int value) {
        matchTimer.setCurrentTick(value);
    }

    // change turn sang cho email ?????u v??o
    public void setTurn(String email) {
        if (player1.getUsername().equals(email)) {
            turnTimer.restart();
            turn = 2;
            lbActive1.setVisible(true);
            lbActive2.setVisible(false);
            
//            lbAvatar1.setBorder(javax.swing.BorderFactory.createTitledBorder("??ang ????nh.."));
//            lbAvatar2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ch???"));
        }

        if (player2.getUsername().equals(email)) {
            turnTimer.restart();
            turn = 4;
            lbActive1.setVisible(true);
            lbActive2.setVisible(false);
//            lbAvatar1.setBorder(javax.swing.BorderFactory.createTitledBorder("Ch???"));
//            lbAvatar2.setBorder(javax.swing.BorderFactory.createTitledBorder("??ang ????nh.."));
        }
        
        if (player3.getUsername().equals(email)) {
            turnTimer.restart();
            turn = 3;
            lbActive1.setVisible(false);
            lbActive2.setVisible(true);
            
        }
        
        if (player4.getUsername().equals(email)) {
            turnTimer.restart();
            turn = 1;
            lbActive1.setVisible(false);
            lbActive2.setVisible(true);
        }
    }

    // change turn sang cho ?????i th??? c???a email ?????u v??o
    public void changeTurnFrom(String email) {
        if (email.equals(player1.getUsername())) {
            setTurn(player1.getUsername());
        } else if (email.equals(player2.getUsername())) {
            setTurn(player2.getUsername());
        } else if (email.equals(player3.getUsername())) {
            setTurn(player3.getUsername());
        } else if (email.equals(player4.getUsername())) {
            setTurn(player4.getUsername());
        }
    }

    public void initBoard() {
        plBoardContainer.removeAll();
        btnOnBoard = new JButton[COLUMN + 2][ROW + 2];

        for (int row = 0; row < ROW; row++) {
            for (int column = 0; column < COLUMN; column++) {
                btnOnBoard[row][column] = createBoardButton(row, column);
                plBoardContainer.add(btnOnBoard[row][column]);
            }
        }
    }

    public void setLastMove(int row, int column) {
        lastMove = btnOnBoard[row][column];
    }

    public void addPoint(int row, int column, String email) {
        if (lastMove != null) {
            lastMove.setBackground(new Color(180, 180, 180));
        }

        lastMove = btnOnBoard[row][column];
        lastMove.setBackground(Color.yellow);
        lastMove.setActionCommand(email); // save email as state

        if (email.equals(player1.getUsername())) {
            lastMove.setIcon(p1Icon);
        } else if (email.equals(player2.getUsername())) {
            lastMove.setIcon(p1Icon);
        } else if (email.equals(player3.getUsername())) {
            lastMove.setIcon(p2Icon);
        } else if (email.equals(player4.getUsername())) {
            lastMove.setIcon(p2Icon);
        }
    }

    public void removePoint(int row, int column) {
        btnOnBoard[row][column].setIcon(null);
        btnOnBoard[row][column].setActionCommand("");
    }

    public void clickOnBoard(int row, int column) {
        String myEmail = RunClient.socketHandler.getLoginEmail();
        System.out.println("Turn: "+turn + ", player: "+myEmail);
        if (myEmail.equals(player1.getUsername()) && turn == 1) {
            RunClient.socketHandler.move(row, column);
        }
        if (myEmail.equals(player2.getUsername()) && turn == 3) {
            System.out.println("1234");
            RunClient.socketHandler.move(row, column);
        }
        if (myEmail.equals(player3.getUsername()) && turn == 2) {
            RunClient.socketHandler.move(row, column);
        }
        if (myEmail.equals(player4.getUsername()) && turn == 4) {
            RunClient.socketHandler.move(row, column);
        }
        // if (myEmail.equals(player1.getUsername()) || myEmail.equals(player2.getUsername()) || myEmail.equals(player3.getUsername()) || myEmail.equals(player4.getUsername())) {
            
        // }
    }

    public JButton createBoardButton(int row, int column) {
        JButton b = new JButton();
        b.setFocusPainted(false);
        b.setBackground(new Color(180, 180, 180));
        b.setActionCommand("");

        b.addActionListener((ActionEvent e) -> {
            clickOnBoard(row, column);

            // test
            // addPoint(row, column, "");
        });

        // https://stackoverflow.com/a/22639054
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (b.getActionCommand().equals("")) {

                    String myEmail = RunClient.socketHandler.getLoginEmail();
                    if (myEmail.equals(player1.getUsername()) && (turn == 1)) {
                        b.setIcon(p1Icon);
                        // turn = 2;

                    }

                    if (myEmail.equals(player2.getUsername()) && (turn == 3)) {
                        b.setIcon(p1Icon);
                        // turn = 4;
                    }
                    
                    if (myEmail.equals(player3.getUsername()) && (turn == 2)) {
                        b.setIcon(p2Icon);
                        // turn = 3;
                    }
                    
                    if (myEmail.equals(player4.getUsername()) && (turn == 4)) {
                        b.setIcon(p2Icon);
                        // turn = 1;
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (b.getActionCommand().equals("")) {
                    b.setIcon(null);
                }
            }
        });

        return b;
    }

    public void addChat(ChatItem c) {
        txaChat.append(c.toString() + "\n");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        plRightContainer = new javax.swing.JPanel();
        plToolContainer = new javax.swing.JPanel();
        btnNewGame = new javax.swing.JButton();
        btnUndo = new javax.swing.JButton();
        btnLeaveRoom = new javax.swing.JButton();
        plPlayerContainer = new javax.swing.JPanel();
        plPlayer = new javax.swing.JPanel();
        lbPlayerNameId1 = new javax.swing.JLabel();
        lbActive1 = new javax.swing.JLabel();
        lbVersus = new javax.swing.JLabel();
        lbPlayerNameId2 = new javax.swing.JLabel();
        lbActive2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbPlayerNameId3 = new javax.swing.JLabel();
        lbPlayerNameId4 = new javax.swing.JLabel();
        plTimer = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pgbTurnTimer = new javax.swing.JProgressBar();
        pgbMatchTimer = new javax.swing.JProgressBar();
        tpChatAndViewerContainer = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        txChatInput = new javax.swing.JTextField();
        btnSendMessage = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaChat = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lListUser = new javax.swing.JList<>();
        plBoardContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Caro Game");
        setResizable(false);

        plToolContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("Ch???c n??ng"));

        btnNewGame.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnNewGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_new_file_24px.png"))); // NOI18N
        btnNewGame.setText("V??n m???i");

        btnUndo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_undo_24px.png"))); // NOI18N
        btnUndo.setText("????nh l???i");

        btnLeaveRoom.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnLeaveRoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_exit_sign_24px.png"))); // NOI18N
        btnLeaveRoom.setText("Tho??t ph??ng");
        btnLeaveRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plToolContainerLayout = new javax.swing.GroupLayout(plToolContainer);
        plToolContainer.setLayout(plToolContainerLayout);
        plToolContainerLayout.setHorizontalGroup(
            plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plToolContainerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(plToolContainerLayout.createSequentialGroup()
                        .addComponent(btnNewGame)
                        .addGap(6, 6, 6)
                        .addComponent(btnUndo))
                    .addComponent(btnLeaveRoom))
                .addGap(42, 42, 42))
        );
        plToolContainerLayout.setVerticalGroup(
            plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plToolContainerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewGame)
                    .addComponent(btnUndo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLeaveRoom)
                .addGap(18, 18, 18))
        );

        plPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder("Ng?????i ch??i"));

        lbPlayerNameId1.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lbPlayerNameId1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPlayerNameId1.setText("Hoang");

        lbActive1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_sphere_30px.png"))); // NOI18N

        lbVersus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbVersus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_sword_48px.png"))); // NOI18N

        lbPlayerNameId2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lbPlayerNameId2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPlayerNameId2.setText("Hien");

        lbActive2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_sphere_30px.png"))); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_round_24px.png"))); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_delete_24px_1.png"))); // NOI18N

        lbPlayerNameId3.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lbPlayerNameId3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPlayerNameId3.setText("Hoang");

        lbPlayerNameId4.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lbPlayerNameId4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPlayerNameId4.setText("Hien");

        javax.swing.GroupLayout plPlayerLayout = new javax.swing.GroupLayout(plPlayer);
        plPlayer.setLayout(plPlayerLayout);
        plPlayerLayout.setHorizontalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbPlayerNameId1, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lbPlayerNameId3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbVersus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(lbActive1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbActive2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbPlayerNameId2, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lbPlayerNameId4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        plPlayerLayout.setVerticalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(lbPlayerNameId1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(lbPlayerNameId3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(71, 71, 71))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbVersus, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGap(125, 125, 125)
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbActive1)
                                    .addComponent(lbActive2)))
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(lbPlayerNameId4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbPlayerNameId2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        plTimer.setBorder(javax.swing.BorderFactory.createTitledBorder("Th???i gian"));

        jLabel4.setText("N?????c ??i");

        jLabel5.setText("Tr???n ?????u");

        pgbTurnTimer.setValue(100);
        pgbTurnTimer.setString("??ang ?????i n?????c ??i ?????u ti??n..");
        pgbTurnTimer.setStringPainted(true);

        pgbMatchTimer.setValue(100);
        pgbMatchTimer.setString("??ang ?????i n?????c ??i ?????u ti??n..");
        pgbMatchTimer.setStringPainted(true);

        javax.swing.GroupLayout plTimerLayout = new javax.swing.GroupLayout(plTimer);
        plTimer.setLayout(plTimerLayout);
        plTimerLayout.setHorizontalGroup(
            plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTimerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pgbTurnTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pgbMatchTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        plTimerLayout.setVerticalGroup(
            plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTimerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pgbTurnTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pgbMatchTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout plPlayerContainerLayout = new javax.swing.GroupLayout(plPlayerContainer);
        plPlayerContainer.setLayout(plPlayerContainerLayout);
        plPlayerContainerLayout.setHorizontalGroup(
            plPlayerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(plTimer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        plPlayerContainerLayout.setVerticalGroup(
            plPlayerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerContainerLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(plPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txChatInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txChatInputKeyPressed(evt);
            }
        });

        btnSendMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_paper_plane_24px.png"))); // NOI18N
        btnSendMessage.setText("G???i");
        btnSendMessage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSendMessageMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txChatInput, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txChatInput))
                .addContainerGap())
        );

        txaChat.setEditable(false);
        txaChat.setColumns(20);
        txaChat.setRows(5);
        jScrollPane3.setViewportView(txaChat);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tpChatAndViewerContainer.addTab("Nh???n tin", jPanel3);

        jScrollPane2.setViewportView(lListUser);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpChatAndViewerContainer.addTab("Ng?????i trong ph??ng", jPanel4);

        javax.swing.GroupLayout plRightContainerLayout = new javax.swing.GroupLayout(plRightContainer);
        plRightContainer.setLayout(plRightContainerLayout);
        plRightContainerLayout.setHorizontalGroup(
            plRightContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plPlayerContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(plToolContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tpChatAndViewerContainer)
        );
        plRightContainerLayout.setVerticalGroup(
            plRightContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plRightContainerLayout.createSequentialGroup()
                .addComponent(plToolContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plPlayerContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpChatAndViewerContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        plBoardContainer.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(plBoardContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(plRightContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plBoardContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plRightContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendMessageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSendMessageMouseClicked
        String chatMsg = txChatInput.getText();
        txChatInput.setText("");

        if (!chatMsg.equals("")) {
            RunClient.socketHandler.chatRoom(chatMsg);
        }
    }//GEN-LAST:event_btnSendMessageMouseClicked

    private void txChatInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txChatInputKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSendMessageMouseClicked(null);
        }
    }//GEN-LAST:event_txChatInputKeyPressed

    private void btnLeaveRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveRoomActionPerformed
        // https://stackoverflow.com/a/8689130
        if (JOptionPane.showConfirmDialog(this,
                "B???n c?? ch???c mu???n tho??t ph??ng?", "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            RunClient.socketHandler.leaveRoom();
        }
    }//GEN-LAST:event_btnLeaveRoomActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InGame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLeaveRoom;
    private javax.swing.JButton btnNewGame;
    private javax.swing.JButton btnSendMessage;
    private javax.swing.JButton btnUndo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<PlayerInGame> lListUser;
    private javax.swing.JLabel lbActive1;
    private javax.swing.JLabel lbActive2;
    private javax.swing.JLabel lbPlayerNameId1;
    private javax.swing.JLabel lbPlayerNameId2;
    private javax.swing.JLabel lbPlayerNameId3;
    private javax.swing.JLabel lbPlayerNameId4;
    private javax.swing.JLabel lbVersus;
    private javax.swing.JProgressBar pgbMatchTimer;
    private javax.swing.JProgressBar pgbTurnTimer;
    private javax.swing.JPanel plBoardContainer;
    private javax.swing.JPanel plPlayer;
    private javax.swing.JPanel plPlayerContainer;
    private javax.swing.JPanel plRightContainer;
    private javax.swing.JPanel plTimer;
    private javax.swing.JPanel plToolContainer;
    private javax.swing.JTabbedPane tpChatAndViewerContainer;
    private javax.swing.JTextField txChatInput;
    private javax.swing.JTextArea txaChat;
    // End of variables declaration//GEN-END:variables
}
