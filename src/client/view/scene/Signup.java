/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view.scene;

import client.RunClient;
import client.view.helper.Validation;
import shared.constant.Avatar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class Signup extends javax.swing.JFrame {

    /**
     * Creates new form SignupF
     */
    public Signup() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    public void setLoading(boolean isLoading) {
        btnSignup.setEnabled(!isLoading);
        btnLogin.setEnabled(!isLoading);
        btnSignup.setText(isLoading ? "Đang xử lý.." : "Đăng ký");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbHeaderText = new javax.swing.JLabel();
        plInput = new javax.swing.JPanel();
        txEmail = new javax.swing.JTextField();
        txPassword = new javax.swing.JPasswordField();
        txRetypePassword = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSignup = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Đăng ký");
        setResizable(false);

        lbHeaderText.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        lbHeaderText.setText("THAM GIA");

        txEmail.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txEmail.setToolTipText("Email");

        txPassword.setToolTipText("Mật khẩu");

        txRetypePassword.setToolTipText("Mật khẩu");

        jLabel1.setText("Username");

        jLabel2.setText("Mật khẩu");

        jLabel4.setText("Nhập lại mật khẩu");

        javax.swing.GroupLayout plInputLayout = new javax.swing.GroupLayout(plInput);
        plInput.setLayout(plInputLayout);
        plInputLayout.setHorizontalGroup(
            plInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plInputLayout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addGroup(plInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(txEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txRetypePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
        );
        plInputLayout.setVerticalGroup(
            plInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plInputLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txRetypePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnSignup.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        btnSignup.setText("ĐĂNG KÝ");
        btnSignup.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSignup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSignupMouseClicked(evt);
            }
        });

        btnLogin.setText("Đăng nhập?");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLoginMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbHeaderText)
                    .addComponent(btnSignup, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin)
                    .addComponent(plInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lbHeaderText)
                .addGap(55, 55, 55)
                .addComponent(plInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(btnSignup, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogin)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSignupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSignupMouseClicked
        try {
            // get data from form
            String username = txEmail.getText();
            String password = new String(txPassword.getPassword());
            String rePass = new String(txRetypePassword.getPassword());

            // validate
            if (!Validation.checkPassword(password)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu phải từ 6-30 ký tự", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txPassword.requestFocus();
                return;
            }
            if (!rePass.equals(password)) {
                JOptionPane.showMessageDialog(this, "Nhập lại mật khẩu chưa khớp", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txRetypePassword.requestFocus();
                return;
            }
            if (!Validation.checkUsername(username)) {
                JOptionPane.showMessageDialog(this, "Username không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txEmail.requestFocus();
                return;
            }

            // call signup from socket handler
            setLoading(true);
            RunClient.socketHandler.signup(username, password);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm sinh phải là số nguyên", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSignupMouseClicked

    private void btnLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseClicked
        this.dispose();
        RunClient.openScene(RunClient.SceneName.LOGIN);
    }//GEN-LAST:event_btnLoginMouseClicked

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
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Signup().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnSignup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lbHeaderText;
    private javax.swing.JPanel plInput;
    private javax.swing.JTextField txEmail;
    private javax.swing.JPasswordField txPassword;
    private javax.swing.JPasswordField txRetypePassword;
    // End of variables declaration//GEN-END:variables
}
