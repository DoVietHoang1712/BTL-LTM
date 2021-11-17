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
    
    public void setOnline(int n) {
        txtOnline.setText("Online: "+n);
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

    public void foundMatch(String competitorName) {
        setDisplayState(State.WAITING_ACCEPT);
//        lbFoundMatch.setText("Đã tìm thấy đối thủ " + competitorName + " . Vào ngay?");
    }

    private void stopAcceptPairMatchTimer() {
        if (acceptPairMatchTimer != null) {
            acceptPairMatchTimer.cancel();
        }
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
                    JOptionPane.showMessageDialog(this, "Không tìm thấy");
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
        plFoundMatch.setVisible(true);
        plFindingMatch.setVisible(true);
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
        btnLSD = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtOnline = new javax.swing.JLabel();
        plFindingMatch = new javax.swing.JPanel();
        lbFindMatch = new javax.swing.JLabel();
        btnCancelFindMatch = new javax.swing.JButton();
        plFoundMatch = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableRank = new javax.swing.JTable();

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

        btnLSD.setText("Lịch sử đấu");

        javax.swing.GroupLayout plBtnsLayout = new javax.swing.GroupLayout(plBtns);
        plBtns.setLayout(plBtnsLayout);
        plBtnsLayout.setHorizontalGroup(
            plBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plBtnsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFindMatch)
                .addGap(84, 84, 84)
                .addComponent(btnLSD)
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
                    .addComponent(btnLogout)
                    .addComponent(btnLSD))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtOnline.setText("Online:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtOnline, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 28, Short.MAX_VALUE)
                .addComponent(txtOnline))
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
                .addGap(181, 181, 181)
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

        btnRefresh.setText("Làm mới");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jLabel1.setText("Bảng xếp hạng");

        javax.swing.GroupLayout plFoundMatchLayout = new javax.swing.GroupLayout(plFoundMatch);
        plFoundMatch.setLayout(plFoundMatchLayout);
        plFoundMatchLayout.setHorizontalGroup(
            plFoundMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFoundMatchLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRefresh))
        );
        plFoundMatchLayout.setVerticalGroup(
            plFoundMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFoundMatchLayout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addGroup(plFoundMatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        tableRank.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Username", "Elo", "Win ", "Lose"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tableRank);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plFindingMatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plBtns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plFoundMatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plFoundMatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(233, Short.MAX_VALUE))
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

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        RunClient.socketHandler.logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnFindMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindMatchActionPerformed
        // chỉ gửi yêu cầu lên server chứ ko đổi giao diện ngay
        // socketHandler sẽ đọc kết quả trả về từ server và quyết định có đổi stateDisplay hay không
        RunClient.socketHandler.findMatch();
    }//GEN-LAST:event_btnFindMatchActionPerformed

    private void btnCancelFindMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelFindMatchActionPerformed
        // chỉ gửi yêu cầu lên server chứ ko đổi giao diện ngay
        // socketHandler sẽ đọc kết quả trả về từ server và quyết định có đổi stateDisplay hay không
        RunClient.socketHandler.cancelFindMatch();
    }//GEN-LAST:event_btnCancelFindMatchActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        setUp();
    }//GEN-LAST:event_btnRefreshActionPerformed

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
    private javax.swing.JButton btnLSD;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbFindMatch;
    private javax.swing.JPanel plBtns;
    private javax.swing.JPanel plFindingMatch;
    private javax.swing.JPanel plFoundMatch;
    private javax.swing.JTable tableRank;
    private javax.swing.JLabel txtOnline;
    // End of variables declaration//GEN-END:variables
}
