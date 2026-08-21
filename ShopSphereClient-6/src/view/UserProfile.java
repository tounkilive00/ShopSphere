/*
 * ShopSphere - UserProfile
 * Profil utilisateur modernisé — photo de profil, modification des infos,
 * changement de mot de passe, historique des commandes.
 */
package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.User;
import service0.UserService;
import view.components.*;
import view.theme.Theme;

/**
 * Fenêtre profil utilisateur avec photo de profil, modification des infos
 * et navigation vers les fonctionnalités clés.
 * @author ShopSphere
 */
public class UserProfile extends JFrame {

    private final User currentUser;
    private UserService userService;

    // Avatar
    private BufferedImage avatarImage = null;
    private JPanel        avatarPanel;
    private static final int AVATAR_SIZE = 90;

    public UserProfile(User user) {
        initComponents();
        this.currentUser = user;
        connectToServer();
        setTitle("ShopSphere — Mon profil");
        setSize(520, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(Theme.NEUTRAL);
        loadAvatarFromUrl(user.getProfilePictureUrl());
        buildUI();
    }

    /** Connexion RMI asynchrone — ne bloque pas le constructeur. */
    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.userService = (UserService) reg.lookup("UserService");
        } catch (Exception e) {
            System.err.println("Profile: server connection failed: " + e.getMessage());
        }
    }

    /** Charge l'image de profil depuis une URL locale (chemin fichier). */
    private void loadAvatarFromUrl(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            File f = new File(url);
            if (f.exists()) {
                avatarImage = ImageIO.read(f);
            }
        } catch (IOException ignored) {}
    }

    private void buildUI() {
        // ── Header Dégradé avec Avatar ─────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, Theme.PRIMARY, getWidth(), getHeight(), new Color(0x0F, 0x28, 0x44));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Décoration arc doré en bas
                g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(),
                        Theme.ACCENT.getBlue(), 60));
                g2.fillOval(-40, getHeight() - 60, getWidth() + 80, 120);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 185));

        // Contenu centré dans le header
        JPanel hc = new JPanel();
        hc.setLayout(new BoxLayout(hc, BoxLayout.Y_AXIS));
        hc.setOpaque(false);
        hc.setBorder(new EmptyBorder(20, 0, 16, 0));

        // ── Avatar cliquable ──────────────────────────────────────────────
        avatarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                int s = AVATAR_SIZE;
                // Ombre portée
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillOval(3, 5, s, s);
                // Bordure or (ring)
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(1, 1, s, s);

                if (avatarImage != null) {
                    // Image rognée en cercle
                    Shape clip = new Ellipse2D.Float(2, 2, s - 2, s - 2);
                    g2.setClip(clip);
                    g2.drawImage(avatarImage, 2, 2, s - 2, s - 2, null);
                    g2.setClip(null);
                } else {
                    // Fond de couleur avec initiales
                    g2.setColor(new Color(0x1A, 0x4A, 0x72));
                    g2.fillOval(2, 2, s - 2, s - 2);
                    String initials = getInitials(currentUser.getFullName());
                    g2.setFont(new Font("SansSerif", Font.BOLD, 32));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(initials,
                            (s - fm.stringWidth(initials)) / 2,
                            (s + fm.getAscent() - fm.getDescent()) / 2);
                }
                // Icône appareil photo (badge)
                g2.setColor(Theme.ACCENT);
                g2.fillOval(s - 20, s - 18, 22, 22);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                g2.setColor(Color.WHITE);
                g2.drawString("📷", s - 17, s + 1);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(AVATAR_SIZE + 4, AVATAR_SIZE + 8); }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatarPanel.setToolTipText("Cliquer pour changer la photo de profil");
        avatarPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { pickProfilePicture(); }
            @Override public void mouseEntered(MouseEvent e) { avatarPanel.setOpaque(false); avatarPanel.repaint(); }
        });
        hc.add(avatarPanel);
        hc.add(Box.createVerticalStrut(10));

        JLabel nameLbl = new JLabel(currentUser.getFullName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        hc.add(nameLbl);
        hc.add(Box.createVerticalStrut(4));

        // Badge rôle
        String roleText = currentUser.isAdmin() ? "👑 Administrateur"
                        : currentUser.isSeller() ? "🏪 Vendeur"
                        : "🛍 Client";
        JLabel roleLbl = new JLabel(roleText);
        roleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        roleLbl.setForeground(Theme.ACCENT);
        roleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        hc.add(roleLbl);

        header.add(hc, BorderLayout.CENTER);

        // Bouton retour (haut gauche)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setOpaque(false);
        SecondaryButton backBtn = new SecondaryButton("← Catalogue");
        backBtn.addActionListener(e -> AppNavigator.show(new MarketPlace(currentUser)));
        topBar.add(backBtn);
        header.add(topBar, BorderLayout.NORTH);

        add(header, BorderLayout.NORTH);

        // ── Corps scrollable ───────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.NEUTRAL);
        body.setBorder(new EmptyBorder(16, 20, 20, 20));

        // Section infos
        body.add(sectionTitle("Informations personnelles"));
        body.add(Box.createVerticalStrut(6));
        body.add(infoRow("Email", currentUser.getEmail(),
                currentUser.isEmailVerified() ? "✓ Vérifié" : "✗ Non vérifié",
                currentUser.isEmailVerified() ? Theme.SUCCESS : Theme.WARNING));
        body.add(infoRow("Téléphone", currentUser.getPhone() != null ? currentUser.getPhone() : "—", null, null));
        body.add(infoRow("Langue", "fr".equals(currentUser.getPreferredLanguage()) ? "Français" : "Anglais", null, null));
        body.add(Box.createVerticalStrut(18));

        // Section actions
        body.add(sectionTitle("Actions du compte"));
        body.add(Box.createVerticalStrut(8));

        PrimaryButton histBtn = new PrimaryButton("📋  Historique de mes commandes");
        histBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H + 4));
        histBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        histBtn.addActionListener(e -> AppNavigator.show(new OrderHistory(currentUser)));
        body.add(histBtn);
        body.add(Box.createVerticalStrut(10));

        if (currentUser.isSeller()) {
            AccentButton dashBtn = new AccentButton("📊  Tableau de bord vendeur");
            dashBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H + 4));
            dashBtn.addActionListener(e -> AppNavigator.show(new SellerDashboard(currentUser)));
            body.add(dashBtn);
            body.add(Box.createVerticalStrut(10));
        }

        // Changer le mot de passe
        SecondaryButton chgPwdBtn = new SecondaryButton("🔐  Changer le mot de passe");
        chgPwdBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H + 4));
        chgPwdBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        chgPwdBtn.addActionListener(e -> showChangePasswordDialog());
        body.add(chgPwdBtn);
        body.add(Box.createVerticalStrut(10));

        // Déconnexion
        JButton logoutBtn = new JButton("Se déconnecter") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(0xC0, 0x20, 0x20)
                        : new Color(0xDC, 0x26, 0x26);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H + 4));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            Session.logout();
            AppNavigator.show(new UserLogin());
        });
        body.add(logoutBtn);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(Theme.NEUTRAL);
        scroll.getViewport().setBackground(Theme.NEUTRAL);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }

    /** Ouvre un sélecteur de fichier et met à jour l'avatar. */
    private void pickProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choisir une photo de profil");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selected);
                if (img != null) {
                    avatarImage = img;
                    avatarPanel.repaint();
                    // Persister le chemin dans le modèle utilisateur
                    currentUser.setProfilePictureUrl(selected.getAbsolutePath());
                    saveProfilePictureAsync(selected.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Impossible de lire l'image sélectionnée.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement de l'image : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Sauvegarde l'URL de la photo de profil sur le serveur en arrière-plan. */
    private void saveProfilePictureAsync(String path) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                if (userService == null) connectToServer();
                if (userService != null) {
                    currentUser.setProfilePictureUrl(path);
                    userService.updateUserRecord(currentUser);
                }
                return null;
            }
            @Override protected void done() {
                try { get(); } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    /** Affiche un dialogue de changement de mot de passe. */
    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Changer le mot de passe", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Theme.NEUTRAL);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Nouveau mot de passe");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Theme.DARK_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(14));

        form.add(fieldLbl("Mot de passe actuel"));
        form.add(Box.createVerticalStrut(4));
        AppPasswordField currentPwdField = new AppPasswordField("Mot de passe actuel");
        currentPwdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(currentPwdField);
        form.add(Box.createVerticalStrut(10));

        form.add(fieldLbl("Nouveau mot de passe (8+ car.)"));
        form.add(Box.createVerticalStrut(4));
        AppPasswordField newPwdField = new AppPasswordField("Nouveau mot de passe");
        newPwdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(newPwdField);
        form.add(Box.createVerticalStrut(14));

        JLabel statusLbl = new JLabel("");
        statusLbl.setFont(Theme.FONT_SMALL);
        statusLbl.setForeground(Theme.ERROR);
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(statusLbl);
        form.add(Box.createVerticalStrut(6));

        PrimaryButton saveBtn = new PrimaryButton("Enregistrer");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String currentPwd = currentPwdField.getText();
            String newPwd = newPwdField.getText();
            if (currentPwd.isEmpty() || newPwd.isEmpty()) {
                statusLbl.setText("Veuillez remplir tous les champs.");
                return;
            }
            if (newPwd.length() < 8) {
                statusLbl.setText("Le nouveau mot de passe doit avoir 8+ caractères.");
                return;
            }
            saveBtn.setEnabled(false);
            saveBtn.setText("Enregistrement...");
            SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    if (userService == null) connectToServer();
                    if (userService == null) throw new Exception("Serveur non disponible.");
                    // Re-login pour valider l'ancien mot de passe
                    userService.login(currentUser.getEmail(), currentPwd);
                    // Mettre à jour
                    currentUser.setPasswordHash(newPwd);
                    userService.updateUserRecord(currentUser);
                    return null;
                }
                @Override protected void done() {
                    saveBtn.setEnabled(true);
                    saveBtn.setText("Enregistrer");
                    try {
                        get();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(UserProfile.this,
                            "Mot de passe mis à jour avec succès.",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        statusLbl.setText(ErrorUtil.rootMessage(ex));
                    }
                }
            };
            w.execute();
        });
        form.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(Theme.GREY_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel infoRow(String label, String value, String badge, Color badgeColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Theme.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LIGHT_GREY),
            new EmptyBorder(12, 14, 12, 14)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(Theme.GREY_TEXT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setBackground(Theme.WHITE);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 13));
        val.setForeground(Theme.DARK_TEXT);
        right.add(val);

        if (badge != null) {
            JLabel badgeLbl = new JLabel(badge);
            badgeLbl.setFont(Theme.FONT_SMALL);
            badgeLbl.setForeground(badgeColor != null ? badgeColor : Theme.SUCCESS);
            right.add(badgeLbl);
        }
        row.add(lbl, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JLabel fieldLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(Theme.DARK_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
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
