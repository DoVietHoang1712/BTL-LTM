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
        btnCreateRoom = new javax.swing.JButton();
        btnFindMatch = new javax.swing.JButton();
        btnJoin = new javax.swing.JButton();
        btnWatch = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        btnProfile = new javax.swing.JButton();
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

        btnCreateRoom.setText("Tạo phòng");
        btnCreateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateRoomActionPerformed(evt);
            }
        });

        btnFindMatch.setText("Tìm trận");
        btnFindMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindMatchActionPerformed(evt);
            }
        });

        btnJoin.setText("Vào phòng");
        btnJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJoinActionPerformed(evt);
            }
        });

        btnWatch.setText("Vào xem");
        btnWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWatchActionPerformed(evt);
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
                .addComponent(btnWatch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnJoin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreateRoom)
                .addContainerGap())
        );
        plBtnsLayout.setVerticalGroup(
            plBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plBtnsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateRoom)
                    .addComponent(btnFindMatch)
                    .addComponent(btnJoin)
                    .addComponent(btnWatch))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnLogout.setText("Đăng xuất");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnProfile.setText("Hồ sơ");
        btnProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnLogout)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProfile)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogout)
                    .addComponent(btnProfile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(lbFindMatch, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
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
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btnRefreshListPlayer)
                        .addGap(18, 18, 18))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(btnRefreshListPlayer)
                .addGap(24, 24, 24))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefreshRank)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        tpRoomAndUser.addTab("Bảng xếp hạng", jPanel6);

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

    private void btnProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileActionPerformed
        RunClient.openScene(RunClient.SceneName.PROFILE);
        RunClient.profileScene.loadProfileData(RunClient.socketHandler.getLoginEmail());
    }//GEN-LAST:event_btnProfileActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        RunClient.socketHandler.logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWatchActionPerformed
        // https://stackoverflow.com/a/38981623
        int column = 0;
        int row = tbListRoom.getSelectedRow();
        if (row >= 0) {
            String roomId = tbListRoom.getModel().getValueAt(row, column).toString();
            RunClient.socketHandler.watchRoom(roomId);
        }
    }//GEN-LAST:event_btnWatchActionPerformed

    private void btnJoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJoinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnJoinActionPerformed

    private void btnFindMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindMatchActionPerformed
        // chỉ gửi yêu cầu lên server chứ ko đổi giao diện ngay
        // socketHandler sẽ đọc kết quả trả về từ server và quyết định có đổi stateDisplay hay không
        RunClient.socketHandler.findMatch();
    }//GEN-LAST:event_btnFindMatchActionPerformed

    private void btnCreateRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateRoomActionPerformed

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
    private javax.swing.JButton btnCreateRoom;
    private javax.swing.JButton btnFindMatch;
    private javax.swing.JButton btnJoin;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProfile;
    private javax.swing.JButton btnRefreshListPlayer;
    private javax.swing.JButton btnRefreshListRoom;
    private javax.swing.JButton btnRefreshRank;
    private javax.swing.JButton btnWatch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbFindMatch;
    private javax.swing.JPanel plBtns;
    private javax.swing.JPanel plFindingMatch;
    private javax.swing.JTable tableOnline;
    private javax.swing.JTable tableRank;
    private javax.swing.JTable tbListRoom;
    private javax.swing.JTabbedPane tpRoomAndUser;
    // End of variables declaration//GEN-END:variables
}
