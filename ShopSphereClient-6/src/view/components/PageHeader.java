/*
 * ShopSphere - PageHeader
 * Bandeau d'en-tête réutilisable (dégradé + titre + sous-titre + actions à droite).
 */
package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * En-tête de page standard ShopSphere : dégradé bleu marine, titre (+ sous-titre
 * optionnel) à gauche, boutons d'action à droite.
 * @author ShopSphere
 */
public class PageHeader extends GradientPanel {

    private final JLabel titleLbl;
    private final JLabel subtitleLbl;
    private final JPanel actionsPanel;

    public PageHeader(String title) {
        this(title, null);
    }

    public PageHeader(String title, String subtitle) {
        super(Theme.PRIMARY, new Color(0x23, 0x52, 0x7A));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, subtitle != null ? 76 : 64));
        setBorder(new EmptyBorder(10, 24, 10, 24));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Theme.WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLbl = new JLabel(subtitle != null ? subtitle : "");
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLbl.setForeground(new Color(0xD0, 0xDF, 0xEE));
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBlock.add(Box.createVerticalGlue());
        textBlock.add(titleLbl);
        if (subtitle != null && !subtitle.isEmpty()) {
            textBlock.add(Box.createRigidArea(new Dimension(0, 3)));
            textBlock.add(subtitleLbl);
        }
        textBlock.add(Box.createVerticalGlue());

        add(textBlock, BorderLayout.WEST);

        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionsPanel.setOpaque(false);
        add(actionsPanel, BorderLayout.EAST);
    }

    /** Ajoute un bouton (ou tout composant) à droite de l'en-tête. */
    public PageHeader addAction(java.awt.Component c) {
        actionsPanel.add(c);
        return this;
    }

    public void setTitleText(String text) { titleLbl.setText(text); }
    public void setSubtitleText(String text) { subtitleLbl.setText(text); }
}

