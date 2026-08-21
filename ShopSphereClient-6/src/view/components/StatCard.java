/*
 * ShopSphere - StatCard
 * Petite carte statistique (titre + valeur, bordure accent gauche) —
 * extraite de AdminPanel.createStatCard() pour reutilisation.
 */
package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Carte statistique compacte (ex: "Total Utilisateurs : 42") avec bande de
 * couleur a gauche. Utilisee dans AdminPanel et SellerDashboard.
 * @author ShopSphere
 */
public class StatCard extends JPanel {

    private final JLabel valueLbl;

    public StatCard(String title, String initialValue, Color accentColor) {
        setLayout(new BorderLayout(0, 4));
        setBackground(Theme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(8, 12, 8, 12)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(Color.GRAY);

        valueLbl = new JLabel(initialValue);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLbl.setForeground(Theme.DARK_TEXT);

        add(titleLbl, BorderLayout.NORTH);
        add(valueLbl, BorderLayout.CENTER);
    }

    public void setValue(String v) { valueLbl.setText(v); }
    public String getValue() { return valueLbl.getText(); }
}
