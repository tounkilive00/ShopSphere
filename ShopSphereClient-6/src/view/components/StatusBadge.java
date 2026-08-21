/*
 * ShopSphere - StatusBadge
 * Pastille de statut coloree (style Amazon/Temu) — reutilisable pour les
 * statuts produit, commande et compte, au lieu de texte brut dans les tableaux.
 */
package view.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import view.theme.Theme;

/**
 * Petite pastille arrondie coloree pour afficher un statut de maniere lisible.
 * @author ShopSphere
 */
public class StatusBadge extends JLabel {

    private Color bg;

    public StatusBadge(String text, Color background) {
        super(text, SwingConstants.CENTER);
        this.bg = background;
        setFont(Theme.FONT_BADGE);
        setForeground(Theme.WHITE);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 10, 3, 10));
    }

    public void setBackgroundColor(Color c) { this.bg = c; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    /** Couleur associee a un statut de produit. */
    public static Color colorForProductStatus(String status) {
        if (status == null) return Theme.GREY_TEXT;
        switch (status) {
            case "ACTIF":         return Theme.SUCCESS;
            case "BROUILLON":     return Theme.GREY_TEXT;
            case "RUPTURE_STOCK": return Theme.WARNING;
            case "ARCHIVE":       return Theme.ERROR;
            default:              return Theme.GREY_TEXT;
        }
    }

    /** Couleur associee a un statut de commande. */
    public static Color colorForOrderStatus(String status) {
        if (status == null) return Theme.GREY_TEXT;
        switch (status) {
            case "EN_ATTENTE":     return Theme.STATUS_ATTENTE;
            case "PAYEE":          return Theme.STATUS_PAYEE;
            case "EN_TRAITEMENT":  return Theme.STATUS_PAYEE;
            case "EXPEDIEE":       return Theme.STATUS_EXPEDIEE;
            case "LIVREE":
            case "TERMINEE":       return Theme.STATUS_LIVREE;
            case "ANNULEE":
            case "REMBOURSEE":     return Theme.STATUS_ANNULEE;
            default:                return Theme.GREY_TEXT;
        }
    }

    /** Couleur associee a un statut de compte utilisateur. */
    public static Color colorForAccountStatus(String status) {
        if (status == null) return Theme.GREY_TEXT;
        switch (status) {
            case "ACTIVE":                return Theme.SUCCESS;
            case "SUSPENDED":             return Theme.ERROR;
            case "PENDING_VERIFICATION":  return Theme.WARNING;
            case "DELETED":               return Theme.GREY_TEXT;
            default:                       return Theme.GREY_TEXT;
        }
    }
}
