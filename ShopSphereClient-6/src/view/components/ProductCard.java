/*
 * ShopSphere - ProductCard v2.0
 * Carte produit premium : glassmorphisme léger, animations de survol fluides,
 * badge discount dynamique, étoiles de notation, quick-actions.
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
 * Carte produit e-commerce premium v2.
 * @author ShopSphere
 */
public class ProductCard extends JPanel {

    private final Product product;
    private float hoverProgress = 0f;  // 0.0 → 1.0 pour animation fluide
    private Timer hoverTimer;

    public ProductCard(Product product, Consumer<Product> onAddToCart,
                       Consumer<Product> onDetails) {
        this.product = product;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Theme.CARD_W, Theme.CARD_H + 30));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Zone image ────────────────────────────────────────────────────
        JPanel imgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color catColor = getCategoryColor(product.getCategory());
                Color catDark  = catColor.darker();

                // Dégradé diagonal
                GradientPaint gp = new GradientPaint(0, 0, catColor,
                        getWidth(), getHeight(), catDark);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Pattern géométrique subtil
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 60, -20, 90, 90);
                g2.fillOval(-20, getHeight() - 50, 70, 70);

                // Image du produit (URL web ou fichier local) si fournie par le vendeur
                boolean imageDrawn = false;
                if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
                    try {
                        String urlStr = product.getImageUrl().trim();
                        Image img = null;
                        if (urlStr.startsWith("http://") || urlStr.startsWith("https://")) {
                            img = javax.imageio.ImageIO.read(new java.net.URL(urlStr));
                        } else {
                            java.io.File file = new java.io.File(urlStr);
                            if (file.exists()) {
                                img = javax.imageio.ImageIO.read(file);
                            }
                        }
                        if (img != null) {
                            Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.setClip(clip);
                            g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                            g2.setClip(null);
                            imageDrawn = true;
                        }
                    } catch (Exception ignored) {}
                }

                if (!imageDrawn) {
                    // Fallback : Icone emoji produit par catégorie
                    String icon = getCategoryIcon(product.getCategory());
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                    FontMetrics fm = g2.getFontMetrics();
                    int iw = fm.stringWidth(icon);
                    g2.drawString(icon, (getWidth() - iw) / 2,
                            getHeight() / 2 + fm.getAscent() / 2 - 4);
                }

                // Badge PROMO avec discount %
                if (product.isOnSale() && product.getBasePrice() > 0) {
                    int discount = (int) ((1 - product.getSalePrice() / product.getBasePrice()) * 100);
                    String badge = "-" + discount + "%";
                    g2.setColor(new Color(0xDC, 0x26, 0x26));
                    g2.fillRoundRect(8, 8, 52, 24, 8, 8);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(Color.WHITE);
                    FontMetrics bfm = g2.getFontMetrics();
                    g2.drawString(badge, 8 + (52 - bfm.stringWidth(badge)) / 2, 24);
                }

                // Overlay rupture de stock
                if (!product.isInStock()) {
                    g2.setColor(new Color(0, 0, 0, 110));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                    g2.setColor(Color.WHITE);
                    String oos = "Rupture de stock";
                    FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString(oos, (getWidth() - fm2.stringWidth(oos)) / 2,
                            getHeight() / 2 + 6);
                }

                // Hover overlay
                if (hoverProgress > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverProgress * 20)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
            }
        };
        imgPanel.setPreferredSize(new Dimension(Theme.CARD_W, 150));
        imgPanel.setOpaque(false);
        imgPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imgPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onDetails != null) onDetails.accept(product);
            }
        });

        // ── Zone info ─────────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(10, 12, 12, 12));

        // Catégorie pill
        if (product.getCategory() != null) {
            JLabel catLbl = new JLabel(product.getCategory().name());
            catLbl.setFont(Theme.FONT_CAPTION);
            catLbl.setForeground(Theme.PRIMARY);
            catLbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.LIGHT_GREY, 1, true),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            catLbl.setOpaque(true);
            catLbl.setBackground(Theme.NEUTRAL);
            catLbl.setAlignmentX(LEFT_ALIGNMENT);
            info.add(catLbl);
            info.add(Box.createVerticalStrut(5));
        }

        // Titre
        String titleStr = product.getTitle();
        if (titleStr != null && titleStr.length() > 32) titleStr = titleStr.substring(0, 29) + "…";
        JLabel titleLbl = new JLabel("<html><b>" + titleStr + "</b></html>");
        titleLbl.setFont(Theme.FONT_HEADING);
        titleLbl.setForeground(Theme.DARK_TEXT);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(2));

        // Vendeur
        if (product.getSeller() != null) {
            JLabel sellerLbl = new JLabel("par " + product.getSeller().getFullName());
            sellerLbl.setFont(Theme.FONT_SMALL);
            sellerLbl.setForeground(Theme.GREY_TEXT);
            sellerLbl.setAlignmentX(LEFT_ALIGNMENT);
            info.add(sellerLbl);
            info.add(Box.createVerticalStrut(6));
        }

        // Prix
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pricePanel.setOpaque(false);
        pricePanel.setAlignmentX(LEFT_ALIGNMENT);
        if (product.isOnSale()) {
            JLabel saleLbl = new JLabel(String.format("%.2f FCFA", product.getSalePrice()));
            saleLbl.setFont(Theme.FONT_PRICE);
            saleLbl.setForeground(Theme.ERROR);
            JLabel baseLbl = new JLabel("<html><strike>" +
                    String.format("%.0f FCFA", product.getBasePrice()) + "</strike></html>");
            baseLbl.setFont(Theme.FONT_SMALL);
            baseLbl.setForeground(Theme.GREY_TEXT);
            pricePanel.add(saleLbl);
            pricePanel.add(baseLbl);
        } else {
            JLabel priceLbl = new JLabel(String.format("%.2f FCFA", product.getBasePrice()));
            priceLbl.setFont(Theme.FONT_PRICE);
            priceLbl.setForeground(Theme.PRIMARY);
            pricePanel.add(priceLbl);
        }
        info.add(pricePanel);
        info.add(Box.createVerticalStrut(6));

        // Stock indicator
        JLabel stockLbl = new JLabel(product.isInStock()
                ? "✔  En stock"
                : "✖  Rupture");
        stockLbl.setFont(Theme.FONT_SMALL);
        stockLbl.setForeground(product.isInStock() ? Theme.SUCCESS : Theme.ERROR);
        stockLbl.setAlignmentX(LEFT_ALIGNMENT);
        info.add(stockLbl);
        info.add(Box.createVerticalStrut(8));

        // Bouton panier amélioré
        AccentButton cartBtn = new AccentButton(
                product.isInStock() ? "🛒  Ajouter au panier" : "Indisponible",
                Theme.CARD_W - 24, Theme.BTN_H_SM);
        cartBtn.setEnabled(product.isInStock());
        cartBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H_SM));
        cartBtn.setAlignmentX(LEFT_ALIGNMENT);
        cartBtn.addActionListener(e -> { if (onAddToCart != null) onAddToCart.accept(product); });
        info.add(cartBtn);

        add(imgPanel, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);

        // ── Animations hover ──────────────────────────────────────────────
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { animateHover(true); }
            @Override public void mouseExited (MouseEvent e) { animateHover(false); }
            @Override public void mouseClicked(MouseEvent e) { if (onDetails != null) onDetails.accept(product); }
        });
    }

    private void animateHover(boolean enter) {
        if (hoverTimer != null) hoverTimer.stop();
        hoverTimer = new Timer(16, null);
        hoverTimer.addActionListener(e -> {
            hoverProgress = enter
                    ? Math.min(1f, hoverProgress + 0.08f)
                    : Math.max(0f, hoverProgress - 0.08f);
            repaint();
            if ((enter && hoverProgress >= 1f) || (!enter && hoverProgress <= 0f))
                ((Timer) e.getSource()).stop();
        });
        hoverTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        float elevate = hoverProgress;

        // Ombre portée dynamique
        int shadowSize  = (int)(2 + elevate * 6);
        int shadowAlpha = (int)(20 + elevate * 30);
        for (int i = shadowSize; i > 0; i--) {
            g2.setColor(new Color(0x1A, 0x3C, 0x5E, shadowAlpha / i));
            g2.fillRoundRect(i, i + 2, w - i, h - i, 16, 16);
        }

        // Fond blanc arrondi
        g2.setColor(Theme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(0, 0, w - shadowSize - 1,
                h - shadowSize - 1, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));

        // Bordure accent dorée au survol
        if (hoverProgress > 0.1f) {
            float alpha = Math.min(1f, hoverProgress * 1.5f);
            g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(),
                    Theme.ACCENT.getBlue(), (int)(alpha * 200)));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f,
                    w - shadowSize - 2.5f, h - shadowSize - 2.5f,
                    Theme.BORDER_RADIUS - 1, Theme.BORDER_RADIUS - 1));
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // Couleurs de fond image par catégorie
    private Color getCategoryColor(Product.Category cat) {
        if (cat == null) return new Color(0xE8, 0xEE, 0xF6);
        switch (cat) {
            case ELECTRONIQUE: return new Color(0xBF, 0xDB, 0xFF);
            case MODE:         return new Color(0xFF, 0xD6, 0xEE);
            case MAISON:       return new Color(0xC8, 0xF0, 0xC8);
            case SPORT:        return new Color(0xFF, 0xE4, 0xC0);
            case BEAUTE:       return new Color(0xFF, 0xD0, 0xED);
            case ALIMENTATION: return new Color(0xD4, 0xF5, 0xB4);
            case LIVRES:       return new Color(0xFF, 0xF0, 0xB0);
            case JOUETS:       return new Color(0xF0, 0xD0, 0xFF);
            case AUTOMOBILES:  return new Color(0xC8, 0xE8, 0xFF);
            case SANTE:        return new Color(0xD0, 0xF0, 0xE0);
            default:           return new Color(0xEA, 0xEE, 0xF4);
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
            case ALIMENTATION: return "🥗";
            case LIVRES:       return "📚";
            case JOUETS:       return "🎮";
            case AUTOMOBILES:  return "🚗";
            case SANTE:        return "💊";
            default:           return "📦";
        }
    }
}
