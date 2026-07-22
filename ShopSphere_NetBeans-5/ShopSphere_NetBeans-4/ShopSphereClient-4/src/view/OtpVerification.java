/*
 * ShopSphere - OtpVerification
 * NOUVEAU — AgriConnect n'avait pas de verification OTP securisee.
 * Fenetre de saisie du code OTP (6 chiffres) recu par SMS ou email.
 */
package view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import service0.UserService;
import view.components.*;
import view.theme.Theme;

public class OtpVerification extends JFrame {

    private UserService userService;

    private final int    userId;
    private final String raison;
    private final String contact;

    private AppTextField codeField;
    private PrimaryButton verifyBtn;
    private JLabel        statusLabel;
    private JLabel        timerLabel;
    private int           secondes = 60;
    private Timer         timer;

    public OtpVerification(int userId, String raison, String contact) {
        this.userId  = userId;
        this.raison  = raison;
        this.contact = contact;
        initComponents();
        connectToServer();
        buildUI();
        startTimer();
    }

    /**
     * Connexion RMI — identique a AgriConnect : LocateRegistry.getRegistry(host, port)
     * + reg.lookup("UserService").
     */
    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.userService = (UserService) reg.lookup("UserService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }

    private void buildUI() {
        setTitle("ShopSphere — Verification OTP");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 440);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 100));
        JPanel hc = new JPanel();
        hc.setLayout(new BoxLayout(hc, BoxLayout.Y_AXIS));
        hc.setBackground(Theme.PRIMARY);
        JLabel titre = new JLabel("Verification");
        titre.setFont(Theme.FONT_TITLE);
        titre.setForeground(Theme.WHITE);
        JLabel sub = new JLabel("Code envoye a : " + contact);
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(new Color(0x94, 0xB8, 0xD4));
        hc.add(titre); hc.add(Box.createVerticalStrut(4)); hc.add(sub);
        header.add(hc);
        add(header, BorderLayout.NORTH);

        // Corps
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.NEUTRAL);
        body.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel instr = new JLabel("<html><center>Entrez le code a 6 chiffres<br>recu par email ou SMS.</center></html>");
        instr.setFont(Theme.FONT_BODY);
        instr.setForeground(Theme.GREY_TEXT);
        instr.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(instr);
        body.add(Box.createVerticalStrut(24));

        // Champ code — gros et centre
        codeField = new AppTextField("000000");
        codeField.setFont(Theme.FONT_MONO);
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        body.add(codeField);
        body.add(Box.createVerticalStrut(10));

        // Timer
        timerLabel = new JLabel("Code valide pendant : 60s");
        timerLabel.setFont(Theme.FONT_SMALL);
        timerLabel.setForeground(Theme.GREY_TEXT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(timerLabel);
        body.add(Box.createVerticalStrut(16));

        // Statut
        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_BODY);
        statusLabel.setForeground(Theme.ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        body.add(statusLabel);
        body.add(Box.createVerticalStrut(6));

        // Bouton verifier
        verifyBtn = new PrimaryButton("Verifier le code");
        verifyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        verifyBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        verifyBtn.addActionListener(e -> verifyCode());
        body.add(verifyBtn);
        body.add(Box.createVerticalStrut(10));

        // Renvoyer
        SecondaryButton resendBtn = new SecondaryButton("Renvoyer le code");
        resendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resendBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        resendBtn.addActionListener(e -> resendOtp());
        body.add(resendBtn);

        add(body, BorderLayout.CENTER);
    }

    private void startTimer() {
        secondes = 600; // 10 minutes
        timer = new Timer(1000, e -> {
            secondes--;
            int min = secondes / 60;
            int sec = secondes % 60;
            timerLabel.setText(String.format("Code valide pendant : %d:%02d", min, sec));
            if (secondes <= 0) {
                ((Timer) e.getSource()).stop();
                timerLabel.setText("Code expire — veuillez en demander un nouveau.");
                timerLabel.setForeground(Theme.ERROR);
                verifyBtn.setEnabled(false);
            }
        });
        timer.start();
    }

    private void verifyCode() {
        String code = codeField.getText().trim();
        if (code.length() != 6) { showError("Entrez les 6 chiffres du code."); return; }
        verifyBtn.setEnabled(false); verifyBtn.setText("Verification...");

        SwingWorker<Boolean, Void> w = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                if (userService == null) { connectToServer(); }
                if (userService == null) {
                    throw new Exception("Cannot connect to server.");
                }
                return userService.verifierOtp(userId, code, raison);
            }
            @Override protected void done() {
                verifyBtn.setEnabled(true); verifyBtn.setText("Verifier le code");
                try {
                    if (get()) {
                        if (timer != null) timer.stop();
                        JOptionPane.showMessageDialog(OtpVerification.this,
                            "Verification reussie !", "Succes", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new UserLogin();
                    }
                } catch (Exception ex) {
                    String msg = ErrorUtil.rootMessage(ex);
                    showError(msg != null ? msg : "Code invalide.");
                }
            }
        };
        w.execute();
    }

    private void resendOtp() {
        try {
            if (userService == null) { connectToServer(); }
            userService.renvoyerOtp(userId, raison);
            startTimer();
            JOptionPane.showMessageDialog(this, "Nouveau code envoye a : " + contact,
                "Code envoye", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Erreur renvoi : " + ErrorUtil.rootMessage(ex));
        }
    }

    private void showError(String msg) { statusLabel.setText(msg); statusLabel.setVisible(true); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
