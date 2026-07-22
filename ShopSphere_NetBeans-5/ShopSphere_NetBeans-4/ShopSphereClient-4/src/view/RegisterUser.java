/*
 * ShopSphere - RegisterUser
 * Inscription utilisateur — connexion RMI identique au modele AgriConnect :
 * connectToServer() fait un lookup synchrone du registre RMI dans le constructeur.
 */
package view;

import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import model.User.Role;
import service0.UserService;
import view.components.*;
import view.theme.Theme;

public class RegisterUser extends JFrame {

    private UserService userService;

    private AppTextField     nomField;
    private AppTextField     emailField;
    private AppPasswordField passwordField;
    private AppPasswordField confirmField;
    private AppTextField     phoneField;
    private AppComboBox<String> roleBox;
    private PrimaryButton    registerBtn;
    private JLabel           statusLabel;
    private JLabel           serverStatusLabel;

    public RegisterUser() {
        initComponents();
        connectToServer();
        buildUI();
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
        setTitle("ShopSphere — Créer un compte");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.NEUTRAL);
        setContentPane(root);

        // ── Header Dégradé Élégan ─────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, 0, getHeight(), new Color(0x0F, 0x28, 0x44));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 110));

        JPanel hc = new JPanel();
        hc.setLayout(new BoxLayout(hc, BoxLayout.Y_AXIS));
        hc.setOpaque(false);

        JLabel logoLbl = new JLabel("ShopSphere — Inscription");
        logoLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        logoLbl.setForeground(Theme.WHITE);
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        serverStatusLabel = new JLabel();
        if (userService != null) {
            serverStatusLabel.setText("● Serveur connecté");
            serverStatusLabel.setForeground(Theme.SUCCESS);
        } else {
            serverStatusLabel.setText("● Serveur hors ligne");
            serverStatusLabel.setForeground(Theme.ERROR);
        }
        serverStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        serverStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        hc.add(logoLbl);
        hc.add(Box.createVerticalStrut(8));
        hc.add(serverStatusLabel);
        header.add(hc);
        root.add(header, BorderLayout.NORTH);

        // ── Card Container ──────────────────────────────────────────────────
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Theme.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(16, 24, 20, 24),
                BorderFactory.createLineBorder(Theme.LIGHT_GREY, 1, true)
        ));

        // Nom complet
        formCard.add(label("Nom complet"));
        formCard.add(Box.createVerticalStrut(4));
        nomField = new AppTextField("Jean Dupont");
        nomField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(nomField);
        formCard.add(Box.createVerticalStrut(10));

        // Email
        formCard.add(label("Adresse email"));
        formCard.add(Box.createVerticalStrut(4));
        emailField = new AppTextField("votre@email.com");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(emailField);
        formCard.add(Box.createVerticalStrut(10));

        // Telephone
        formCard.add(label("Téléphone (optionnel)"));
        formCard.add(Box.createVerticalStrut(4));
        phoneField = new AppTextField("+33 6 12 34 56 78");
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(phoneField);
        formCard.add(Box.createVerticalStrut(10));

        // Mot de passe
        formCard.add(label("Mot de passe (8+ car., majuscule, chiffre)"));
        formCard.add(Box.createVerticalStrut(4));
        passwordField = new AppPasswordField("Votre mot de passe");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(10));

        // Confirmation
        formCard.add(label("Confirmer le mot de passe"));
        formCard.add(Box.createVerticalStrut(4));
        confirmField = new AppPasswordField("Confirmer le mot de passe");
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(confirmField);
        formCard.add(Box.createVerticalStrut(10));

        // Role
        formCard.add(label("Vous êtes :"));
        formCard.add(Box.createVerticalStrut(4));
        roleBox = new AppComboBox<>(new String[]{"Client", "Vendeur"});
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(roleBox);
        formCard.add(Box.createVerticalStrut(14));

        // Statut d'erreur / validation
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(Theme.ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        formCard.add(statusLabel);
        formCard.add(Box.createVerticalStrut(6));

        // Bouton creation
        registerBtn = new PrimaryButton("Créer mon compte");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerBtn.addActionListener(e -> performRegister());
        formCard.add(registerBtn);
        formCard.add(Box.createVerticalStrut(10));

        // Retour connexion
        SecondaryButton backBtn = new SecondaryButton("Déjà un compte ? Se connecter");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        backBtn.addActionListener(e -> { dispose(); new UserLogin(); });
        formCard.add(backBtn);

        JPanel outerPadding = new JPanel(new BorderLayout());
        outerPadding.setBackground(Theme.NEUTRAL);
        outerPadding.setBorder(new EmptyBorder(16, 24, 20, 24));
        outerPadding.add(formCard, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(outerPadding);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        root.add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(Theme.DARK_TEXT);
        return l;
    }

    private void performRegister() {
        String nom     = nomField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();
        String pass    = passwordField.getText();
        String confirm = confirmField.getText();
        String roleStr = (String) roleBox.getSelectedItem();

        if (nom.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires."); return;
        }
        if (!pass.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas."); return;
        }
        if (pass.length() < 8) {
            showError("Le mot de passe doit avoir au moins 8 caractères."); return;
        }

        registerBtn.setEnabled(false);
        registerBtn.setText("Création...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override protected User doInBackground() throws Exception {
                if (userService == null) { connectToServer(); }
                if (userService == null) throw new Exception("Impossible de se connecter au serveur.");
                User newUser = new User();
                newUser.setFullName(nom);
                newUser.setEmail(email);
                newUser.setPhone(phone.isEmpty() ? null : phone);
                newUser.setPasswordHash(pass); // hash fait coté serveur
                newUser.setRole("Vendeur".equals(roleStr) ? Role.SELLER : Role.CLIENT);
                newUser.setPreferredLanguage("fr");
                return userService.createUserRecord(newUser);
            }
            @Override protected void done() {
                registerBtn.setEnabled(true);
                registerBtn.setText("Créer mon compte");
                try {
                    User created = get();
                    if (created != null) {
                        try {
                            if (userService == null) { connectToServer(); }
                            userService.envoyerOtp(created.getId(), "EMAIL", "INSCRIPTION");
                        } catch (Exception ignored) {}
                        JOptionPane.showMessageDialog(RegisterUser.this,
                            "Compte créé ! Un code de vérification a été envoyé à :\n" + email,
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new OtpVerification(created.getId(), "INSCRIPTION", email).setVisible(true);
                    }
                } catch (Exception ex) {
                    String msg = ErrorUtil.rootMessage(ex);
                    showError(msg != null ? msg : "Erreur lors de la création du compte.");
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg) {
        statusLabel.setText("<html><center>" + msg + "</center></html>");
        statusLabel.setVisible(true);
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
