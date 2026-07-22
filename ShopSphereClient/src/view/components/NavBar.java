/*
 * ShopSphere - NavBar
 * Barre de navigation persistante style Amazon/Temu
 * Logo | Barre de recherche globale | Panier | Profil
 */
package view.components;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Barre de navigation superieure — presente sur toutes les fenetres.
 * @author ShopSphere
 */
public class NavBar extends JPanel {

    private final JTextField searchField;
    private JLabel cartBadge;
    private int cartCount = 0;

    public NavBar(String username, Consumer<String> onSearch,
                  Runnable onCart, Runnable onProfile) {
        setLayout(new BorderLayout(12, 0));
        setBackground(Theme.PRIMARY);
        setPreferredSize(new Dimension(0, Theme.NAVBAR_H));
        setBorder(new EmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));

        // ── Logo ─────────────────────────────────────────────────────────
        JLabel logo = new JLabel("ShopSphere");
        logo.setFont(new Font("Arial", Font.BOLD, 20));
        logo.setForeground(Theme.WHITE);
        logo.setBorder(new EmptyBorder(0, 0, 0, Theme.PADDING_LG));
        add(logo, BorderLayout.WEST);

        // ── Barre de recherche ────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(0, 0));
        searchPanel.setBackground(Theme.PRIMARY);

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_BODY);
        searchField.setBackground(Theme.WHITE);
        searchField.setForeground(Theme.GREY_TEXT);
        searchField.setBorder(new EmptyBorder(6, 12, 6, 12));

        // Placeholder dynamique
        searchField.setText("Rechercher produits, marques...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().startsWith("Rechercher"))  {
                    searchField.setText("");
                    searchField.setForeground(Theme.DARK_TEXT);
                }
            }
        });
        searchField.addActionListener(e -> triggerSearch(onSearch));

        JButton searchBtn = new JButton("🔍");
        searchBtn.setBackground(Theme.ACCENT);
        searchBtn.setForeground(Theme.WHITE);
        searchBtn.setFont(Theme.FONT_BODY);
        searchBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> triggerSearch(onSearch));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn,   BorderLayout.EAST);
        add(searchPanel, BorderLayout.CENTER);

        // ── Actions droite ────────────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        actions.setBackground(Theme.PRIMARY);

        // Panier avec badge
        JPanel cartPanel = new JPanel(null);
        cartPanel.setBackground(Theme.PRIMARY);
        cartPanel.setPreferredSize(new Dimension(48, 40));

        JButton cartBtn = new JButton("🛒");
        cartBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        cartBtn.setForeground(Theme.WHITE);
        cartBtn.setBackground(Theme.PRIMARY);
        cartBtn.setBorderPainted(false);
        cartBtn.setFocusPainted(false);
        cartBtn.setContentAreaFilled(false);
        cartBtn.setBounds(0, 6, 30, 30);
        cartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (onCart != null) cartBtn.addActionListener(e -> onCart.run());

        cartBadge = new JLabel("0");
        cartBadge.setFont(Theme.FONT_BADGE);
        cartBadge.setForeground(Theme.WHITE);
        cartBadge.setBackground(Theme.ERROR);
        cartBadge.setOpaque(true);
        cartBadge.setHorizontalAlignment(SwingConstants.CENTER);
        cartBadge.setBounds(18, 2, 18, 18);
        cartBadge.setVisible(false);

        cartPanel.add(cartBtn);
        cartPanel.add(cartBadge);
        actions.add(cartPanel);

        // Profil
        String name = (username != null && !username.isEmpty())
                ? "👤 " + username : "👤 Connexion";
        JButton profileBtn = new JButton(name);
        profileBtn.setFont(Theme.FONT_BODY);
        profileBtn.setForeground(Theme.WHITE);
        profileBtn.setBackground(Theme.PRIMARY);
        profileBtn.setBorderPainted(false);
        profileBtn.setFocusPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (onProfile != null) profileBtn.addActionListener(e -> onProfile.run());
        actions.add(profileBtn);

        add(actions, BorderLayout.EAST);
    }

    private void triggerSearch(Consumer<String> onSearch) {
        String q = searchField.getText().trim();
        if (!q.isEmpty() && !q.startsWith("Rechercher") && onSearch != null) {
            onSearch.accept(q);
        }
    }

    public void updateCartBadge(int count) {
        cartCount = count;
        cartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        cartBadge.setVisible(count > 0);
        repaint();
    }

    public String getSearchText() { return searchField.getText(); }
}
