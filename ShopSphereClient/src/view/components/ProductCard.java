/*
 * ShopSphere - ProductCard
 * Carte produit style Amazon/Temu — image, titre, prix barre, badge promo, etoiles, bouton panier
 * Nouveau composant (AgriConnect avait des lignes dans un tableau)
 */
package view.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import view.theme.Theme;

/**
 * Carte produit reutilisable — style grille Amazon/Temu.
 * @author ShopSphere
 */
public class ProductCard extends JPanel {

    private final Product product;
    private boolean hovered = false;

    public ProductCard(Product product, Consumer<Product> onAddToCart,
                       Consumer<Product> onDetails) {
        this.product = product;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Theme.CARD_W, Theme.CARD_H));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Zone image ────────────────────────────────────────────────────
        JPanel imgPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.NEUTRAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Icone produit selon la categorie
                String icon = getCategoryIcon(product.getCategory());
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
                FontMetrics fm = g2.getFontMetrics();
                int iw = fm.stringWidth(icon);
                g2.drawString(icon, (getWidth() - iw) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 4);
                // Badge PROMO
                if (product.isOnSale()) {
                    g2.setColor(Theme.ERROR);
                    g2.fillRoundRect(8, 8, 60, 20, 6, 6);
                    g2.setFont(Theme.FONT_BADGE);
                    g2.setColor(Theme.WHITE);
                    g2.drawString("PROMO", 14, 22);
                }
                g2.dispose();
            }
        };
        imgPanel.setPreferredSize(new Dimension(Theme.CARD_W, 150));
        imgPanel.setOpaque(false);
        imgPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onDetails != null) onDetails.accept(product);
            }
        });

        // ── Zone info ─────────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Theme.BG_CARD);
        info.setBorder(new EmptyBorder(8, 10, 8, 10));

        // Titre
        String titleStr = product.getTitle();
        if (titleStr.length() > 36) titleStr = titleStr.substring(0, 33) + "...";
        JLabel titleLbl = new JLabel("<html>" + titleStr + "</html>");
        titleLbl.setFont(Theme.FONT_HEADING);
        titleLbl.setForeground(Theme.DARK_TEXT);
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(4));

        // Vendeur
        if (product.getSeller() != null) {
            JLabel sellerLbl = new JLabel("Vendu par " + product.getSeller().getFullName());
            sellerLbl.setFont(Theme.FONT_SMALL);
            sellerLbl.setForeground(Theme.GREY_TEXT);
            info.add(sellerLbl);
            info.add(Box.createVerticalStrut(4));
        }

        // Prix
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pricePanel.setBackground(Theme.BG_CARD);
        if (product.isOnSale()) {
            JLabel salePrice = new JLabel(String.format("%.2f EUR", product.getSalePrice()));
            salePrice.setFont(Theme.FONT_PRICE);
            salePrice.setForeground(Theme.ERROR);
            JLabel origPrice = new JLabel(String.format("%.2f", product.getBasePrice()));
            origPrice.setFont(Theme.FONT_SMALL);
            origPrice.setForeground(Theme.GREY_TEXT);
            pricePanel.add(salePrice);
            pricePanel.add(origPrice);
        } else {
            JLabel price = new JLabel(String.format("%.2f EUR", product.getBasePrice()));
            price.setFont(Theme.FONT_PRICE);
            price.setForeground(Theme.PRIMARY);
            pricePanel.add(price);
        }
        info.add(pricePanel);
        info.add(Box.createVerticalStrut(4));

        // Stock
        JLabel stockLbl = new JLabel(product.isInStock()
                ? "✓ En stock (" + product.getStockQty() + ")"
                : "✗ Rupture de stock");
        stockLbl.setFont(Theme.FONT_SMALL);
        stockLbl.setForeground(product.isInStock() ? Theme.SUCCESS : Theme.ERROR);
        info.add(stockLbl);
        info.add(Box.createVerticalStrut(6));

        // Bouton panier
        AccentButton cartBtn = new AccentButton(
                product.isInStock() ? "🛒 Ajouter au panier" : "Indisponible",
                Theme.CARD_W - 20, 32);
        cartBtn.setEnabled(product.isInStock());
        cartBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cartBtn.addActionListener(e -> { if (onAddToCart != null) onAddToCart.accept(product); });
        info.add(cartBtn);

        add(imgPanel, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Ombre
        g2.setColor(new Color(0, 0, 0, hovered ? 25 : 12));
        g2.fillRoundRect(3, 5, getWidth()-3, getHeight()-3, 14, 14);
        // Fond blanc
        g2.setColor(Theme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 14, 14);
        // Bordure accent au survol
        if (hovered) {
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth()-3, getHeight()-3, 14, 14);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private String getCategoryIcon(Product.Category cat) {
        if (cat == null) return "📦";
        switch (cat) {
            case ELECTRONIQUE: return "📱";
            case MODE:         return "👗";
            case MAISON:       return "🏠";
            case SPORT:        return "⚽";
            case BEAUTE:       return "💄";
            case ALIMENTATION: return "🛒";
            case LIVRES:       return "📚";
            case JOUETS:       return "🎮";
            case AUTOMOBILES:  return "🚗";
            case SANTE:        return "💊";
            default:           return "📦";
        }
    }
}
