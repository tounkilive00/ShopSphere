/*
 * ShopSphere - StatCard
 * Carte statistique moderne (titre + valeur, bande accent à gauche, coins arrondis).
 */
package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Carte statistique compacte (ex: "Total Utilisateurs : 42") avec bande de
 * couleur à gauche et coins arrondis.
 * @author ShopSphere
 */
public class StatCard extends JPanel {

    private final JLabel valueLbl;
    private final Color accentColor;

    public StatCard(String title, String initialValue, Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 16, 10, 14));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(0x64, 0x74, 0x8B));

        valueLbl = new JLabel(initialValue);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(Theme.DARK_TEXT);

        add(titleLbl, BorderLayout.NORTH);
        add(valueLbl, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fond blanc de la carte
        g2.setColor(Theme.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);

        // Bande de couleur à gauche
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 6, h - 1, 12, 12);
        g2.fillRect(3, 0, 3, h - 1);

        // Contour subtil
        g2.setColor(new Color(0xE2, 0xE8, 0xF0));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

        g2.dispose();
    }

    public void setValue(String v) { valueLbl.setText(v); }
    public String getValue() { return valueLbl.getText(); }
}

