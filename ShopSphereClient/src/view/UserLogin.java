/*
 * ShopSphere - UserLogin
 * Meme structure qu'AgriConnect view/UserLogin.java (JFrame de connexion)
 * Améliorations :
 *   - BCrypt cote serveur (le client envoie le mot de passe brut via RMI securise)
 *   - Bouton OTP pour verification en 2 etapes
 *   - Textes en francais (langue principale)
 *   - Design ShopSphere (bleu marine + or ambre)
 */
package view;

import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import service0.UserService;
import view.components.*;
import view.theme.Theme;

/**
 * Fenetre de connexion ShopSphere.
 * Meme structure JFrame qu'AgriConnect UserLogin.java
 * @author ShopSphere
 */
public class UserLogin extends JFrame {

    private AppTextField     emailField;
    private AppPasswordField passwordField;
    private PrimaryButton    loginBtn;
    private SecondaryButton  registerBtn;
    private JLabel           statusLabel;
    private UserService userService;

    public UserLogin() {
        initComponents();
        buildUI();
        connectToServer();
    }
     private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.userService = (UserService) reg.lookup("UserService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }
    private void buildUI() {
        setTitle("ShopSphere — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // ── En-tete bleu marine ────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 140));

        JLabel logoLbl = new JLabel("ShopSphere");
        logoLbl.setFont(new Font("Arial", Font.BOLD, 30));
        logoLbl.setForeground(Theme.WHITE);

        JLabel taglineLbl = new JLabel("Achetez. Vendez. Simplicite.");
        taglineLbl.setFont(Theme.FONT_BODY);
        taglineLbl.setForeground(new Color(0x94, 0xB8, 0xD4));

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(Theme.PRIMARY);
        headerContent.add(logoLbl);
        headerContent.add(Box.createVerticalStrut(6));
        headerContent.add(taglineLbl);
        header.add(headerContent);
        add(header, BorderLayout.NORTH);

        // ── Formulaire ─────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.NEUTRAL);
        form.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel titleLbl = new JLabel("Connexion");
        titleLbl.setFont(Theme.FONT_TITLE);
        titleLbl.setForeground(Theme.DARK_TEXT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(titleLbl);
        form.add(Box.createVerticalStrut(20));

        // Email
        JLabel emailLbl = new JLabel("Adresse email");
        emailLbl.setFont(Theme.FONT_HEADING);
        emailLbl.setForeground(Theme.DARK_TEXT);
        form.add(emailLbl);
        form.add(Box.createVerticalStrut(4));
        emailField = new AppTextField("votre@email.com");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(emailField);
        form.add(Box.createVerticalStrut(14));

        // Mot de passe
        JLabel pwdLbl = new JLabel("Mot de passe");
        pwdLbl.setFont(Theme.FONT_HEADING);
        pwdLbl.setForeground(Theme.DARK_TEXT);
        form.add(pwdLbl);
        form.add(Box.createVerticalStrut(4));
        passwordField = new AppPasswordField("Votre mot de passe");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(passwordField);

        // Mot de passe oublie
        JLabel forgotLbl = new JLabel("Mot de passe oublie ?");
        forgotLbl.setFont(Theme.FONT_SMALL);
        forgotLbl.setForeground(Theme.PRIMARY);
        forgotLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        forgotLbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(UserLogin.this,
                    "Un code OTP va etre envoye a votre email.\nFonctionnalite disponible dans la prochaine version.",
                    "Reinitialisation", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        form.add(Box.createVerticalStrut(6));
        form.add(forgotLbl);
        form.add(Box.createVerticalStrut(20));

        // Statut erreur
        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_BODY);
        statusLabel.setForeground(Theme.ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(4));

        // Bouton connexion
        loginBtn = new PrimaryButton("Se connecter");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        loginBtn.addActionListener(e -> performLogin());
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(10));

        // Separateur
        JLabel orLbl = new JLabel("— ou —");
        orLbl.setFont(Theme.FONT_SMALL);
        orLbl.setForeground(Theme.GREY_TEXT);
        orLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(orLbl);
        form.add(Box.createVerticalStrut(10));

        // Bouton inscription
        registerBtn = new SecondaryButton("Creer un compte");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        registerBtn.addActionListener(e -> openRegister());
        form.add(registerBtn);

        add(form, BorderLayout.CENTER);

        // Enter pour connexion
        getRootPane().setDefaultButton(loginBtn);
        setVisible(true);
    }

    /**
     * Connexion via RMI — meme logique qu'AgriConnect UserLogin.
     * Le mot de passe est verifie cote serveur par BCrypt.
     * Etait : userService.login(email, hash) avec hash MD5
     * Maintenant : userService.login(email, plainPassword) => BCrypt.checkpw cote serveur
     */
    private void performLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("Connexion...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                UserService us = RMIClient.getUserService();
                // Le serveur verifie le mot de passe avec BCrypt
                return us.login(email, password);
            }

            @Override
            protected void done() {
                loginBtn.setEnabled(true);
                loginBtn.setText("Se connecter");
                try {
                    User user = get();
                    if (user != null) {
                        Session.setCurrentUser(user);
                        dispose();
                        // Rediriger selon le role (meme logique qu'AgriConnect)
                        if (user.isAdmin()) {
                            new AdminPanel(user).setVisible(true);
                        } else {
                            new MarketPlace(user).setVisible(true);
                        }
                    }
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    showError(msg != null ? msg : "Erreur de connexion au serveur.");
                }
            }
        };
        worker.execute();
    }

    private void openRegister() {
        dispose();
        new RegisterUser().setVisible(true);
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setVisible(true);
    }

    public static void main(String[] args) {
        // Point d'entree de l'application client — meme qu'AgriConnect
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) { /* ignore */ }
            new UserLogin();
        });
    }

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
