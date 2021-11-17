/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view.scene;

import client.RunClient;
import client.view.helper.LookAndFeel;
import java.util.Vector;
import java.util.concurrent.Callable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import shared.helper.CountDownTimer;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class MainMenu extends javax.swing.JFrame {

    public enum State {
        DEFAULT,
        FINDING_MATCH,
        WAITING_ACCEPT,
        WAITING_COMPETITOR_ACCEPT
    };

    CountDownTimer acceptPairMatchTimer;
    CountDownTimer waitingPairTimer;
    final int acceptWaitingTime = 15;

    boolean pairAcceptChoosed = false;

    /**
     * Creates new form MainMenuF
     */
    public MainMenu() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Caro Game - " + RunClient.socketHandler.getLoginEmail());

        // default to hidden
        setDisplayState(State.DEFAULT);
    }
    
    public void setUp() {
        RunClient.socketHandler.listRank();
        RunClient.socketHandler.listOnline();
        RunClient.socketHandler.getProfile();
        RunClient.socketHandler.listHistory();
    }
    
    public void setProfile(String username, String elo, String wins, String lose) {
        txtUsername.setText(username);
        txtElo.setText("Elo: " + elo);
        txtWins.setText("Wins: "+ wins);
        txtLose.setText("Loses: "+lose);
    }
    
    public void setListRank(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {

        } else if (status.equals("success")) {
            // https://niithanoi.edu.vn/huong-dan-thao-tac-voi-jtable-lap-trinh-java-swing.html
            Vector vheader = new Vector();
            vheader.add("Username");
            vheader.add("Elo");
            vheader.add("Wins");
            vheader.add("Lose");

            Vector vdata = new Vector();

            // i += 3: 3 là số cột trong bảng
            // i = 3; i < roomCount + 3: dữ liệu phòng bắt đầu từ index 3 trong mảng splitted
            for (int i = 2; i < splitted.length - 3; i += 4) {

                String username = splitted[i];
                String elo = splitted[i + 1];
                String wins = splitted[i + 2];
                String lose = splitted[i + 3];

                Vector vrow = new Vector();
                vrow.add(username);
                vrow.add(elo);
                vrow.add(wins);
                vrow.add(lose);

                vdata.add(vrow);
            }
            tableRank.setModel(new DefaultTableModel(vdata, vheader));
        }
    }
    
    public void setListOnline(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {

        } else if (status.equals("success")) {
            // https://niithanoi.edu.vn/huong-dan-thao-tac-voi-jtable-lap-trinh-java-swing.html
            Vector vheader = new Vector();
            vheader.add("Username");
            vheader.add("Elo");
            vheader.add("Wins");
            vheader.add("Lose");

            Vector vdata = new Vector();

            // i += 3: 3 là số cột trong bảng
            // i = 3; i < roomCount + 3: dữ liệu phòng bắt đầu từ index 3 trong mảng splitted
            for (int i = 2; i < splitted.length - 3; i += 4) {

                String username = splitted[i];
                String elo = splitted[i + 1];
                String wins = splitted[i + 2];
                String lose = splitted[i + 3];

                Vector vrow = new Vector();
                vrow.add(username);
                vrow.add(elo);
                vrow.add(wins);
                vrow.add(lose);

                vdata.add(vrow);
            }
            tableOnline.setModel(new DefaultTableModel(vdata, vheader));
        }
    }
    
    public void setListHistory(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {

        } else if (status.equals("success")) {
            // https://niithanoi.edu.vn/huong-dan-thao-tac-voi-jtable-lap-trinh-java-swing.html
            Vector vheader = new Vector();
            vheader.add("ID");
            vheader.add("Play time");
            vheader.add("Status");
            vheader.add("Total Move");
            vheader.add("Time");

            Vector vdata = new Vector();

            // i += 3: 3 là số cột trong bảng
            // i = 3; i < roomCount + 3: dữ liệu phòng bắt đầu từ index 3 trong mảng splitted
            for (int i = 2; i < splitted.length - 9; i += 10) {

                boolean nestedStatus = splitted[4].equals(RunClient.socketHandler.getLoginUsername()) 
                        || splitted[5].equals(RunClient.socketHandler.getLoginUsername());
                String ID = (splitted[i+9]);
                String playTime = splitted[i + 6];
                String totalMove = splitted[i+8];
                String time = splitted[i+7];
                Vector vrow = new Vector();
                vrow.add(ID);
                vrow.add(playTime);
                vrow.add(nestedStatus);
                vrow.add(totalMove);
                vrow.add(time);

                vdata.add(vrow);
            }
            tableListHistory.setModel(new DefaultTableModel(vdata, vheader));
        }
    }

    public void setListRoom(Vector vdata, Vector vheader) {
        tbListRoom.setModel(new DefaultTableModel(vdata, vheader));
    }

    public void foundMatch(String competitorName) {
        setDisplayState(State.WAITING_ACCEPT);
//        lbFoundMatch.setText("Đã tìm thấy đối thủ " + competitorName + " . Vào ngay?");
    }

    private void stopAcceptPairMatchTimer() {
        if (acceptPairMatchTimer != null) {
            acceptPairMatchTimer.cancel();
        }
    }

    private void startAcceptPairMatchTimer() {
        acceptPairMatchTimer = new CountDownTimer(acceptWaitingTime);
        acceptPairMatchTimer.setTimerCallBack(
                // end callback
                (Callable) () -> {
                    // reset acceptPairMatchTimer
                    acceptPairMatchTimer.restart();
                    acceptPairMatchTimer.pause();

                    // tự động từ chối nếu quá thời gian mà chưa chọn đồng ý
                    if (!pairAcceptChoosed) {
                        RunClient.socketHandler.declinePairMatch();
                    }
                    return null;
                },
                // tick callback
                (Callable) () -> {
//                    lbTimerPairMatch.setText(acceptPairMatchTimer.getCurrentTick() + "s");
                    return null;
                },
                // tick interval
                1
        );
    }

    private void stopWaitingPairMatchTimer() {
        if (waitingPairTimer != null) {
            waitingPairTimer.cancel();
        }
    }

    private void startWaitingPairMatchTimer() {
        waitingPairTimer = new CountDownTimer(5 * 60); // 5p
        waitingPairTimer.setTimerCallBack(
                (Callable) () -> {
                    setDisplayState(State.DEFAULT);
                    JOptionPane.showMessageDialog(this, "Mãi chả thấy ai tìm trận.. Xui");
                    return null;
                },
                (Callable) () -> {
                    lbFindMatch.setText("Đang tìm trận.. " + (5 * 60 - waitingPairTimer.getCurrentTick()) + "s");
                    return null;
                },
                1
        );
    }

    public void setDisplayState(State s) {

        // mở hết lên
        LookAndFeel.enableComponents(plBtns, true);
//        plFoundMatch.setVisible(true);
        plFindingMatch.setVisible(true);
//        btnAcceptPairMatch.setEnabled(true);
//        btnDeclinePairMatch.setEnabled(true);
        btnLogout.setEnabled(true);

        // xong đóng từng cái tùy theo state
        switch (s) {
            case DEFAULT:
                stopWaitingPairMatchTimer();
                stopAcceptPairMatchTimer();
                plFindingMatch.setVisible(false);
                break;

            case FINDING_MATCH:
                startWaitingPairMatchTimer();
                stopAcceptPairMatchTimer();
                LookAndFeel.enableComponents(plBtns, false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_ACCEPT:
                stopWaitingPairMatchTimer();
                startAcceptPairMatchTimer();
                pairAcceptChoosed = false;
                LookAndFeel.enableComponents(plBtns, false);
                plFindingMatch.setVisible(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_COMPETITOR_ACCEPT:
                LookAndFeel.enableComponents(plBtns, false);
                pairAcceptChoosed = true;
                plFindingMatch.setVisible(false);
                btnLogout.setEnabled(false);
                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel10 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        plBtns = new javax.swing.JPanel();
        btnFindMatch = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtUsername = new javax.swing.JLabel();
        txtElo = new javax.swing.JLabel();
        txtWins = new javax.swing.JLabel();
        txtLose = new javax.swing.JLabel();
        plFindingMatch = new javax.swing.JPanel();
        lbFindMatch = new javax.swing.JLabel();
        btnCancelFindMatch = new javax.swing.JButton();
        tpRoomAndUser = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbListRoom = new javax.swing.JTable();
        btnRefreshListRoom = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableOnline = new javax.swing.JTable();
        btnRefreshListPlayer = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRank = new javax.swing.JTable();
        btnRefreshRank = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableListHistory = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Caro Game");
        setResizable(false);

        plBtns.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));

        btnFindMatch.setText("Tìm trận");
        btnFindMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindMatchActionPerformed(evt);
            }
        });

        btnLogout.setText("Đăng xuất");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plBtnsLayout = new javax.swing.GroupLayout(plBtns);
        plBtns.setLayout(plBtnsLayout);
        plBtnsLayout.setHorizontalGroup(
            plBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plBtnsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFindMatch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
        );
        plBtnsLayout.setVerticalGroup(
            plBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plBtnsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFindMatch)
                    .addComponent(btnLogout))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtUsername.setText("username");

        txtElo.setText("jLabel2");

        txtWins.setText("jLabel3");

        txtLose.setText("jLabel4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtUsername)
                .addGap(82, 82, 82)
                .addComponent(txtElo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(txtWins, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(txtLose, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername)
                    .addComponent(txtElo)
                    .addComponent(txtWins)
                    .addComponent(txtLose))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        lbFindMatch.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lbFindMatch.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFindMatch.setText("Đang tìm trận...");

        btnCancelFindMatch.setText("Hủy");
        btnCancelFindMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelFindMatchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plFindingMatchLayout = new javax.swing.GroupLayout(plFindingMatch);
        plFindingMatch.setLayout(plFindingMatchLayout);
        plFindingMatchLayout.setHorizontalGroup(
            plFindingMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFindingMatchLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbFindMatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(plFindingMatchLayout.createSequentialGroup()
                .addGap(175, 175, 175)
                .addComponent(btnCancelFindMatch)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        plFindingMatchLayout.setVerticalGroup(
            plFindingMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFindingMatchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbFindMatch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelFindMatch)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbListRoom.setAutoCreateRowSorter(true);
        tbListRoom.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        tbListRoom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbListRoom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbListRoom.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tbListRoom);

        btnRefreshListRoom.setText("Làm mới");
        btnRefreshListRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshListRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRefreshListRoom))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefreshListRoom)
                .addContainerGap())
        );

        tpRoomAndUser.addTab("Danh sách phòng", jPanel5);

        tableOnline.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tableOnline);

        btnRefreshListPlayer.setText("Làm mới");
        btnRefreshListPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshListPlayerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(338, Short.MAX_VALUE)
                .addComponent(btnRefreshListPlayer)
                .addGap(18, 18, 18))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(btnRefreshListPlayer)
                .addContainerGap())
        );

        tpRoomAndUser.addTab("Người chơi", jPanel3);

        tableRank.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tableRank);

        btnRefreshRank.setText("Làm mới");
        btnRefreshRank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshRankActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnRefreshRank)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(btnRefreshRank)
                .addContainerGap())
        );

        tpRoomAndUser.addTab("Bảng xếp hạng", jPanel6);

        tableListHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tableListHistory);

        jButton1.setText("Làm mới");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        tpRoomAndUser.addTab("Lịch sử đấu", jPanel7);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpRoomAndUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(plFindingMatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plBtns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(plBtns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plFindingMatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpRoomAndUser, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshRankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshRankActionPerformed
        // TODO add your handling code here:
        RunClient.socketHandler.listRank();
    }//GEN-LAST:event_btnRefreshRankActionPerformed

    private void btnRefreshListPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshListPlayerActionPerformed
        // TODO add your handling code here:
        RunClient.socketHandler.listOnline();
    }//GEN-LAST:event_btnRefreshListPlayerActionPerformed

    private void btnRefreshListRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshListRoomActionPerformed
        RunClient.socketHandler.listRoom();
    }//GEN-LAST:event_btnRefreshListRoomActionPerformed

    private void btnCancelFindMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelFindMatchActionPerformed
        // chỉ gửi yêu cầu lên server chứ ko đổi giao diện ngay
        // socketHandler sẽ đọc kết quả trả về từ server và quyết định có đổi stateDisplay hay không
        RunClient.socketHandler.cancelFindMatch();
    }//GEN-LAST:event_btnCancelFindMatchActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        RunClient.socketHandler.logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnFindMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindMatchActionPerformed
        // chỉ gửi yêu cầu lên server chứ ko đổi giao diện ngay
        // socketHandler sẽ đọc kết quả trả về từ server và quyết định có đổi stateDisplay hay không
        RunClient.socketHandler.findMatch();
    }//GEN-LAST:event_btnFindMatchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        RunClient.socketHandler.listHistory();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelFindMatch;
    private javax.swing.JButton btnFindMatch;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefreshListPlayer;
    private javax.swing.JButton btnRefreshListRoom;
    private javax.swing.JButton btnRefreshRank;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lbFindMatch;
    private javax.swing.JPanel plBtns;
    private javax.swing.JPanel plFindingMatch;
    private javax.swing.JTable tableListHistory;
    private javax.swing.JTable tableOnline;
    private javax.swing.JTable tableRank;
    private javax.swing.JTable tbListRoom;
    private javax.swing.JTabbedPane tpRoomAndUser;
    private javax.swing.JLabel txtElo;
    private javax.swing.JLabel txtLose;
    private javax.swing.JLabel txtUsername;
    private javax.swing.JLabel txtWins;
    // End of variables declaration//GEN-END:variables
}
