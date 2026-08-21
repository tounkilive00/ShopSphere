/*
 * ShopSphere - GradientPanel
 * Panneau a fond degrade reutilisable — remplace les classes anonymes
 * dupliquees dans chaque vue (AdminPanel, SellerDashboard, MarketPlace...).
 */
package view.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import view.theme.Theme;

/**
 * JPanel a fond degrade horizontal, utilise pour les bandeaux d'en-tete.
 * @author ShopSphere
 */
public class GradientPanel extends JPanel {

    private Color startColor;
    private Color endColor;
    private boolean vertical;

    public GradientPanel() {
        this(Theme.PRIMARY, new Color(0x23, 0x52, 0x7A), false);
    }

    public GradientPanel(Color startColor, Color endColor) {
        this(startColor, endColor, false);
    }

    public GradientPanel(Color startColor, Color endColor, boolean vertical) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.vertical = vertical;
        setOpaque(false);
    }

    public void setColors(Color start, Color end) {
        this.startColor = start;
        this.endColor = end;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = vertical
                ? new GradientPaint(0, 0, startColor, 0, getHeight(), endColor)
                : new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
