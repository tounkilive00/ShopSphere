/*
 * ShopSphere - PrimaryButton v2.0
 * Bouton principal premium — dégradé bleu marine, animation scale au clic,
 * état loading, icône optionnelle, ripple effect subtil.
 */
package view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import view.theme.Theme;

/**
 * Bouton primaire ShopSphere v2 — design moderne avec micro-animations.
 * @author ShopSphere
 */
public class PrimaryButton extends JButton {

    private float   rippleAlpha = 0f;
    private boolean loading     = false;
    private String  loadingText = "Chargement...";
    private float   scale       = 1.0f;
    private Timer   rippleTimer;

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
        setPreferredSize(new Dimension(200, Theme.BTN_H));

        // Ripple on press
        addChangeListener(e -> {
            if (getModel().isPressed()) startRipple();
        });

        // Scale animation on press
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e)  {
                animateScale(0.96f, 60);
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                animateScale(1.0f,  80);
            }
        });
    }

    /** Active l'état "loading" (désactive le bouton et affiche un spinner textuel). */
    public void setLoading(boolean loading, String text) {
        this.loading     = loading;
        this.loadingText = text != null ? text : "Chargement...";
        setEnabled(!loading);
        repaint();
    }

    private void startRipple() {
        rippleAlpha = 0.35f;
        if (rippleTimer != null) rippleTimer.stop();
        rippleTimer = new Timer(25, null);
        rippleTimer.addActionListener(e -> {
            rippleAlpha -= 0.04f;
            if (rippleAlpha <= 0) { rippleAlpha = 0; ((Timer)e.getSource()).stop(); }
            repaint();
        });
        rippleTimer.start();
    }

    private void animateScale(float target, int durationMs) {
        float start = scale;
        int steps   = durationMs / 16;
        final int[] step = {0};
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            step[0]++;
            float p = Math.min(1f, (float) step[0] / steps);
            scale = start + (target - start) * p;
            repaint();
            if (step[0] >= steps) ((Timer) e.getSource()).stop();
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        // Apply scale transform around center
        if (scale != 1.0f) {
            int cx = w / 2, cy = h / 2;
            g2.translate(cx, cy);
            g2.scale(scale, scale);
            g2.translate(-cx, -cy);
        }

        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1,
                Theme.BORDER_RADIUS_LG, Theme.BORDER_RADIUS_LG);

        if (!isEnabled()) {
            g2.setColor(Theme.LIGHT_GREY);
            g2.fill(shape);
            drawText(g2, loadingText.equals("Chargement...") && loading ? loadingText : getText(),
                    Theme.GREY_TEXT, w, h);
            g2.dispose();
            return;
        }

        // Gradient fill
        Color top    = getModel().isRollover() ? Theme.PRIMARY_LIGHT : Theme.PRIMARY;
        Color bottom = getModel().isRollover() ? Theme.PRIMARY       : Theme.PRIMARY_DARK;
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fill(shape);

        // Sheen highlight (top inner glow)
        g2.setColor(new Color(255, 255, 255, 20));
        g2.fillRoundRect(1, 1, w - 2, h / 2, Theme.BORDER_RADIUS_LG - 2, Theme.BORDER_RADIUS_LG - 2);

        // Ripple overlay
        if (rippleAlpha > 0) {
            g2.setColor(new Color(255, 255, 255, (int)(rippleAlpha * 255)));
            g2.fill(shape);
        }

        // Focus ring
        if (isFocusOwner()) {
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Float(2, 2, w - 5, h - 5,
                    Theme.BORDER_RADIUS_LG - 2, Theme.BORDER_RADIUS_LG - 2));
        }

        String label = loading ? loadingText : getText();
        drawText(g2, label, Theme.WHITE, w, h);
        g2.dispose();
    }

    private void drawText(Graphics2D g2, String text, Color color, int w, int h) {
        g2.setFont(getFont());
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (w - fm.stringWidth(text)) / 2,
                (h + fm.getAscent() - fm.getDescent()) / 2);
    }
}
