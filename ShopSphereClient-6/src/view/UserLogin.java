/*
 * ShopSphere - UserLogin
 * Connexion RMI — identique au modele AgriConnect : connectToServer()
 * fait un lookup synchrone du registre RMI dans le constructeur.
 * Design professionnel avec header dégradé et validation fluide.
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
 * Fenêtre de connexion ShopSphere.
 * @author ShopSphere
 */
public class UserLogin extends JFrame {

    private UserService userService;

    private AppTextField     emailField;
    private AppPasswordField passwordField;
    private PrimaryButton    loginBtn;
    private SecondaryButton  registerBtn;
    private JLabel           statusLabel;
    private JLabel           serverStatusLabel;
    private JPanel           mainPanel;

    public UserLogin() {
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
        setTitle("ShopSphere — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 680);
        setMinimumSize(new Dimension(440, 620));
        setLocationRelativeTo(null);
        setResizable(true);
        setBackground(Theme.NEUTRAL);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.NEUTRAL);
        setContentPane(mainPanel);

        // ── Header Dégradé Bleu Marine ────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, Theme.PRIMARY,
                    getWidth(), getHeight(), new Color(0x0D, 0x24, 0x3C)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Décoration : cercle doré translucide
                g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(),
                        Theme.ACCENT.getBlue(), 30));
                g2.fillOval(getWidth() - 80, -30, 130, 130);
                g2.fillOval(-40, getHeight() - 40, 100, 80);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 175));

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setOpaque(false);

        // Icône ShopSphere stylisée
        JLabel iconLbl = new JLabel("🛍");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerContent.add(iconLbl);
        headerContent.add(Box.createVerticalStrut(6));

        JLabel logoLbl = new JLabel("ShopSphere");
        logoLbl.setFont(new Font("SansSerif", Font.BOLD, 30));
        logoLbl.setForeground(Theme.WHITE);
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel taglineLbl = new JLabel("Achetez. Vendez. Simplement.");
        taglineLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        taglineLbl.setForeground(new Color(0xA8, 0xC8, 0xE8));
        taglineLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Indicateur statut serveur — reflete le resultat de connectToServer()
        serverStatusLabel = new JLabel();
        if (userService != null) {
            serverStatusLabel.setText("● Serveur connecté");
            serverStatusLabel.setForeground(Theme.SUCCESS);
        } else {
            serverStatusLabel.setText("● Serveur hors ligne — réessayez");
            serverStatusLabel.setForeground(Theme.ERROR);
        }
        serverStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        serverStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerContent.add(logoLbl);
        headerContent.add(Box.createVerticalStrut(4));
        headerContent.add(taglineLbl);
        headerContent.add(Box.createVerticalStrut(8));
        headerContent.add(serverStatusLabel);
        header.add(headerContent);
        mainPanel.add(header, BorderLayout.NORTH);

        // ── Formulaire Card ───────────────────────────────────────────────────
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Theme.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(14, Theme.LIGHT_GREY),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JLabel titleLbl = new JLabel("Connexion à votre compte");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLbl.setForeground(Theme.DARK_TEXT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(titleLbl);
        formCard.add(Box.createVerticalStrut(4));

        JLabel subtitleLbl = new JLabel("Bon retour parmi nous !");
        subtitleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLbl.setForeground(Theme.GREY_TEXT);
        subtitleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(subtitleLbl);
        formCard.add(Box.createVerticalStrut(20));

        // Email
        formCard.add(fieldLabel("Adresse email"));
        formCard.add(Box.createVerticalStrut(5));
        emailField = new AppTextField("votre@email.com");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(emailField);
        formCard.add(Box.createVerticalStrut(14));

        // Mot de passe
        formCard.add(fieldLabel("Mot de passe"));
        formCard.add(Box.createVerticalStrut(5));
        passwordField = new AppPasswordField("Votre mot de passe");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(6));

        // Mot de passe oublié
        JLabel forgotLbl = new JLabel("Mot de passe oublié ?");
        forgotLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        forgotLbl.setForeground(Theme.PRIMARY);
        forgotLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        forgotLbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(UserLogin.this,
                    "Un code OTP sera envoyé à votre adresse email.\nFonctionnalité disponible.",
                    "Réinitialisation", JOptionPane.INFORMATION_MESSAGE);
            }
            @Override public void mouseEntered(MouseEvent e) { forgotLbl.setForeground(Theme.ACCENT); }
            @Override public void mouseExited(MouseEvent e)  { forgotLbl.setForeground(Theme.PRIMARY); }
        });
        formCard.add(forgotLbl);
        formCard.add(Box.createVerticalStrut(16));

        // Statut erreur
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(Theme.ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setVisible(false);
        formCard.add(statusLabel);
        formCard.add(Box.createVerticalStrut(4));

        // Bouton connexion
        loginBtn = new PrimaryButton("Se connecter");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> performLogin());
        formCard.add(loginBtn);
        formCard.add(Box.createVerticalStrut(14));

        // Séparateur
        JPanel sepPanel = new JPanel(new BorderLayout(8, 0));
        sepPanel.setBackground(Theme.WHITE);
        sepPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JSeparator sep1 = new JSeparator(); sep1.setForeground(Theme.LIGHT_GREY);
        JSeparator sep2 = new JSeparator(); sep2.setForeground(Theme.LIGHT_GREY);
        JLabel orLbl = new JLabel("ou");
        orLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        orLbl.setForeground(Theme.GREY_TEXT);
        orLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sepPanel.add(sep1, BorderLayout.WEST);
        sepPanel.add(orLbl, BorderLayout.CENTER);
        sepPanel.add(sep2, BorderLayout.EAST);
        formCard.add(sepPanel);
        formCard.add(Box.createVerticalStrut(12));

        // Bouton inscription
        registerBtn = new SecondaryButton("Créer un compte gratuitement");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.addActionListener(e -> openRegister());
        formCard.add(registerBtn);

        JPanel outerPadding = new JPanel(new BorderLayout());
        outerPadding.setBackground(Theme.NEUTRAL);
        outerPadding.setBorder(new EmptyBorder(18, 24, 18, 24));
        outerPadding.add(formCard, BorderLayout.CENTER);

        mainPanel.add(outerPadding, BorderLayout.CENTER);

        // Pied de page
        JLabel footerLbl = new JLabel("ShopSphere © 2026 — Tous droits réservés");
        footerLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footerLbl.setForeground(Theme.GREY_TEXT);
        footerLbl.setHorizontalAlignment(SwingConstants.CENTER);
        footerLbl.setBorder(new EmptyBorder(4, 0, 8, 0));
        mainPanel.add(footerLbl, BorderLayout.SOUTH);

        // Enter pour connexion
        getRootPane().setDefaultButton(loginBtn);
        setVisible(true);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(Theme.DARK_TEXT);
        return l;
    }

    private void performLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        statusLabel.setVisible(false);
        loginBtn.setEnabled(false);
        loginBtn.setText("Connexion...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                if (userService == null) { connectToServer(); }
                if (userService == null) {
                    throw new Exception("Impossible de se connecter au serveur.");
                }
                return userService.login(email, password);
            }

            @Override
            protected void done() {
                loginBtn.setEnabled(true);
                loginBtn.setText("Se connecter");
                try {
                    User user = get();
                    if (user != null) {
                        Session.setCurrentUser(user);
                        if (user.isAdmin()) {
                            AppNavigator.show(new AdminPanel(user));
                        } else {
                            AppNavigator.show(new MarketPlace(user));
                        }
                    }
                } catch (Exception ex) {
                    String msg = ErrorUtil.rootMessage(ex);
                    showError(msg != null ? msg : "Erreur de connexion au serveur.");
                }
            }
        };
        worker.execute();
    }

    private void openRegister() {
        AppNavigator.show(new RegisterUser());
    }

    private void showError(String msg) {
        statusLabel.setText("<html><center>" + msg + "</center></html>");
        statusLabel.setVisible(true);
    }

    /** Bordure arrondie personnalisée pour la card de formulaire. */
    private static class RoundedBorder extends EmptyBorder {
        private final int   radius;
        private final Color color;
        RoundedBorder(int radius, Color color) {
            super(0, 0, 0, 0);
            this.radius = radius; this.color = color;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }
        @Override public boolean isBorderOpaque() { return false; }
    }

    static {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }

    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) { /* ignore */ }
            AppNavigator.show(new UserLogin());
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
