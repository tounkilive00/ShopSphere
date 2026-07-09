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
        setFont(Theme.FONT_BUTTON);
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
        Color bg = getModel().isPressed()  ? Theme.NEUTRAL :
                   getModel().isRollover() ? Theme.LIGHT_GREY :
                   Theme.WHITE;
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                Theme.BORDER_RADIUS * 2f, Theme.BORDER_RADIUS * 2f));
        g2.setColor(getModel().isRollover() ? Theme.PRIMARY : Theme.LIGHT_GREY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2,
                Theme.BORDER_RADIUS * 2f, Theme.BORDER_RADIUS * 2f));
        g2.setFont(getFont());
        g2.setColor(Theme.PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth()  - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
