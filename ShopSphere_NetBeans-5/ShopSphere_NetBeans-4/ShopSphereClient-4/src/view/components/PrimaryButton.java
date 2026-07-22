/*
 * ShopSphere - PrimaryButton
 * Bouton principal premium avec degrade bleu marine + effet hover lumineux
 */
package view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import view.theme.Theme;

/**
 * Bouton primaire ShopSphere — degrade bleu marine, bords arrondis.
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
        setFont(new Font("SansSerif", Font.BOLD, 13));
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

        if (!isEnabled()) {
            g2.setColor(Theme.LIGHT_GREY);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16f, 16f));
            g2.setFont(getFont());
            g2.setColor(Theme.GREY_TEXT);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
            return;
        }

        // Degrade bleu marine
        Color top    = getModel().isPressed()  ? new Color(0x0D, 0x24, 0x3C) :
                       getModel().isRollover() ? new Color(0x2A, 0x62, 0x8F) :
                       Theme.PRIMARY;
        Color bottom = getModel().isPressed()  ? Theme.PRIMARY :
                       getModel().isRollover() ? Theme.PRIMARY :
                       new Color(0x0D, 0x28, 0x45);

        GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16f, 16f));

        // Lueur interne
        if (getModel().isRollover()) {
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() / 2f, 14f, 14f));
        }

        // Texte
        g2.setFont(getFont());
        g2.setColor(Theme.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
