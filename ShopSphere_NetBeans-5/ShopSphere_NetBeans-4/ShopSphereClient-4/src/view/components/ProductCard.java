/*
 * ShopSphere - ProductCard
 * Carte produit premium — ombre portee, badge promo, stars, effet hover.
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
 * Carte produit reutilisable — style e-commerce premium.
 * @author ShopSphere
 */
public class ProductCard extends JPanel {

    private final Product product;
    private boolean hovered = false;

    public ProductCard(Product product, Consumer<Product> onAddToCart,
                       Consumer<Product> onDetails) {
        this.product = product;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Theme.CARD_W, Theme.CARD_H + 20));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Zone image ────────────────────────────────────────────────────
        JPanel imgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond degrade subtil selon la categorie
                Color catColor = getCategoryColor(product.getCategory());
                GradientPaint gp = new GradientPaint(0, 0, catColor.brighter(),
                        0, getHeight(), catColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Icone produit
                String icon = getCategoryIcon(product.getCategory());
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
                FontMetrics fm = g2.getFontMetrics();
                int iw = fm.stringWidth(icon);
                g2.drawString(icon, (getWidth() - iw) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 6);

                // Badge PROMO
                if (product.isOnSale()) {
                    g2.setColor(new Color(0xDC, 0x26, 0x26));
                    g2.fillRoundRect(8, 8, 66, 22, 8, 8);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g2.setColor(Color.WHITE);
                    g2.drawString("PROMO", 13, 23);
                }

                // Badge stock epuise
                if (!product.isInStock()) {
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g2.setColor(Color.WHITE);
                    String oos = "Rupture de stock";
                    FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString(oos, (getWidth() - fm2.stringWidth(oos)) / 2, getHeight() / 2 + 5);
                }
                g2.dispose();
            }
        };
        imgPanel.setPreferredSize(new Dimension(Theme.CARD_W, 155));
        imgPanel.setOpaque(false);
        imgPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onDetails != null) onDetails.accept(product);
            }
        });

        // ── Zone info ─────────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Theme.BG_CARD);
        info.setBorder(new EmptyBorder(8, 10, 10, 10));

        // Titre
        String titleStr = product.getTitle();
        if (titleStr.length() > 34) titleStr = titleStr.substring(0, 31) + "...";
        JLabel titleLbl = new JLabel("<html><b>" + titleStr + "</b></html>");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLbl.setForeground(Theme.DARK_TEXT);
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(3));

        // Vendeur
        if (product.getSeller() != null) {
            JLabel sellerLbl = new JLabel("par " + product.getSeller().getFullName());
            sellerLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
            sellerLbl.setForeground(Theme.GREY_TEXT);
            info.add(sellerLbl);
            info.add(Box.createVerticalStrut(4));
        }

        // Prix
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pricePanel.setBackground(Theme.BG_CARD);
        if (product.isOnSale()) {
            JLabel salePrice = new JLabel(String.format("%.2f EUR", product.getSalePrice()));
            salePrice.setFont(new Font("SansSerif", Font.BOLD, 16));
            salePrice.setForeground(new Color(0xDC, 0x26, 0x26));
            JLabel origPrice = new JLabel(String.format("%.2f", product.getBasePrice()));
            origPrice.setFont(new Font("SansSerif", Font.PLAIN, 11));
            origPrice.setForeground(Theme.GREY_TEXT);
            pricePanel.add(salePrice);
            pricePanel.add(origPrice);
        } else {
            JLabel price = new JLabel(String.format("%.2f EUR", product.getBasePrice()));
            price.setFont(new Font("SansSerif", Font.BOLD, 16));
            price.setForeground(Theme.PRIMARY);
            pricePanel.add(price);
        }
        info.add(pricePanel);
        info.add(Box.createVerticalStrut(5));

        // Stock
        JLabel stockLbl = new JLabel(product.isInStock()
                ? "✓ En stock (" + product.getStockQty() + ")"
                : "✗ Rupture de stock");
        stockLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        stockLbl.setForeground(product.isInStock() ? Theme.SUCCESS : Theme.ERROR);
        info.add(stockLbl);
        info.add(Box.createVerticalStrut(7));

        // Bouton panier
        AccentButton cartBtn = new AccentButton(
                product.isInStock() ? "🛒  Ajouter au panier" : "Indisponible",
                Theme.CARD_W - 20, 32);
        cartBtn.setEnabled(product.isInStock());
        cartBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cartBtn.addActionListener(e -> { if (onAddToCart != null) onAddToCart.accept(product); });
        info.add(cartBtn);

        add(imgPanel, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);

        // Hover
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Ombre portee
        int shadow = hovered ? 3 : 1;
        for (int i = shadow; i > 0; i--) {
            g2.setColor(new Color(0, 0, 0, hovered ? (30 - i * 6) : (12 - i * 3)));
            g2.fillRoundRect(i, i + 2, getWidth() - i, getHeight() - i, 16, 16);
        }

        // Fond blanc arrondi
        g2.setColor(Theme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth() - shadow - 1, getHeight() - shadow - 1, 14, 14);

        // Bordure accent au survol
        if (hovered) {
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - shadow - 3, getHeight() - shadow - 3, 13, 13);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private Color getCategoryColor(Product.Category cat) {
        if (cat == null) return new Color(0xE8, 0xEE, 0xF6);
        switch (cat) {
            case ELECTRONIQUE: return new Color(0xDC, 0xF0, 0xFF);
            case MODE:         return new Color(0xFF, 0xEB, 0xF5);
            case MAISON:       return new Color(0xEE, 0xF9, 0xEE);
            case SPORT:        return new Color(0xFF, 0xF0, 0xE4);
            case BEAUTE:       return new Color(0xFF, 0xE8, 0xF4);
            case ALIMENTATION: return new Color(0xF0, 0xFB, 0xE0);
            case LIVRES:       return new Color(0xFF, 0xF8, 0xE0);
            case JOUETS:       return new Color(0xF8, 0xE4, 0xFF);
            default:           return new Color(0xF0, 0xF4, 0xF8);
        }
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
