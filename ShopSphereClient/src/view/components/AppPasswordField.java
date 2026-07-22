/*
 * ShopSphere - AppPasswordField
 * Meme structure qu'AgriConnect view/components/AppPasswordField.java
 * Champ mot de passe avec afficher/masquer toggle
 */
package view.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Champ mot de passe personnalise avec bouton afficher/masquer.
 * Meme pattern qu'AgriConnect AppPasswordField.
 * @author ShopSphere
 */
public class AppPasswordField extends JPanel {

    private final JPasswordField passwordField;
    private final JToggleButton  toggleBtn;
    private boolean focused = false;

    public AppPasswordField(String placeholder) {
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(280, Theme.FIELD_H));
        setBackground(Theme.BG_INPUT);
        applyBorder(false);

        passwordField = new JPasswordField();
        passwordField.setFont(Theme.FONT_BODY);
        passwordField.setForeground(Theme.DARK_TEXT);
        passwordField.setBackground(Theme.BG_INPUT);
        passwordField.setBorder(new EmptyBorder(0, Theme.PADDING_MD, 0, 0));
        passwordField.setOpaque(false);

        // Placeholder simulation
        passwordField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                focused = true; applyBorder(true);
            }
            @Override public void focusLost(FocusEvent e) {
                focused = false; applyBorder(false);
            }
        });

        // Toggle afficher / masquer
        toggleBtn = new JToggleButton("👁");
        toggleBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        toggleBtn.setBackground(Theme.BG_INPUT);
        toggleBtn.setBorder(new EmptyBorder(0, 6, 0, 10));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.addActionListener(e -> {
            if (toggleBtn.isSelected()) {
                passwordField.setEchoChar((char) 0); // afficher
                toggleBtn.setText("🙈");
            } else {
                passwordField.setEchoChar('•'); // masquer
                toggleBtn.setText("👁");
            }
        });

        add(passwordField, BorderLayout.CENTER);
        add(toggleBtn,     BorderLayout.EAST);
    }

    private void applyBorder(boolean active) {
        Color borderColor = active ? Theme.PRIMARY : Theme.LIGHT_GREY;
        int thickness = active ? 2 : 1;
        setBorder(BorderFactory.createLineBorder(borderColor, thickness, true));
    }

    public char[] getPassword()  { return passwordField.getPassword(); }
    public String getText()      { return new String(passwordField.getPassword()); }
    public void   setText(String v){ passwordField.setText(v); }
    public void   addActionListener(ActionListener l){ passwordField.addActionListener(l); }
}
