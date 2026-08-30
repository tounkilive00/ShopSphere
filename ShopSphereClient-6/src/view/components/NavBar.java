/*
 * ShopSphere - NavBar
 * Barre de navigation premium — dégradé bleu marine, barre de recherche arrondie,
 * badge panier animé, avatar circulaire avec photo de profil.
 * Performance : debounce sur la recherche (300 ms) pour limiter les appels RMI.
 */
package view.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import view.AdminPanel;
import view.theme.Theme;

/**
 * Barre de navigation supérieure de ShopSphere.
 * Inclut un avatar circulaire (photo de profil ou initiales),
 * un champ de recherche avec debounce et un badge panier animé.
 * @author ShopSphere
 */
public class NavBar extends JPanel {

    private final JTextField searchField;
    private JLabel           cartBadge;
    private JPanel           avatarPanel;
    private BufferedImage    avatarImage;
    private String           initials = "?";

    /** Timer pour debounce de la recherche (évite un appel RMI à chaque frappe). */
    private Timer debounceTimer;

    public NavBar(String username, Consumer<String> onSearch,
                  Runnable onCart, Runnable onProfile) {
        this(username, null, onSearch, onCart, onProfile);
    }

    public NavBar(String username, User user, Consumer<String> onSearch,
                  Runnable onCart, Runnable onProfile) {

        setLayout(new BorderLayout(12, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(0, Theme.NAVBAR_H));
        setBorder(new EmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));

        // Préparer initiales et avatar
        if (username != null && !username.isEmpty()) {
            String[] parts = username.trim().split("\\s+");
            initials = parts.length == 1
                    ? parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase()
                    : ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        if (user != null) {
            loadAvatarImage(user.getProfilePictureUrl());
        }

        // ── Logo ─────────────────────────────────────────────────────────
        JLabel logo = new JLabel("🛍 ShopSphere");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(Theme.WHITE);
        logo.setBorder(new EmptyBorder(0, 0, 0, Theme.PADDING_MD));
        add(logo, BorderLayout.WEST);

        // ── Barre de recherche avec debounce ──────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 4, getWidth() - 1, getHeight() - 8, 24, 24);
                g2.dispose();
            }
        };
        searchPanel.setOpaque(false);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_BODY);
        searchField.setOpaque(false);
        searchField.setForeground(Theme.GREY_TEXT);
        searchField.setBorder(new EmptyBorder(6, 16, 6, 8));
        searchField.setText("Rechercher produits, marques...");

        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (searchField.getText().startsWith("Rechercher")) {
                    searchField.setText("");
                    searchField.setForeground(Theme.DARK_TEXT);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher produits, marques...");
                    searchField.setForeground(Theme.GREY_TEXT);
                }
            }
        });

        // Debounce : attendre 300 ms après la dernière frappe avant de chercher
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { scheduleSearch(onSearch); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { scheduleSearch(onSearch); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { scheduleSearch(onSearch); }
        });
        searchField.addActionListener(e -> triggerSearch(onSearch)); // Enter = immédiat

        JButton searchBtn = new JButton("Rechercher") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? Theme.ACCENT.darker() : Theme.ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setOpaque(false);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(100, 32));
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> triggerSearch(onSearch));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        add(searchPanel, BorderLayout.CENTER);

        // ── Actions droite ────────────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        actions.setOpaque(false);

        // Bouton Panneau d'administration si l'utilisateur est administrateur
        if (user != null && user.isAdmin()) {
            JButton adminBtn = new JButton("Administration") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = getModel().isRollover() ? Theme.ACCENT_DARK : Theme.ACCENT;
                    g2.setColor(bg);
                    g2.fillRoundRect(0, (getHeight() - 30) / 2, getWidth(), 30, 14, 14);
                    g2.setFont(Theme.FONT_LABEL);
                    g2.setColor(Theme.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            };
            adminBtn.setOpaque(false);
            adminBtn.setContentAreaFilled(false);
            adminBtn.setBorderPainted(false);
            adminBtn.setFocusPainted(false);
            adminBtn.setPreferredSize(new Dimension(135, Theme.NAVBAR_H));
            adminBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            adminBtn.setToolTipText("Ouvrir le panneau d'administration");
            adminBtn.addActionListener(e -> AppNavigator.show(new AdminPanel(user)));
            actions.add(adminBtn);
        }

        // Panier avec badge
        JPanel cartPanel = new JPanel(null);
        cartPanel.setOpaque(false);
        cartPanel.setPreferredSize(new Dimension(52, Theme.NAVBAR_H));

        JButton cartBtn = makeIconBtn("🛒", 22);
        cartBtn.setBounds(4, (Theme.NAVBAR_H - 32) / 2, 32, 32);
        if (onCart != null) cartBtn.addActionListener(e -> onCart.run());

        cartBadge = new JLabel("0");
        cartBadge.setFont(new Font("SansSerif", Font.BOLD, 9));
        cartBadge.setForeground(Theme.WHITE);
        cartBadge.setBackground(Theme.ERROR);
        cartBadge.setOpaque(true);
        cartBadge.setHorizontalAlignment(SwingConstants.CENTER);
        cartBadge.setBounds(22, (Theme.NAVBAR_H - 32) / 2 - 2, 18, 16);
        cartBadge.setVisible(false);

        cartPanel.add(cartBtn);
        cartPanel.add(cartBadge);
        actions.add(cartPanel);

        // Avatar profil circulaire
        avatarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                int s = 36;
                int y = (getHeight() - s) / 2;
                // Bordure or au survol
                g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(),
                        Theme.ACCENT.getBlue(), getModel().isRollover() ? 255 : 150));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(0, y, s, s);
                if (avatarImage != null) {
                    Shape clip = new Ellipse2D.Float(1, y + 1, s - 2, s - 2);
                    g2.setClip(clip);
                    g2.drawImage(avatarImage, 1, y + 1, s - 2, s - 2, null);
                    g2.setClip(null);
                } else {
                    g2.setColor(new Color(0x1A, 0x50, 0x80));
                    g2.fillOval(1, y + 1, s - 2, s - 2);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(initials,
                            (s - fm.stringWidth(initials)) / 2,
                            y + (s + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
            }
            // Expose rollover model
            private ButtonModel getModel() {
                // Accessible via mouse state
                return new DefaultButtonModel();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(44, Theme.NAVBAR_H); }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatarPanel.setToolTipText("Mon profil");
        avatarPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (onProfile != null) onProfile.run(); }
            @Override public void mouseEntered(MouseEvent e) { avatarPanel.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { avatarPanel.repaint(); }
        });
        actions.add(avatarPanel);

        // Nom utilisateur
        String shortName = (username != null && !username.isEmpty())
                ? username.split(" ")[0] : "Connexion";
        JButton profileNameBtn = makeTextBtn(shortName);
        profileNameBtn.addActionListener(e -> { if (onProfile != null) onProfile.run(); });
        actions.add(profileNameBtn);

        add(actions, BorderLayout.EAST);
    }

    /** Charge l'image de profil depuis le chemin local. */
    public void loadAvatarImage(String path) {
        if (path == null || path.isEmpty()) { avatarImage = null; return; }
        try {
            File f = new File(path);
            if (f.exists()) avatarImage = ImageIO.read(f);
        } catch (IOException ignored) { avatarImage = null; }
        if (avatarPanel != null) avatarPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), 0, new Color(0x23, 0x52, 0x7A));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Ligne inférieure dorée
        g2.setColor(Theme.ACCENT);
        g2.fillRect(0, getHeight() - 2, getWidth(), 2);
        g2.dispose();
    }

    private JButton makeIconBtn(String icon, int fontSize) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        btn.setForeground(Theme.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeTextBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(Theme.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(Theme.ACCENT); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(Theme.WHITE); }
        });
        return btn;
    }

    /** Lance la recherche avec un délai de 300 ms (debounce). */
    private void scheduleSearch(Consumer<String> onSearch) {
        if (debounceTimer != null && debounceTimer.isRunning()) debounceTimer.stop();
        debounceTimer = new Timer(300, e -> triggerSearch(onSearch));
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }

    private void triggerSearch(Consumer<String> onSearch) {
        String q = searchField.getText().trim();
        if (!q.startsWith("Rechercher") && onSearch != null) {
            onSearch.accept(q);
        }
    }

    public void updateCartBadge(int count) {
        cartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        cartBadge.setVisible(count > 0);
        repaint();
    }

    public String getSearchText() { return searchField.getText(); }
}
