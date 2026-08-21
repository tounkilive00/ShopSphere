/*
 * ShopSphere - PageHeader
 * Bandeau d'en-tete reutilisable (degrade + titre + actions a droite).
 * Remplace ~20 lignes dupliquees dans chaque vue par un seul appel.
 */
package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * En-tete de page standard ShopSphere : degrade bleu marine, titre (+ sous-titre
 * optionnel) a gauche, boutons d'action a droite.
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
        setPreferredSize(new Dimension(0, subtitle != null ? 72 : 64));
        setBorder(new EmptyBorder(0, 24, 0, 20));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setBorder(new EmptyBorder(0, 0, 0, 0));

        titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLbl.setForeground(Theme.WHITE);
        titleLbl.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(titleLbl);

        subtitleLbl = new JLabel(subtitle != null ? subtitle : "");
        subtitleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLbl.setForeground(new Color(0xCE, 0xDD, 0xEC));

        if (subtitle != null) {
            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.add(titleRow);
            wrap.add(subtitleLbl);
            add(wrap, BorderLayout.WEST);
        } else {
            add(titleRow, BorderLayout.WEST);
        }

        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        add(actionsPanel, BorderLayout.EAST);
    }

    /** Ajoute un bouton (ou tout composant) a droite de l'en-tete. */
    public PageHeader addAction(java.awt.Component c) {
        actionsPanel.add(c);
        return this;
    }

    public void setTitleText(String text) { titleLbl.setText(text); }
    public void setSubtitleText(String text) { subtitleLbl.setText(text); }
}
