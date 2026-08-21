/*
 * ShopSphere - SecondaryButton
 * Meme structure qu'AgriConnect view/components/SecondaryButton.java
 * Bouton contour (outline) — fond transparent, bordure bleue marine
 */
package view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import view.theme.Theme;

/**
 * Bouton secondaire ShopSphere — style outline.
 * Meme pattern qu'AgriConnect SecondaryButton.
 * @author ShopSphere
 */
public class SecondaryButton extends JButton {

    public SecondaryButton(String text) {
        super(text);
        setFont(new Font("SansSerif", Font.BOLD, 13));
        setForeground(Theme.PRIMARY);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(200, Theme.BTN_H));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = getModel().isPressed()  ? new Color(0xE8, 0xEF, 0xF6) :
                   getModel().isRollover() ? new Color(0xF0, 0xF5, 0xFF) :
                   Theme.WHITE;
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16f, 16f));
        Color borderColor = getModel().isRollover() ? Theme.PRIMARY : new Color(0xC0, 0xCE, 0xE0);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 15f, 15f));
        g2.setFont(getFont());
        g2.setColor(Theme.PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth()  - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
