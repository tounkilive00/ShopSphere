/*
 * ShopSphere - SecondaryButton
 * Bouton contour (outline) — fond blanc/transparent, bordure bleue marine.
 * Calcul dynamique de la largeur en fonction du texte pour un rendu compact.
 */
package view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import view.theme.Theme;

/**
 * Bouton secondaire ShopSphere — style outline responsive.
 * @author ShopSphere
 */
public class SecondaryButton extends JButton {

    public SecondaryButton(String text) {
        super(text);
        init(0, 36);
    }

    public SecondaryButton(String text, int width, int height) {
        super(text);
        init(width, height);
    }

    private void init(int customWidth, int customHeight) {
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setForeground(Theme.PRIMARY);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        FontMetrics fm = getFontMetrics(getFont());
        int w = (customWidth > 0) ? customWidth : Math.max(100, fm.stringWidth(getText()) + 32);
        int h = (customHeight > 0) ? customHeight : 36;
        setPreferredSize(new Dimension(w, h));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg = getModel().isPressed()  ? new Color(0xE2, 0xE8, 0xF0) :
                   getModel().isRollover() ? new Color(0xF1, 0xF5, 0xF9) :
                   Theme.WHITE;
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 14f, 14f));

        Color borderColor = getModel().isRollover() ? Theme.PRIMARY : new Color(0xCB, 0xD5, 0xE1);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 13f, 13f));

        g2.setFont(getFont());
        g2.setColor(Theme.PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(getText())) / 2;
        int y = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}

