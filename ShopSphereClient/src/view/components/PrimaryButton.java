/*
 * ShopSphere - PrimaryButton
 * Meme structure qu'AgriConnect view/components/PrimaryButton.java
 * Bouton principal avec fond bleu marine et coins arrondis
 */
package view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import view.theme.Theme;

/**
 * Bouton primaire ShopSphere — fond bleu marine.
 * Meme pattern qu'AgriConnect PrimaryButton.
 * @author ShopSphere
 */
public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {
        super(text);
        init();
    }

    public PrimaryButton(String text, int width, int height) {
        super(text);
        setPreferredSize(new Dimension(width, height));
        init();
    }

    private void init() {
        setFont(Theme.FONT_BUTTON);
        setForeground(Theme.WHITE);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (getPreferredSize().width == 0)
            setPreferredSize(new Dimension(200, Theme.BTN_H));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = getModel().isPressed()  ? Theme.PRIMARY.darker() :
                   getModel().isRollover() ? new Color(0x23, 0x52, 0x7A) :
                   Theme.PRIMARY;

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                Theme.BORDER_RADIUS * 2f, Theme.BORDER_RADIUS * 2f));

        // Texte centre
        g2.setFont(getFont());
        g2.setColor(Theme.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth()  - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
