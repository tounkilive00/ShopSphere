/*
 * ShopSphere - UserProfile
 * Profil utilisateur — modifier infos, changer mot de passe, historique
 * Meme role qu'AgriConnect view/User.java
 */
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import view.components.*;
import view.theme.Theme;

public class UserProfile extends JFrame {

    private final User currentUser;

    public UserProfile(User user) {
        this.currentUser = user;
        setTitle("ShopSphere — Mon profil");
        setSize(460, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header avec avatar
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 120));

        JPanel hc = new JPanel();
        hc.setLayout(new BoxLayout(hc, BoxLayout.Y_AXIS));
        hc.setBackground(Theme.PRIMARY);

        // Avatar cercle
        JLabel avatar = new JLabel(user.isAdmin() ? "👑" : (user.isSeller() ? "🏪" : "👤")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillOval(0, 0, 56, 56);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        hc.add(avatar);
        hc.add(Box.createVerticalStrut(6));

        JLabel nameLbl = new JLabel(user.getFullName());
        nameLbl.setFont(Theme.FONT_SUBTITLE);
        nameLbl.setForeground(Theme.WHITE);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        hc.add(nameLbl);

        JLabel roleLbl = new JLabel(user.getRole().name());
        roleLbl.setFont(Theme.FONT_SMALL);
        roleLbl.setForeground(Theme.ACCENT);
        roleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        hc.add(roleLbl);

        header.add(hc);
        add(header, BorderLayout.NORTH);

        // Corps
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.NEUTRAL);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Infos
        body.add(infoRow("Email", user.getEmail()));
        body.add(infoRow("Telephone", user.getPhone() != null ? user.getPhone() : "Non renseigne"));
        body.add(infoRow("Langue", "fr".equals(user.getPreferredLanguage()) ? "Francais" : "Anglais"));
        body.add(infoRow("Email verifie", user.isEmailVerified() ? "✓ Oui" : "✗ Non"));
        body.add(Box.createVerticalStrut(20));

        // Actions
        PrimaryButton histBtn = new PrimaryButton("Historique de mes commandes");
        histBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        histBtn.addActionListener(e -> new OrderHistory(user).setVisible(true));
        body.add(histBtn);
        body.add(Box.createVerticalStrut(10));

        if (user.isSeller()) {
            AccentButton dashBtn = new AccentButton("Tableau de bord vendeur");
            dashBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
            dashBtn.addActionListener(e -> new SellerDashboard(user).setVisible(true));
            body.add(dashBtn);
            body.add(Box.createVerticalStrut(10));
        }

        SecondaryButton logoutBtn = new SecondaryButton("Se deconnecter");
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        logoutBtn.addActionListener(e -> {
            Session.logout();
            dispose();
            new UserLogin();
        });
        body.add(logoutBtn);

        add(body, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Theme.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LIGHT_GREY),
            new EmptyBorder(10, 12, 10, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_HEADING);
        lbl.setForeground(Theme.GREY_TEXT);
        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_BODY);
        val.setForeground(Theme.DARK_TEXT);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }
}
