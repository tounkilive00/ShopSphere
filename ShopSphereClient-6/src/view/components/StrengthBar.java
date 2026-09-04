/*
 * ShopSphere - StrengthBar
 * Jauge de force pour les champs mot de passe.
 */
package view.components;

import java.awt.*;
import javax.swing.JPanel;
import view.theme.Theme;

/**
 * Jauge segmentée indiquant la force d'un mot de passe (0–4).
 * @author ShopSphere
 */
public class StrengthBar extends JPanel {

    private int strength = 0;

    public StrengthBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 8));
    }

    public void setStrength(int s) {
        this.strength = Math.max(0, Math.min(4, s));
        repaint();
    }

    public int getStrength() { return strength; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int segW = (getWidth() - 6) / 4; // 3px gap total for 4 segments
        Color[] colors = { Theme.ERROR, Theme.WARNING, Theme.ACCENT, Theme.SUCCESS };
        for (int i = 0; i < 4; i++) {
            Color c = (i < strength) ? colors[i] : Theme.LIGHT_GREY;
            g2.setColor(c);
            g2.fillRoundRect(i * (segW + 2), 0, segW, getHeight(), 4, 4);
        }
        g2.dispose();
    }
}
