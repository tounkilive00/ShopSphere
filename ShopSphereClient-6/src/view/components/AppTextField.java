/*
 * ShopSphere - AppTextField
 * Meme structure qu'AgriConnect view/components/AppTextField.java
 * Champ de saisie avec placeholder, bordure coloree au focus
 */
package view.components;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Champ de saisie personnalise ShopSphere.
 * Meme pattern qu'AgriConnect AppTextField.
 * @author ShopSphere
 */
public class AppTextField extends JTextField {

    private String placeholder;
    private boolean focused = false;

    public AppTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setForeground(Theme.DARK_TEXT);
        setBackground(Theme.BG_INPUT);
        setPreferredSize(new Dimension(280, Theme.FIELD_H));
        setOpaque(true);
        applyBorder(false);

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                focused = true;
                applyBorder(true);
                repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                focused = false;
                applyBorder(false);
                repaint();
            }
        });
    }

    private void applyBorder(boolean active) {
        Color borderColor = active ? Theme.PRIMARY : new Color(0xC8, 0xD6, 0xE4);
        int thickness = active ? 2 : 1;
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, thickness, true),
            new EmptyBorder(Theme.PADDING_XS, Theme.PADDING_MD,
                            Theme.PADDING_XS, Theme.PADDING_MD)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !focused && placeholder != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Theme.GREY_TEXT);
            g2.setFont(Theme.FONT_BODY);
            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(placeholder, ins.left,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    public String getPlaceholder()           { return placeholder; }
    public void   setPlaceholder(String v)   { this.placeholder = v; repaint(); }
}
