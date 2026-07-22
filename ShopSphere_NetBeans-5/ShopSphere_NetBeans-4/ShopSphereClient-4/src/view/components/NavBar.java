/*
 * ShopSphere - NavBar
 * Barre de navigation premium — degrade bleu marine, barre de recherche arrondie,
 * badge panier anime, profil avec nom utilisateur.
 */
package view.components;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Barre de navigation superieure — presente sur toutes les fenetres.
 * Design professionnel avec degrade et recherche arrondie.
 * @author ShopSphere
 */
public class NavBar extends JPanel {

    private final JTextField searchField;
    private JLabel cartBadge;

    public NavBar(String username, Consumer<String> onSearch,
                  Runnable onCart, Runnable onProfile) {

        setLayout(new BorderLayout(12, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(0, Theme.NAVBAR_H));
        setBorder(new EmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));

        // ── Logo ─────────────────────────────────────────────────────────
        JLabel logo = new JLabel("🛍 ShopSphere");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(Theme.WHITE);
        logo.setBorder(new EmptyBorder(0, 0, 0, Theme.PADDING_MD));
        add(logo, BorderLayout.WEST);

        // ── Barre de recherche ────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 4, getWidth() - 1, getHeight() - 8, 22, 22);
                g2.dispose();
            }
        };
        searchPanel.setOpaque(false);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_BODY);
        searchField.setOpaque(false);
        searchField.setForeground(Theme.GREY_TEXT);
        searchField.setBorder(new EmptyBorder(6, 14, 6, 8));
        searchField.setText("Rechercher produits, marques...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().startsWith("Rechercher")) {
                    searchField.setText("");
                    searchField.setForeground(Theme.DARK_TEXT);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher produits, marques...");
                    searchField.setForeground(Theme.GREY_TEXT);
                }
            }
        });
        searchField.addActionListener(e -> triggerSearch(onSearch));

        JButton searchBtn = new JButton("🔍") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? Theme.ACCENT.darker() : Theme.ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setOpaque(false);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(44, 32));
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> triggerSearch(onSearch));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        add(searchPanel, BorderLayout.CENTER);

        // ── Actions droite ────────────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        actions.setOpaque(false);

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

        // Profil
        String shortName = (username != null && !username.isEmpty())
                ? username.split(" ")[0]
                : "Connexion";
        JButton profileBtn = makeTextBtn("👤 " + shortName);
        if (onProfile != null) profileBtn.addActionListener(e -> onProfile.run());
        actions.add(profileBtn);

        add(actions, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), 0, new Color(0x23, 0x52, 0x7A));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Ligne inferieure decorative or
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
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(Theme.ACCENT); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setForeground(Theme.WHITE); }
        });
        return btn;
    }

    private void triggerSearch(Consumer<String> onSearch) {
        String q = searchField.getText().trim();
        if (!q.isEmpty() && !q.startsWith("Rechercher") && onSearch != null) {
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
